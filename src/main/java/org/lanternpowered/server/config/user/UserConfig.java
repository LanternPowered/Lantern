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
package org.lanternpowered.server.config.user;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lanternpowered.server.config.ConfigBase;
import org.spongepowered.api.profile.GameProfile;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.objectmapping.Setting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class UserConfig<T extends UserEntry> extends ConfigBase implements UserStorage<T> {

    public static final ConfigurationOptions DEFAULT_OPTIONS = ConfigurationOptions.defaults();

    @Setting(value = "entries")
    private volatile List<T> entries = Lists.newArrayList();

    private volatile Map<UUID, T> byUUID;
    private volatile Map<String, T> byName;

    public UserConfig(Path path) throws IOException {
        super(path);
    }

    public UserConfig(Path path, ConfigurationOptions options) throws IOException {
        super(path, options);
    }

    protected List<T> getEntriesList() {
        return this.entries;
    }

    protected void setEntriesList(List<T> entries) {
        this.entries = entries;
    }

    private List<T> getEntries() {
        List<T> entries = this.getEntriesList();
        if (entries instanceof CopyOnWriteArrayList) {
            return entries;
        }
        entries = Lists.newCopyOnWriteArrayList(entries);
        this.setEntriesList(entries);
        return entries;
    }

    private Map<String, T> getByName() {
        if (this.byName == null) {
            this.byName = Maps.newConcurrentMap();
            for (T entry : this.getEntries()) {
                this.byName.put(entry.getProfile().getName().toLowerCase(), entry);
            }
        }
        return this.byName;
    }

    private Map<UUID, T> getByUUID() {
        if (this.byUUID == null) {
            this.byUUID = Maps.newConcurrentMap();
            for (T entry : this.getEntries()) {
                this.byUUID.put(entry.getProfile().getUniqueId(), entry);
            }
        }
        return this.byUUID;
    }

    @Override
    public Optional<T> getEntryByUUID(UUID uniqueId) {
        return Optional.ofNullable(this.getByUUID().get(uniqueId));
    }

    @Override
    public Optional<T> getEntryByName(String username) {
        return Optional.ofNullable(this.getByName().get(username.toLowerCase()));
    }

    @Override
    public Optional<T> getEntry(GameProfile gameProfile) {
        return Optional.ofNullable(this.getByUUID().get(gameProfile.getUniqueId()));
    }

    @Override
    public void addEntry(T entry) {
        final GameProfile gameProfile = entry.getProfile();
        T entry0 = this.getByUUID().remove(gameProfile.getUniqueId());
        if (entry0 != null) {
            this.getEntries().remove(entry0);
        }
        this.getEntries().add(entry);
        this.getByUUID().put(gameProfile.getUniqueId(), entry);
        this.getByName().put(gameProfile.getName().toLowerCase(), entry);
    }

    @Override
    public boolean removeEntry(UUID uniqueId) {
        T entry = this.getByUUID().remove(uniqueId);
        if (entry != null) {
            return this.getEntries().remove(entry);
        }
        return false;
    }
}
