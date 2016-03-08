/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.server.profile;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.lanternpowered.server.config.ConfigBase;
import org.lanternpowered.server.game.LanternGame;
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
                LanternGame.log().warn("An error occurred while loading the profile cache file.", e);
            }
        } catch (IOException e) {
            LanternGame.log().warn("An error occurred while instantiating the profile cache file.", e);
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
                    entry.gameProfile.getName().ifPresent(n -> byName.put(n.toLowerCase(), entry));
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
            LanternGame.log().warn("An error occurred while saving the profile cache file.", e);
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
    public Optional<GameProfile> getById(UUID uniqueId) {
        ProfileCacheEntry entry = this.byUUID.get(checkNotNull(uniqueId, "uniqueId"));
        if (entry != null) {
            if (entry.isExpired()) {
                this.byUUID.remove(uniqueId);
                entry.gameProfile.getName().ifPresent(this.byName::remove);
            } else {
                return Optional.of(entry.gameProfile);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<GameProfile> lookupById(UUID uniqueId) {
        try {
            GameProfile gameProfile = GameProfileQuery.queryProfileByUUID(uniqueId, false);
            this.add(gameProfile, true, null);
            return Optional.of(gameProfile);
        } catch (IOException e) {
            LanternGame.log().warn("An error occurred while retrieving game profile data.", e);
        } catch (ProfileNotFoundException ignored) {
        }
        return Optional.empty();
    }

    @Override
    public Optional<GameProfile> getByName(String name) {
        ProfileCacheEntry entry = this.byName.get(checkNotNull(name, "name"));
        if (entry != null) {
            if (entry.isExpired()) {
                this.byUUID.remove(entry.gameProfile.getUniqueId());
                this.byName.remove(name);
            } else {
                return Optional.of(entry.gameProfile);
            }
        }
        return Optional.empty();
    }

    @Override
    public Map<String, Optional<GameProfile>> lookupByNames(Iterable<String> names) {
        Map<String, Optional<GameProfile>> result = Maps.newHashMap();

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

        try {
            Map<String, UUID> namesResult = GameProfileQuery.queryUUIDByName(names0);
            names0.forEach(name -> {
                if (namesResult.containsKey(name)) {
                    result.put(name, this.lookupById(namesResult.get(name)));
                } else {
                    result.put(name, Optional.empty());
                }
            });
        } catch (IOException e) {
            LanternGame.log().warn("An error occurred while retrieving game profile data.", e);
        }

        return result;
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
            LanternGame.log().warn("An error occurred while retrieving game profile data.", e);
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
            }
            return Optional.of(gameProfile);
        } catch (IOException e) {
            LanternGame.log().warn("An error occurred while retrieving game profile data.", e);
        } catch (ProfileNotFoundException ignored) {
        }
        return Optional.empty();
    }

    @Override
    public Collection<GameProfile> getProfiles() {
        return this.byUUID.values().stream().map(entry -> entry.gameProfile).collect(GuavaCollectors.toImmutableList());
    }
}
