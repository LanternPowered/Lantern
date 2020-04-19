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
package org.lanternpowered.server.profile;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.lanternpowered.server.config.ConfigBase;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.GameProfileCache;
import org.spongepowered.api.profile.ProfileNotFoundException;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class LanternGameProfileCache implements GameProfileCache {

    // The duration before a profile expires
    private static final Duration EXPIRATION_DURATION = Duration.ofDays(30);

    // Lookup by name
    private final Map<String, ProfileCacheEntry> byName = new ConcurrentHashMap<>();

    // Lookup by unique id
    private final Map<UUID, ProfileCacheEntry> byUUID = new ConcurrentHashMap<>();

    // The cache file
    private final ProfileCacheFile cacheFile;

    LanternGameProfileCache(Path cacheFile) {
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

        ProfileCacheFile(Path path) throws IOException {
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

    @SuppressWarnings("NullableProblems")
    @ConfigSerializable
    private static class ProfileCacheEntry {

        @Setting(value = "profile")
        private LanternGameProfile gameProfile;

        @Setting(value = "expiration-date")
        private Instant expirationDate;

        private ProfileCacheEntry() {
        }

        ProfileCacheEntry(GameProfile profile, Instant expirationDate) {
            this.gameProfile = (LanternGameProfile) profile;
            this.expirationDate = expirationDate;
        }

        boolean isExpired() {
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

    @Override
    public boolean add(GameProfile profile, boolean overwrite, @Nullable Instant expiry) {
        final UUID uuid = checkNotNull(profile, "profile").getUniqueId();
        if (!overwrite && this.byUUID.containsKey(uuid)) {
            return false;
        }
        if (expiry == null) {
            expiry = calculateDefaultExpirationDate();
        }
        final ProfileCacheEntry entry = new ProfileCacheEntry(profile, expiry);
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
        final ProfileCacheEntry entry = this.byUUID.get(checkNotNull(uniqueId, "uniqueId"));
        if (entry != null) {
            if (entry.isExpired()) {
                this.byUUID.remove(uniqueId, entry);
                entry.gameProfile.getName().ifPresent(name -> {
                    final ProfileCacheEntry entry1 = this.byName.get(name);
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
        final ImmutableMap.Builder<UUID, Optional<GameProfile>> builder = ImmutableMap.builder();
        uniqueIds.forEach(uniqueId -> builder.put(uniqueId, getById(uniqueId)));
        return builder.build();
    }

    @Override
    public Optional<GameProfile> lookupById(UUID uniqueId) {
        try {
            final GameProfile gameProfile = GameProfileQuery.queryProfileByUUID(uniqueId, true);
            add(gameProfile, true, (Instant) null);
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
        final ImmutableMap.Builder<UUID, Optional<GameProfile>> builder = ImmutableMap.builder();
        uniqueIds.forEach(uniqueId -> builder.put(uniqueId, lookupById(uniqueId)));
        return builder.build();
    }

    @Override
    public Optional<GameProfile> getOrLookupById(UUID uniqueId) {
        final Optional<GameProfile> gameProfile = getById(checkNotNull(uniqueId, "uniqueId"));
        if (!gameProfile.isPresent()) {
            return lookupById(uniqueId);
        }
        return gameProfile;
    }

    @Override
    public Map<UUID, Optional<GameProfile>> getOrLookupByIds(Iterable<UUID> uniqueIds) {
        checkNotNull(uniqueIds, "uniqueIds");
        final ImmutableMap.Builder<UUID, Optional<GameProfile>> builder = ImmutableMap.builder();
        uniqueIds.forEach(uniqueId -> builder.put(uniqueId, getOrLookupById(uniqueId)));
        return builder.build();
    }

    @Override
    public Optional<GameProfile> getByName(String name) {
        final ProfileCacheEntry entry = this.byName.get(checkNotNull(name, "name"));
        if (entry != null) {
            if (entry.isExpired()) {
                final UUID uniqueId = entry.gameProfile.getUniqueId();
                final ProfileCacheEntry entry1 = this.byUUID.get(uniqueId);
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
        final ImmutableMap.Builder<String, Optional<GameProfile>> builder = ImmutableMap.builder();
        names.forEach(name -> builder.put(name, getByName(name)));
        return builder.build();
    }

    @Override
    public Map<String, Optional<GameProfile>> lookupByNames(Iterable<String> names) {
        checkNotNull(names, "names");
        final ImmutableMap.Builder<String, Optional<GameProfile>> result = ImmutableMap.builder();
        lookupByNamesInto(result, Lists.newArrayList(names));
        return result.build();
    }

    @Override
    public Optional<GameProfile> getOrLookupByName(String name) {
        final Optional<GameProfile> gameProfile = getByName(checkNotNull(name, "name"));
        if (!gameProfile.isPresent()) {
            return lookupByName(name);
        }
        return gameProfile;
    }

    @Override
    public Map<String, Optional<GameProfile>> getOrLookupByNames(Iterable<String> names) {
        checkNotNull(names, "names");
        final ImmutableMap.Builder<String, Optional<GameProfile>> result = ImmutableMap.builder();

        final List<String> names0 = Lists.newArrayList(names);
        final Iterator<String> it = names0.iterator();
        while (it.hasNext()) {
            final String name = it.next();
            final Optional<GameProfile> gameProfile = getByName(name);
            if (gameProfile.isPresent()) {
                result.put(name, gameProfile);
                it.remove();
            }
        }

        lookupByNamesInto(result, names0);
        return result.build();
    }

    private void lookupByNamesInto(ImmutableMap.Builder<String, Optional<GameProfile>> builder, List<String> names) {
        try {
            final Map<String, UUID> namesResult = GameProfileQuery.queryUUIDByName(names);
            names.forEach(name -> {
                if (namesResult.containsKey(name)) {
                    builder.put(name, lookupById(namesResult.get(name)));
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
            final Map<String, UUID> result = GameProfileQuery.queryUUIDByName(
                    Collections.singletonList(checkNotNull(name, "name")));
            if (result.isEmpty()) {
                return Optional.empty();
            }
            return lookupById(result.get(name));
        } catch (IOException e) {
            Lantern.getLogger().warn("An error occurred while retrieving game profile data.", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<GameProfile> fillProfile(GameProfile profile, boolean signed) {
        checkNotNull(profile, "profile");
        final Optional<GameProfile> cachedProfile = getById(profile.getUniqueId());
        if (!cachedProfile.isPresent()) {
            return Optional.empty();
        }
        cachedProfile.get().getName().ifPresent(((LanternGameProfile) profile)::setName);
        if (signed) {
            profile.getPropertyMap().putAll(cachedProfile.get().getPropertyMap());
        } else {
            // Add all the properties without the signature when not signed is requested
            for (Map.Entry<String, ProfileProperty> entry : cachedProfile.get().getPropertyMap().entries()) {
                profile.getPropertyMap().put(entry.getKey(), ((LanternProfileProperty) entry.getValue()).withoutSignature());
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<GameProfile> getProfiles() {
        final ImmutableList.Builder<GameProfile> builder = ImmutableList.builder();
        final Iterator<Map.Entry<UUID, ProfileCacheEntry>> it = this.byUUID.entrySet().iterator();
        while (it.hasNext()) {
            final ProfileCacheEntry entry = it.next().getValue();
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
        return getProfiles().stream()
                .filter(profile -> profile.getName().isPresent() &&
                        profile.getName().get().toLowerCase(Locale.ROOT).startsWith(search))
                .collect(ImmutableSet.toImmutableSet());
    }
}
