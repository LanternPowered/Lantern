/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.config.user;

import com.google.common.collect.ImmutableList;
import ninja.leaping.configurate.ConfigurationOptions;
import org.lanternpowered.server.config.ConfigBase;
import org.spongepowered.api.profile.GameProfile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class UserConfig<T extends UserEntry> extends ConfigBase implements UserStorage<T> {

    private final Map<UUID, T> byUUID = new ConcurrentHashMap<>();
    private final Map<String, T> byName = new ConcurrentHashMap<>();

    public UserConfig(Path path, boolean hocon) throws IOException {
        super(path, hocon);
    }

    public UserConfig(Path path, ConfigurationOptions options, boolean hocon) throws IOException {
        super(path, options, hocon);
    }

    protected abstract List<T> getBackingList();

    @Override
    public void save() throws IOException {
        synchronized (this) {
            getBackingList().clear();
            for (T entry : this.byUUID.values()) {
                getBackingList().add(entry);
            }
            super.save();
        }
    }

    @Override
    public void load() throws IOException {
        synchronized (this) {
            super.load();
            this.byUUID.clear();
            this.byName.clear();
            for (T entry : getBackingList()) {
                this.byUUID.put(entry.getProfile().getUniqueId(), entry);
                final Optional<String> optName = entry.getProfile().getName();
                optName.ifPresent(s -> this.byName.put(s.toLowerCase(), entry));
            }
        }
    }

    @Override
    public Optional<T> getEntryByUUID(UUID uniqueId) {
        return Optional.ofNullable(this.byUUID.get(uniqueId));
    }

    @Override
    public Optional<T> getEntryByName(String username) {
        return Optional.ofNullable(this.byName.get(username.toLowerCase()));
    }

    @Override
    public Optional<T> getEntryByProfile(GameProfile gameProfile) {
        return getEntryByUUID(gameProfile.getUniqueId());
    }

    @Override
    public void addEntry(T entry) {
        final GameProfile gameProfile = entry.getProfile();
        this.byUUID.put(gameProfile.getUniqueId(), entry);
        final Optional<String> optName = entry.getProfile().getName();
        optName.ifPresent(s -> this.byName.put(s.toLowerCase(), entry));
    }

    @Override
    public boolean removeEntry(UUID uniqueId) {
        T entry = this.byUUID.remove(uniqueId);
        if (entry != null) {
            final Optional<String> optName = entry.getProfile().getName();
            optName.ifPresent(s -> this.byName.remove(s.toLowerCase()));
            return true;
        }
        return false;
    }

    @Override
    public Collection<T> getEntries() {
        return ImmutableList.copyOf(this.byUUID.values());
    }

}
