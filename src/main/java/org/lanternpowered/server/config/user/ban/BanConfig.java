/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import org.lanternpowered.server.config.ConfigBase;
import org.lanternpowered.server.config.user.UserStorage;
import org.lanternpowered.server.util.collect.Lists2;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.Ban.Ip;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class BanConfig extends ConfigBase implements UserStorage<BanEntry>, BanService {

    public static final ConfigurationOptions OPTIONS;

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
    private List<BanEntry> entries = Lists.newArrayList();

    // A version of the entries list that allows concurrent operations
    private final List<BanEntry> entries0 = Lists2.createCopyOnWriteExpirableValueListWithPredicate(entry -> {
        final Optional<Instant> optExpirationDate = entry.getExpirationDate();
        return optExpirationDate.isPresent() && Instant.now().compareTo(optExpirationDate.get()) > 0;
    });

    public BanConfig(Path path) throws IOException {
        super(path, OPTIONS);
    }

    @Override
    public void save() throws IOException {
        synchronized (this.entries) {
            this.entries.clear();
            this.entries.addAll(this.entries0);
            super.save();
        }
    }

    @Override
    public void load() throws IOException {
        synchronized (this.entries) {
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
        return this.getEntryByUUID(gameProfile.getUniqueId());
    }

    @Override
    public void addEntry(BanEntry entry) {
        this.addBan(entry);
    }

    @Override
    public boolean removeEntry(UUID uniqueId) {
        Optional<BanEntry> ban = this.getEntryByUUID(uniqueId);
        if (ban.isPresent()) {
            return this.removeBan(ban.get());
        }
        return false;
    }

    @Override
    public Collection<? extends Ban> getBans() {
        return ImmutableList.copyOf(this.entries0);
    }

    @Override
    public Collection<Ban.Profile> getProfileBans() {
        return (Collection) this.entries0.stream().filter(e -> e instanceof Ban.Profile).collect(GuavaCollectors.toImmutableList());
    }

    @Override
    public Collection<Ban.Ip> getIpBans() {
        return (Collection) this.entries0.stream().filter(e -> e instanceof Ban.Ip).collect(GuavaCollectors.toImmutableList());
    }

    @Override
    public Optional<Ban.Profile> getBanFor(GameProfile profile) {
        return (Optional) this.getEntryByProfile(profile);
    }

    @Override
    public Optional<Ip> getBanFor(InetAddress address) {
        final String address0 = address.getHostAddress();
        return (Optional) this.entries0.stream().filter(e -> e instanceof BanEntry.Ip &&
                ((BanEntry.Ip) e).getAddress().getHostAddress().equalsIgnoreCase(address0)).findFirst();
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
        Optional<Ban.Profile> ban = this.getBanFor(profile);
        if (ban.isPresent()) {
            this.entries0.remove(ban.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean pardon(InetAddress address) {
        Optional<Ban.Ip> ban = this.getBanFor(address);
        if (ban.isPresent()) {
            this.entries0.remove(ban.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean removeBan(Ban ban) {
        return this.entries0.remove(ban);
    }

    @Override
    public Optional<? extends Ban> addBan(Ban ban) {
        Optional<Ban> oldBan;
        if (ban instanceof Ban.Ip) {
            oldBan = (Optional) this.getBanFor(((Ban.Ip) ban).getAddress());
        } else {
            oldBan = (Optional) this.getBanFor(((Ban.Profile) ban).getProfile());
        }
        if (oldBan.isPresent()) {
            this.entries0.remove(oldBan.get());
        }
        this.entries0.add((BanEntry) ban);
        return oldBan;
    }

    @Override
    public boolean hasBan(Ban ban) {
        return this.entries0.contains(ban);
    }

    @Override
    public Collection<BanEntry> getEntries() {
        return ImmutableList.copyOf(this.entries0);
    }

}
