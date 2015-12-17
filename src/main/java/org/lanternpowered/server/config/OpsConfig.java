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
package org.lanternpowered.server.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.lanternpowered.server.profile.LanternGameProfile;
import org.spongepowered.api.profile.GameProfile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

public class OpsConfig extends ConfigBase {

    @Setting(value = "entries")
    private List<Entry> entries = Lists.newArrayList();

    private volatile Map<UUID, Entry> byUUID;
    private volatile Map<String, Entry> byName;

    public OpsConfig(Path path) throws IOException {
        super(path);
    }

    private Map<String, Entry> getByName() {
        if (this.byName == null) {
            this.byName = Maps.newConcurrentMap();
            for (Entry entry : this.entries) {
                this.byName.put(entry.profile.getName().toLowerCase(), entry);
            }
        }
        return this.byName;
    }

    private Map<UUID, Entry> getByUUID() {
        if (this.byUUID == null) {
            this.byUUID = Maps.newConcurrentMap();
            for (Entry entry : this.entries) {
                this.byUUID.put(entry.profile.getUniqueId(), entry);
            }
        }
        return this.byUUID;
    }

    /**
     * Gets the op entry for the specified unique id.
     * 
     * @param uniqueId the unique id
     * @return the entry if present, otherwise {@link Optional#empty()}
     */
    public Optional<Entry> getEntryByUUID(UUID uniqueId) {
        return Optional.ofNullable(this.getByUUID().get(uniqueId));
    }

    /**
     * Gets the op entry for the specified username.
     * 
     * @param username the username
     * @return the entry if present, otherwise {@link Optional#empty()}
     */
    public Optional<Entry> getEntryByName(String username) {
        return Optional.ofNullable(this.getByName().get(username.toLowerCase()));
    }

    /**
     * Gets the op entry for the specified game profile.
     * 
     * @param gameProfile the game profile
     * @return the entry if present, otherwise {@link Optional#empty()}
     */
    public Optional<Entry> getEntry(GameProfile gameProfile) {
        return Optional.ofNullable(this.getByUUID().get(gameProfile.getUniqueId()));
    }

    /**
     * Adds the op entry and replaces any present ones.
     * 
     * @param entry the entry
     */
    public void addEntry(Entry entry) {
        Entry entry0 = this.getByUUID().remove(entry.profile.getUniqueId());
        if (entry0 != null) {
            this.entries.remove(entry0);
        }
        this.entries.add(entry);
        this.getByUUID().put(entry.profile.getUniqueId(), entry);
        this.getByName().put(entry.profile.getName().toLowerCase(), entry);
    }

    /**
     * Removes the op entry for the specified player unique id.
     * 
     * @param uniqueId the unique id
     */
    public void removeEntry(UUID uniqueId) {
        Entry entry = this.getByUUID().remove(uniqueId);
        if (entry != null) {
            this.entries.remove(entry);
        }
    }

    @ConfigSerializable
    public static class Entry {

        @Setting(value = "profile")
        private LanternGameProfile profile;

        @Setting(value = "level")
        private int opLevel;

        protected Entry() {
        }

        public Entry(LanternGameProfile profile, int opLevel) {
            this.profile = profile;
            this.opLevel = opLevel;
        }

        public LanternGameProfile getProfile() {
            return this.profile;
        }

        public int getOpLevel() {
            return this.opLevel;
        }
    }
}
