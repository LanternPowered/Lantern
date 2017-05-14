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
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.lanternpowered.server.game.DirectoryKeys;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.GameProfileCache;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.profile.ProfileNotFoundException;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

@Singleton
public final class LanternGameProfileManager implements GameProfileManager {

    private static final String FILE_NAME = "profile-cache.json";

    // The game profile cache
    private GameProfileCache gameProfileCache;

    // The default game profile cache
    private final LanternGameProfileCache defaultGameProfileCache;

    @Inject
    public LanternGameProfileManager(@Named(DirectoryKeys.CONFIG) Path configDirectory) {
        this.defaultGameProfileCache = new LanternGameProfileCache(configDirectory.resolve(FILE_NAME));
        this.gameProfileCache = this.defaultGameProfileCache;
    }

    @Override
    public GameProfile createProfile(UUID uniqueId, @Nullable String name) {
        return new LanternGameProfile(uniqueId, name);
    }

    @Override
    public ProfileProperty createProfileProperty(String name, String value, @Nullable String signature) {
        return new LanternProfileProperty(name, value, signature);
    }

    private GameProfile getById(UUID uniqueId, boolean useCache, boolean signed) throws Exception {
        if (useCache) {
            final Optional<GameProfile> optProfile = this.gameProfileCache.getOrLookupById(uniqueId);
            if (optProfile.isPresent()) {
                return optProfile.get();
            }
        }
        return GameProfileQuery.queryProfileByUUID(uniqueId, signed);
    }

    @Override
    public CompletableFuture<GameProfile> get(UUID uniqueId, boolean useCache) {
        checkNotNull(uniqueId, "uniqueId");
        return Lantern.getScheduler().submitAsyncTask(() -> getById(uniqueId, useCache, true));
    }

    @Override
    public CompletableFuture<Collection<GameProfile>> getAllById(Iterable<UUID> uniqueIds, boolean useCache) {
        checkNotNull(uniqueIds, "uniqueIds");
        return Lantern.getScheduler().submitAsyncTask(() -> {
            final ImmutableList.Builder<GameProfile> builder = ImmutableList.builder();
            for (UUID uniqueId : uniqueIds) {
                builder.add(getById(uniqueId, useCache, true));
            }
            return builder.build();
        });
    }

    @Override
    public CompletableFuture<GameProfile> get(String name, boolean useCache) {
        checkNotNull(name, "name");
        return Lantern.getScheduler().submitAsyncTask(() -> {
            if (useCache) {
                final Optional<GameProfile> optProfile = this.gameProfileCache.getOrLookupByName(name);
                if (optProfile.isPresent()) {
                    return optProfile.get();
                }
                throw new ProfileNotFoundException("Unable to find a profile for the name: " + name);
            }
            final Map<String, UUID> result = GameProfileQuery.queryUUIDByName(Collections.singletonList(name));
            if (!result.containsKey(name)) {
                throw new ProfileNotFoundException("Unable to find a profile for the name: " + name);
            }
            return GameProfileQuery.queryProfileByUUID(result.get(name), true);
        });
    }

    @Override
    public CompletableFuture<Collection<GameProfile>> getAllByName(Iterable<String> names, boolean useCache) {
        checkNotNull(names, "names");
        return Lantern.getScheduler().submitAsyncTask(() -> {
            if (useCache) {
                final Map<String, Optional<GameProfile>> profiles = this.gameProfileCache.getOrLookupByNames(names);
                return profiles.values().stream().filter(Optional::isPresent).map(Optional::get).collect(ImmutableSet.toImmutableSet());
            }
            final ImmutableList.Builder<GameProfile> builder = ImmutableList.builder();
            final Map<String, UUID> results = GameProfileQuery.queryUUIDByName(names);
            for (String name : names) {
                if (!results.containsKey(name)) {
                    throw new ProfileNotFoundException("Unable to find a profile with the name: " + name);
                }
                builder.add(getById(results.get(name), false, true));
            }
            return builder.build();
        });
    }

    @Override
    public CompletableFuture<GameProfile> fill(GameProfile profile, boolean signed, boolean useCache) {
        checkNotNull(profile, "profile");
        return Lantern.getScheduler().submitAsyncTask(() -> {
            if (useCache) {
                // Load the profile into the cache
                this.gameProfileCache.getOrLookupById(profile.getUniqueId());
                final Optional<GameProfile> optProfile = this.gameProfileCache.fillProfile(profile, signed);
                if (optProfile.isPresent()) {
                    return optProfile.get();
                }
                throw new ProfileNotFoundException("Failed to find a profile with the uuid: " + profile.getUniqueId());
            }
            final GameProfile gameProfile = getById(profile.getUniqueId(), false, signed);
            ((LanternGameProfile) profile).setName(gameProfile.getName().get());
            profile.getPropertyMap().putAll(gameProfile.getPropertyMap());
            return profile;
        });
    }

    @Override
    public GameProfileCache getCache() {
        return this.gameProfileCache;
    }

    @Override
    public void setCache(GameProfileCache cache) {
        this.gameProfileCache = checkNotNull(cache, "cache");
    }

    @Override
    public LanternGameProfileCache getDefaultCache() {
        return this.defaultGameProfileCache;
    }
}
