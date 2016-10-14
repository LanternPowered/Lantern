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
package org.lanternpowered.server.profile;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.lanternpowered.server.config.ConfigBase;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.GameProfileCache;
import org.spongepowered.api.profile.ProfileNotFoundException;
import org.spongepowered.api.util.GuavaCollectors;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class LanternGameProfileCache implements GameProfileCache {

    // The duration before a profile expires
    private static final Duration EXPIRATION_DURATION = Duration.ofDays(30);

    // Lookup by name
    private final Map<String, ProfileCacheEntry> byName = Maps.newConcurrentMap();

    // Lookup by unique id
    private final Map<UUID, ProfileCacheEntry> byUUID = Maps.newConcurrentMap();

    // The cache file
    private final ProfileCacheFile cacheFile;

    public LanternGameProfileCache(Path cacheFile) {
        ProfileCacheFile cache = null;
        try {
            cache = new ProfileCacheFile(cacheFile);
            try {
                cache.load();
            } catch (IOException e) {
                Lantern.getLogger().warn("An error occurred while loading the profile cache file.", e);
            }
        } catch (IOException e) {
            Lantern.getLogger().warn("An error occurred while instantiating the profile cache file.", e);
        }
        this.cacheFile = cache;
    }

    private class ProfileCacheFile extends ConfigBase {

        @Setting(value = "entries")
        private List<ProfileCacheEntry> entries = new ArrayList<>();

        public ProfileCacheFile(Path path) throws IOException {
            super(path, false);
        }

        @Override
        public void save() throws IOException {
            synchronized (this) {
                this.entries.clear();
                this.entries.addAll(byUUID.values().stream().collect(Collectors.toList()));
                this.entries.addAll(byName.values().stream().filter(e -> !this.entries.contains(e)).collect(Collectors.toList()));
                super.save();
            }
        }

        @Override
        public void load() throws IOException {
            synchronized (this) {
                super.load();
                byUUID.clear();
                byName.clear();
                this.entries.stream().filter(e -> !e.isExpired()).forEach(entry -> {
                    byUUID.put(entry.gameProfile.getUniqueId(), entry);
                    entry.gameProfile.getName().ifPresent(n -> byName.put(n, entry));
                });
            }
        }
    }

    @ConfigSerializable
    private static class ProfileCacheEntry {

        @Setting(value = "profile")
        private LanternGameProfile gameProfile;
        @Setting(value = "expiration-date")
        private Instant expirationDate;
        @Setting(value = "signed")
        private boolean signed;

        private ProfileCacheEntry() {
        }

        public ProfileCacheEntry(GameProfile profile, Instant expirationDate) {
            this.gameProfile = (LanternGameProfile) profile;
            this.expirationDate = expirationDate;
        }

        public boolean isExpired() {
            return Instant.now().compareTo(this.expirationDate) > 0;
        }
    }

    private Instant calculateDefaultExpirationDate() {
        return Instant.now().plus(EXPIRATION_DURATION);
    }

    /**
     * Saves the game profile cache.
     */
    public void save() {
        try {
            this.cacheFile.save();
        } catch (IOException e) {
            Lantern.getLogger().warn("An error occurred while saving the profile cache file.", e);
        }
    }

    /**
     * TODO: SpongeAPI should use Instant?
     */
    @Override
    public boolean add(GameProfile profile, boolean overwrite, @Nullable Date expiry) {
        UUID uuid = checkNotNull(profile, "profile").getUniqueId();
        if (overwrite && this.byUUID.containsKey(uuid)) {
            return false;
        }
        Instant expirationDate;
        if (expiry != null) {
            expirationDate = expiry.toInstant();
        } else {
            expirationDate = this.calculateDefaultExpirationDate();
        }
        ProfileCacheEntry entry = new ProfileCacheEntry(profile, expirationDate);
        this.byUUID.put(uuid, entry);
        profile.getName().ifPresent(name -> this.byName.put(name, entry));
        return true;
    }

    @Override
    public boolean remove(GameProfile profile) {
        boolean flag = this.byUUID.remove(profile.getUniqueId()) != null;
        if (profile.getName().isPresent()) {
            flag = this.byName.remove(profile.getName().get()) != null || flag;
        }
        return flag;
    }

    @Override
    public Collection<GameProfile> remove(Iterable<GameProfile> profiles) {
        final ImmutableList.Builder<GameProfile> removed = ImmutableList.builder();
        for (GameProfile profile : profiles) {
            if (this.remove(profile)) {
                removed.add(profile);
            }
        }
        return removed.build();
    }

    @Override
    public void clear() {
        this.byName.clear();
        this.byUUID.clear();
    }

    @Override
    public Optional<GameProfile> getById(UUID uniqueId) {
        ProfileCacheEntry entry = this.byUUID.get(checkNotNull(uniqueId, "uniqueId"));
        if (entry != null) {
            if (entry.isExpired()) {
                this.byUUID.remove(uniqueId, entry);
                entry.gameProfile.getName().ifPresent(name -> {
                    ProfileCacheEntry entry1 = this.byName.get(name);
                    if (entry == entry1) {
                        this.byName.remove(name, entry);
                    }
                });
            } else {
                return Optional.of(entry.gameProfile);
            }
        }
        return Optional.empty();
    }

    @Override
    public Map<UUID, Optional<GameProfile>> getByIds(Iterable<UUID> uniqueIds) {
        checkNotNull(uniqueIds, "uniqueIds");
        ImmutableMap.Builder<UUID, Optional<GameProfile>> builder = ImmutableMap.builder();
        uniqueIds.forEach(uniqueId -> builder.put(uniqueId, this.getById(uniqueId)));
        return builder.build();
    }

    @Override
    public Optional<GameProfile> lookupById(UUID uniqueId) {
        try {
            GameProfile gameProfile = GameProfileQuery.queryProfileByUUID(uniqueId, true);
            this.add(gameProfile, true, null);
            this.byUUID.get(gameProfile.getUniqueId()).signed = true;
            return Optional.of(gameProfile);
        } catch (IOException e) {
            Lantern.getLogger().warn("An error occurred while retrieving game profile data.", e);
        } catch (ProfileNotFoundException ignored) {
        }
        return Optional.empty();
    }

    @Override
    public Map<UUID, Optional<GameProfile>> lookupByIds(Iterable<UUID> uniqueIds) {
        checkNotNull(uniqueIds, "uniqueIds");
        ImmutableMap.Builder<UUID, Optional<GameProfile>> builder = ImmutableMap.builder();
        uniqueIds.forEach(uniqueId -> builder.put(uniqueId, this.lookupById(uniqueId)));
        return builder.build();
    }

    @Override
    public Optional<GameProfile> getOrLookupById(UUID uniqueId) {
        Optional<GameProfile> gameProfile = this.getById(checkNotNull(uniqueId, "uniqueId"));
        if (!gameProfile.isPresent()) {
            return this.lookupById(uniqueId);
        }
        return gameProfile;
    }

    @Override
    public Map<UUID, Optional<GameProfile>> getOrLookupByIds(Iterable<UUID> uniqueIds) {
        checkNotNull(uniqueIds, "uniqueIds");
        ImmutableMap.Builder<UUID, Optional<GameProfile>> builder = ImmutableMap.builder();
        uniqueIds.forEach(uniqueId -> builder.put(uniqueId, this.getOrLookupById(uniqueId)));
        return builder.build();
    }

    @Override
    public Optional<GameProfile> getByName(String name) {
        ProfileCacheEntry entry = this.byName.get(checkNotNull(name, "name"));
        if (entry != null) {
            if (entry.isExpired()) {
                UUID uniqueId = entry.gameProfile.getUniqueId();
                ProfileCacheEntry entry1 = this.byUUID.get(uniqueId);
                if (entry == entry1 || entry1.isExpired()) {
                    this.byUUID.remove(uniqueId, entry);
                }
                this.byName.remove(name, entry);
            } else {
                return Optional.of(entry.gameProfile);
            }
        }
        return Optional.empty();
    }

    @Override
    public Map<String, Optional<GameProfile>> getByNames(Iterable<String> names) {
        checkNotNull(names, "names");
        ImmutableMap.Builder<String, Optional<GameProfile>> builder = ImmutableMap.builder();
        names.forEach(name -> builder.put(name, this.getByName(name)));
        return builder.build();
    }

    @Override
    public Map<String, Optional<GameProfile>> lookupByNames(Iterable<String> names) {
        checkNotNull(names, "names");
        ImmutableMap.Builder<String, Optional<GameProfile>> result = ImmutableMap.builder();
        this.lookupByNamesInto(result, Lists.newArrayList(names));
        return result.build();
    }

    @Override
    public Optional<GameProfile> getOrLookupByName(String name) {
        Optional<GameProfile> gameProfile = this.getByName(checkNotNull(name, "name"));
        if (!gameProfile.isPresent()) {
            return this.lookupByName(name);
        }
        return gameProfile;
    }

    @Override
    public Map<String, Optional<GameProfile>> getOrLookupByNames(Iterable<String> names) {
        checkNotNull(names, "names");
        ImmutableMap.Builder<String, Optional<GameProfile>> result = ImmutableMap.builder();

        List<String> names0 = Lists.newArrayList(names);
        Iterator<String> it = names0.iterator();
        while (it.hasNext()) {
            String name = it.next();
            Optional<GameProfile> gameProfile = this.getByName(name);
            if (gameProfile.isPresent()) {
                result.put(name, gameProfile);
                it.remove();
            }
        }

        this.lookupByNamesInto(result, names0);
        return result.build();
    }

    private void lookupByNamesInto(ImmutableMap.Builder<String, Optional<GameProfile>> builder, List<String> names) {
        try {
            Map<String, UUID> namesResult = GameProfileQuery.queryUUIDByName(names);
            names.forEach(name -> {
                if (namesResult.containsKey(name)) {
                    builder.put(name, this.lookupById(namesResult.get(name)));
                } else {
                    builder.put(name, Optional.empty());
                }
            });
        } catch (IOException e) {
            Lantern.getLogger().warn("An error occurred while retrieving game profile data.", e);
        }
    }

    @Override
    public Optional<GameProfile> lookupByName(String name) {
        try {
            Map<String, UUID> result = GameProfileQuery.queryUUIDByName(Collections.singletonList(checkNotNull(name, "name")));
            if (result.isEmpty()) {
                return Optional.empty();
            }
            return this.lookupById(result.get(name));
        } catch (IOException e) {
            Lantern.getLogger().warn("An error occurred while retrieving game profile data.", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<GameProfile> fillProfile(GameProfile profile, boolean signed) {
        try {
            GameProfile gameProfile = GameProfileQuery.queryProfileByUUID(checkNotNull(profile, "profile").getUniqueId(), signed);
            profile.getPropertyMap().putAll(gameProfile.getPropertyMap());
            ProfileCacheEntry entry = this.byUUID.get(profile.getUniqueId());
            if (entry == null || entry.isExpired() || (!entry.signed && signed)) {
                this.add(gameProfile, true, null);
                this.byUUID.get(gameProfile.getUniqueId()).signed = true;
            }
            return Optional.of(gameProfile);
        } catch (IOException e) {
            Lantern.getLogger().warn("An error occurred while retrieving game profile data.", e);
        } catch (ProfileNotFoundException ignored) {
        }
        return Optional.empty();
    }

    @Override
    public Collection<GameProfile> getProfiles() {
        ImmutableList.Builder<GameProfile> builder = ImmutableList.builder();
        Iterator<Map.Entry<UUID, ProfileCacheEntry>> it = this.byUUID.entrySet().iterator();
        while (it.hasNext()) {
            ProfileCacheEntry entry = it.next().getValue();
            if (entry.isExpired()) {
                entry.gameProfile.getName().ifPresent(this.byName::remove);
                it.remove();
            } else {
                builder.add(entry.gameProfile);
            }
        }
        return builder.build();
    }

    @Override
    public Collection<GameProfile> match(String name) {
        final String search = checkNotNull(name, "name").toLowerCase(Locale.ROOT);
        return this.getProfiles().stream().filter(profile -> profile.getName().isPresent())
                .filter(profile -> profile.getName().get().toLowerCase(Locale.ROOT).startsWith(search))
                .collect(GuavaCollectors.toImmutableSet());
    }
}
