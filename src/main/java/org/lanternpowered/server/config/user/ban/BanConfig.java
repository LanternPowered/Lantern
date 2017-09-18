/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.config.user.ban;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import org.lanternpowered.server.config.ConfigBase;
import org.lanternpowered.server.config.user.UserStorage;
import org.lanternpowered.server.event.CauseStack;
import org.lanternpowered.server.game.DirectoryKeys;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.service.CloseableService;
import org.lanternpowered.server.util.collect.Lists2;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.Ban.Ip;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
@SuppressWarnings({"unchecked", "rawtypes", "SuspiciousMethodCalls"})
public final class BanConfig extends ConfigBase implements UserStorage<BanEntry>, BanService, CloseableService {

    private static final String FILE_NAME = "bans.json";
    private static final ConfigurationOptions OPTIONS;

    static {
        final TypeSerializerCollection typeSerializers = DEFAULT_OPTIONS.getSerializers();
        final TypeSerializer banSerializer = new BanEntrySerializer();
        final TypeSerializer ipTypeSerializer = typeSerializers.get(TypeToken.of(BanEntry.Ip.class));
        final TypeSerializer userTypeSerializer = typeSerializers.get(TypeToken.of(BanEntry.Profile.class));
        typeSerializers.registerType(TypeToken.of(Ban.class), banSerializer)
                .registerType(TypeToken.of(BanEntry.class), banSerializer)
                .registerType(TypeToken.of(BanEntry.Ip.class), ipTypeSerializer)
                .registerType(TypeToken.of(Ban.Ip.class), ipTypeSerializer)
                .registerType(TypeToken.of(BanEntry.Profile.class), userTypeSerializer)
                .registerType(TypeToken.of(Ban.Profile.class), userTypeSerializer);
        OPTIONS = ConfigurationOptions.defaults().setSerializers(typeSerializers);
    }

    @Setting(value = "entries")
    private List<BanEntry> entries = new ArrayList<>();

    // A version of the entries list that allows concurrent operations
    private final List<BanEntry> entries0 = Collections.synchronizedList(Lists2.createExpirableValueListWithPredicate(BanEntry::isExpired));

    @Inject
    public BanConfig(@Named(DirectoryKeys.CONFIG) Path configFolder) throws IOException {
        super(configFolder.resolve(FILE_NAME), OPTIONS, false);
        // Load the config
        load();
    }

    @Override
    public void save() throws IOException {
        synchronized (this) {
            this.entries.clear();
            this.entries.addAll(this.entries0);
            super.save();
        }
    }

    @Override
    public void load() throws IOException {
        synchronized (this) {
            super.load();
            this.entries0.clear();
            this.entries0.addAll(this.entries);
        }
    }

    @Override
    public Optional<BanEntry> getEntryByUUID(UUID uniqueId) {
        return this.entries0.stream().filter(e -> e instanceof BanEntry.Profile &&
                ((BanEntry.Profile) e).getProfile().getUniqueId().equals(uniqueId)).findFirst();
    }

    @Override
    public Optional<BanEntry> getEntryByName(String username) {
        return this.entries0.stream().filter(e -> {
            if (!(e instanceof BanEntry.Profile)) {
                return false;
            }
            final Optional<String> optName = ((BanEntry.Profile) e).getProfile().getName();
            return optName.isPresent() && optName.get().equalsIgnoreCase(username);
        }).findFirst();
    }

    @Override
    public Optional<BanEntry> getEntryByProfile(GameProfile gameProfile) {
        return getEntryByUUID(gameProfile.getUniqueId());
    }

    /**
     * Gets a {@link BanEntry.Ip} for the specified {@link InetAddress}. May return
     * {@link Optional#empty()} if not found.
     *
     * @param address The address
     * @return The ban entry
     */
    public Optional<BanEntry> getEntryByIp(InetAddress address) {
        final String address0 = checkNotNull(address, "address").getHostAddress();
        return this.entries0.stream().filter(e -> e instanceof BanEntry.Ip &&
                ((BanEntry.Ip) e).getAddress().getHostAddress().equalsIgnoreCase(address0)).findFirst();
    }

    @Override
    public void addEntry(BanEntry entry) {
        this.addBan(entry);
    }

    @Override
    public boolean removeEntry(UUID uniqueId) {
        final Optional<BanEntry> ban = this.getEntryByUUID(checkNotNull(uniqueId, "uniqueId"));
        return ban.isPresent() && this.removeBan(ban.get());
    }

    @Override
    public Collection<? extends Ban> getBans() {
        return ImmutableList.copyOf(this.entries0);
    }

    @Override
    public Collection<Ban.Profile> getProfileBans() {
        return (Collection) this.entries0.stream().filter(e -> e instanceof Ban.Profile).collect(ImmutableList.toImmutableList());
    }

    @Override
    public Collection<Ban.Ip> getIpBans() {
        return (Collection) this.entries0.stream().filter(e -> e instanceof Ban.Ip).collect(ImmutableList.toImmutableList());
    }

    @Override
    public Optional<Ban.Profile> getBanFor(GameProfile profile) {
        return (Optional) this.getEntryByProfile(profile);
    }

    @Override
    public Optional<Ip> getBanFor(InetAddress address) {
        return (Optional) this.getEntryByIp(address);
    }

    @Override
    public boolean isBanned(GameProfile profile) {
        return this.getBanFor(profile).isPresent();
    }

    @Override
    public boolean isBanned(InetAddress address) {
        return this.getBanFor(address).isPresent();
    }

    @Override
    public boolean pardon(GameProfile profile) {
        final Optional<Ban.Profile> ban = getBanFor(checkNotNull(profile, "profile"));
        return ban.isPresent() && removeBan(ban.get());
    }

    @Override
    public boolean pardon(InetAddress address) {
        final Optional<Ban.Ip> ban = getBanFor(checkNotNull(address, "address"));
        return ban.isPresent() && removeBan(ban.get());
    }

    @Override
    public boolean removeBan(Ban ban) {
        checkNotNull(ban, "ban");
        if (this.entries0.remove(ban)) {
            final CauseStack causeStack = CauseStack.currentOrEmpty();
            // Post the pardon events
            final Event event;
            final Cause cause = causeStack.getCurrentCause();
            if (ban instanceof Ban.Ip) {
                event = SpongeEventFactory.createPardonIpEvent(cause, (Ban.Ip) ban);
            } else {
                final Ban.Profile profileBan = (Ban.Profile) ban;
                // Check if the pardoned player is online (not yet been kicked)
                final Optional<Player> optTarget = Sponge.getServer().getPlayer(profileBan.getProfile().getUniqueId());
                if (optTarget.isPresent()) {
                    event = SpongeEventFactory.createPardonUserEventTargetPlayer(cause, profileBan, optTarget.get(), optTarget.get());
                } else {
                    event = SpongeEventFactory.createPardonUserEvent(cause, profileBan, Lantern.getGame().getServiceManager()
                            .provideUnchecked(UserStorageService.class).getOrCreate(profileBan.getProfile()));
                }
            }
            // Just ignore for now the fact that they may be cancellable,
            // only the PardonIpEvent seems to be cancellable
            // TODO: Should they all be cancellable or none of them?
            Sponge.getEventManager().post(event);
            return true;
        }
        return false;
    }

    @Override
    public Optional<? extends Ban> addBan(Ban ban) {
        checkNotNull(ban, "ban");
        final Optional<Ban> oldBan;
        if (ban instanceof Ban.Ip) {
            oldBan = (Optional) getBanFor(((Ban.Ip) ban).getAddress());
        } else {
            oldBan = (Optional) getBanFor(((Ban.Profile) ban).getProfile());
        }
        oldBan.ifPresent(this.entries0::remove);
        this.entries0.add((BanEntry) ban);
        if (!oldBan.isPresent() || !oldBan.get().equals(ban)) {
            final CauseStack causeStack = CauseStack.currentOrEmpty();
            // Post the ban events
            final Event event;
            final Cause cause = causeStack.getCurrentCause();
            if (ban instanceof Ban.Ip) {
                event = SpongeEventFactory.createBanIpEvent(cause, (Ban.Ip) ban);
            } else {
                final Ban.Profile profileBan = (Ban.Profile) ban;
                // Check if the pardoned player is online (not yet been kicked)
                final Optional<Player> optTarget = Sponge.getServer().getPlayer(profileBan.getProfile().getUniqueId());
                if (optTarget.isPresent()) {
                    event = SpongeEventFactory.createBanUserEventTargetPlayer(cause, profileBan, optTarget.get(), optTarget.get());
                } else {
                    event = SpongeEventFactory.createBanUserEvent(cause, profileBan, Lantern.getGame().getServiceManager()
                            .provideUnchecked(UserStorageService.class).getOrCreate(profileBan.getProfile()));
                }
            }
            // Just ignore for now the fact that they may be cancellable,
            // only the PardonIpEvent seems to be cancellable
            // TODO: Should they all be cancellable or none of them?
            Sponge.getEventManager().post(event);
        }
        return oldBan;
    }

    @Override
    public boolean hasBan(Ban ban) {
        return this.entries0.contains(checkNotNull(ban, "ban"));
    }

    @Override
    public Collection<BanEntry> getEntries() {
        return ImmutableList.copyOf(this.entries0);
    }

    @Override
    public void close() {
        try {
            save();
        } catch (IOException e) {
            Lantern.getLogger().error("A error occurred while saving the bans config.", e);
        }
    }
}
