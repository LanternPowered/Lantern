/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;

import org.lanternpowered.server.config.ConfigBase;
import org.lanternpowered.server.config.user.UserStorage;
import org.lanternpowered.server.config.user.ban.BanEntry.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.util.ban.Ban;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

@SuppressWarnings({"unchecked", "rawtypes"})
public class BanConfig extends ConfigBase implements UserStorage<BanEntry> {

    public static final ConfigurationOptions OPTIONS;

    static {
        final TypeSerializerCollection typeSerializers = DEFAULT_OPTIONS.getSerializers();
        final TypeSerializer banSerializer = new BanEntrySerializer();
        final TypeSerializer ipTypeSerializer = typeSerializers.get(TypeToken.of(BanEntry.Ip.class));
        final TypeSerializer userTypeSerializer = typeSerializers.get(TypeToken.of(BanEntry.User.class));
        typeSerializers.registerType(TypeToken.of(Ban.class), banSerializer)
                .registerType(TypeToken.of(BanEntry.class), banSerializer)
                .registerType(TypeToken.of(BanEntry.Ip.class), ipTypeSerializer)
                .registerType(TypeToken.of(Ban.Ip.class), ipTypeSerializer)
                .registerType(TypeToken.of(BanEntry.User.class), userTypeSerializer)
                .registerType(TypeToken.of(Ban.User.class), userTypeSerializer);
        OPTIONS = ConfigurationOptions.defaults().setSerializers(typeSerializers);
    }

    @Setting(value = "entries")
    private List<BanEntry> entries = Lists.newArrayList();

    private final Map<String, BanEntry> byIp = Maps.newConcurrentMap();
    private final Map<UUID, BanEntry> byUUID = Maps.newConcurrentMap();
    private final Map<String, BanEntry> byName = Maps.newConcurrentMap();

    public BanConfig(Path path) throws IOException {
        super(path, OPTIONS);
    }

    @Override
    public void save() throws IOException {
        synchronized (this.entries) {
            this.entries.clear();
            for (BanEntry entry : this.byUUID.values()) {
                this.entries.add(entry);
            }
            for (BanEntry entry : this.byIp.values()) {
                this.entries.add(entry);
            }
            super.save();
        }
    }

    @Override
    public void load() throws IOException {
        synchronized (this.entries) {
            super.load();
            this.byIp.clear();
            this.byUUID.clear();
            this.byName.clear();
            for (BanEntry entry : this.entries) {
                if (entry instanceof BanEntry.User) {
                    final GameProfile gameProfile = ((BanEntry.User) entry).getProfile();
                    this.byUUID.put(gameProfile.getUniqueId(), entry);
                    this.byName.put(gameProfile.getName().toLowerCase(), entry);
                } else {
                    this.byIp.put(((BanEntry.Ip) entry).getAddress().getHostAddress(), entry);
                }
            }
        }
    }

    @Override
    public Optional<BanEntry> getEntryByUUID(UUID uniqueId) {
        return Optional.ofNullable(this.byUUID.get(uniqueId));
    }

    @Override
    public Optional<BanEntry> getEntryByName(String username) {
        return Optional.ofNullable(this.byName.get(username.toLowerCase()));
    }

    @Override
    public Optional<BanEntry> getEntryByProfile(GameProfile gameProfile) {
        return this.getEntryByUUID(gameProfile.getUniqueId());
    }

    /**
     * Gets the ban entry for the specified inet address.
     * 
     * @param address the inet address
     * @return the entry if present, otherwise {@link Optional#empty()}
     */
    public Optional<BanEntry> getEntryByAddress(InetAddress address) {
        return Optional.ofNullable(this.byIp.get(address.getHostAddress()));
    }

    @Override
    public void addEntry(BanEntry entry) {
        if (entry instanceof BanEntry.User) {
            final GameProfile gameProfile = ((BanEntry.User) entry).getProfile();
            this.byUUID.put(gameProfile.getUniqueId(), entry);
            this.byName.put(gameProfile.getName().toLowerCase(), entry);
        } else {
            this.byIp.put(((BanEntry.Ip) entry).getAddress().getHostAddress(), entry);
        }
    }

    @Override
    public boolean removeEntry(UUID uniqueId) {
        BanEntry.User entry = (User) this.byUUID.remove(uniqueId);
        if (entry != null) {
            this.byName.remove(entry.getProfile().getName().toLowerCase());
            return true;
        }
        return false;
    }

    /**
     * Removes a entry for the specified inet address.
     * 
     * @param address the inet address
     * @return whether a entry was removed
     */
    public boolean removeEntry(InetAddress address) {
        final String key = address.getHostAddress();
        return this.byIp.remove(key) != null;
    }
}
