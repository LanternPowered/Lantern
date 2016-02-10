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

import com.google.common.base.Equivalence;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.lanternpowered.server.util.UUIDHelper;
import org.lanternpowered.server.util.UniqueEvictingQueue;
import org.lanternpowered.server.util.collect.Maps2;
import org.lanternpowered.server.util.collect.expirable.ExpirableValue;
import org.lanternpowered.server.util.collect.expirable.ExpirableValueMap;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.profile.ProfileNotFoundException;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.util.GuavaCollectors;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

public final class LanternGameProfileManager implements GameProfileManager {

    private static final int CACHE_SIZE = 1000;

    private static final TemporalUnit EXPIRATION_TIME_UNIT = ChronoUnit.MONTHS;
    private static final int EXPIRATION_TIME = 1;

    private final AtomicInteger counter = new AtomicInteger();
    private final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool(
            runnable -> new Thread(runnable, "profile-resolver-" + this.counter.getAndIncrement())));

    // The gson instance
    private final Gson gson = new Gson();

    // Whether the two lookups should be linked
    private final ThreadLocal<Boolean> linkLookups = ThreadLocal.withInitial(() -> true);

    // Lookup by name
    private final ExpirableValueMap<String, GameProfile, CacheEntry> byName = Maps2.createConcurrentExpirableValueMap(
            (k, v) -> {
                final UUID uniqueId = v.getUniqueId();
                if (this.linkLookups.get() && this.cacheByUUID().containsKey(uniqueId)) {
                    return this.cacheByUUID().getBacking().get(uniqueId);
                }
                return new CacheEntry(v);
            });

    // Lookup by unique id
    private final ExpirableValueMap<UUID, GameProfile, CacheEntry> byUUID = Maps2.createConcurrentExpirableValueMap(
            (k, v) -> {
                final Optional<String> optName;
                if (this.linkLookups.get() && (optName = v.getName()).isPresent() && this.byName.containsKey(optName.get())) {
                    return this.byName.getBacking().get(optName.get());
                }
                return new CacheEntry(v);
            });

    // Lambda logic...
    private ExpirableValueMap<UUID, GameProfile, CacheEntry> cacheByUUID() {
        return this.byUUID;
    }

    // All the game profiles that where used
    private final Queue<GameProfile> profiles = UniqueEvictingQueue.createConcurrent((Equivalence) Equivalence.equals(), CACHE_SIZE);

    private Instant calculateExpirationDate() {
        return Instant.now().plus(EXPIRATION_TIME, EXPIRATION_TIME_UNIT);
    }

    @ConfigSerializable
    private class CacheEntry implements ExpirableValue<GameProfile> {

        @Setting(value = "profile")
        private GameProfile gameProfile;
        @Setting(value = "expiration-date")
        private Instant expirationDate;
        @Setting(value = "signed")
        private boolean signed;

        private CacheEntry() {
        }

        public CacheEntry(GameProfile profile, Instant expirationDate) {
            this.expirationDate = expirationDate;
            this.gameProfile = profile;
        }

        public CacheEntry(GameProfile profile) {
            this(profile, calculateExpirationDate());
        }

        @Override
        public GameProfile getValue() {
            return this.gameProfile;
        }

        @Override
        public boolean isExpired() {
            return Instant.now().compareTo(this.expirationDate) > 0;
        }
    }

    /**
     * Stops the profile resolver service.
     */
    public void shutdown() {
        this.service.shutdown();
    }

    /**
     * Puts the game profile into the cache.
     *
     * @param gameProfile the game profile
     */
    public void putProfile(GameProfile gameProfile) {
        this.linkLookups.set(false);
        this.byUUID.put(gameProfile.getUniqueId(), gameProfile);
        gameProfile.getName().ifPresent(name -> this.byName.put(name, gameProfile));
        this.linkLookups.set(true);
    }

    public Optional<GameProfile> getCachedProfile(String name) {
        return Optional.ofNullable(this.byName.get(name));
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
        GameProfile gameProfile;
        if (useCache) {
            gameProfile = this.byUUID.get(uniqueId);
            if (gameProfile != null && (!signed || this.byUUID.getBacking().get(uniqueId).signed == signed)) {
                return gameProfile;
            }
        }
        // Can throw IOException or ProfileNotFoundException
        gameProfile = this.queryProfileByUUID(uniqueId, signed);
        if (useCache) {
            this.byUUID.put(uniqueId, gameProfile);
            this.byUUID.getBacking().get(uniqueId).signed = signed;
        }
        return gameProfile;
    }

    @Override
    public ListenableFuture<GameProfile> get(UUID uniqueId, boolean useCache) {
        return this.service.submit(() -> this.getById(uniqueId, useCache, true));
    }

    @Override
    public ListenableFuture<Collection<GameProfile>> getAllById(Iterable<UUID> uniqueIds, boolean useCache) {
        return this.service.submit(() -> {
            final ImmutableList.Builder<GameProfile> builder = ImmutableList.builder();
            for (UUID uniqueId : uniqueIds) {
                builder.add(this.getById(uniqueId, useCache, true));
            }
            return builder.build();
        });
    }

    @Override
    public ListenableFuture<GameProfile> get(String name, boolean useCache) {
        return this.service.submit(() -> {
            GameProfile gameProfile;
            if (useCache) {
                gameProfile = this.byName.get(name);
                if (gameProfile != null) {
                    return gameProfile;
                }
            }
            final Map<String, UUID> result = this.queryUUIDByName(Collections.singletonList(name));
            if (result.containsKey(name)) {
                throw new ProfileNotFoundException("Unable to find a profile with the name: " + name);
            }
            gameProfile = this.getById(result.get(name), useCache, true);
            if (useCache) {
                this.byName.put(name, gameProfile);
            }
            return gameProfile;
        });
    }

    @Override
    public ListenableFuture<Collection<GameProfile>> getAllByName(Iterable<String> names, boolean useCache) {
        return this.service.submit(() -> {
            final ImmutableList.Builder<GameProfile> builder = ImmutableList.builder();
            GameProfile gameProfile;

            List<String> rest;
            if (useCache) {
                rest = Lists.newArrayList();
                for (String name : names) {
                    gameProfile = this.byName.get(name);
                    if (gameProfile != null) {
                        builder.add(gameProfile);
                    } else {
                        rest.add(name);
                    }
                }
            } else {
                rest = Lists.newArrayList(names);
            }

            if (!rest.isEmpty()) {
                final Map<String, UUID> results = this.queryUUIDByName(rest);
                for (String name : rest) {
                    if (!results.containsKey(name)) {
                        throw new ProfileNotFoundException("Unable to find a profile with the name: " + name);
                    }
                    gameProfile = this.getById(results.get(name), useCache, true);
                    if (useCache) {
                        this.byName.put(name, gameProfile);
                    }
                    builder.add(gameProfile);
                }
            }

            return builder.build();
        });
    }

    @Override
    public ListenableFuture<GameProfile> fill(GameProfile profile, boolean signed, boolean useCache) {
        return this.service.submit(() -> {
            final GameProfile gameProfile = this.getById(profile.getUniqueId(), useCache, signed);
            ((LanternGameProfile) profile).setName(gameProfile.getName().get());
            profile.getPropertyMap().putAll(gameProfile.getPropertyMap());
            return profile;
        });
    }

    @Override
    public Collection<GameProfile> getCachedProfiles() {
        return ImmutableList.copyOf(this.profiles);
    }

    @Override
    public Collection<GameProfile> match(String lastKnownName) {
        return this.profiles.stream().filter(profile -> {
            final Optional<String> optName = profile.getName();
            return optName.isPresent() && optName.get().equalsIgnoreCase(lastKnownName);
        }).collect(GuavaCollectors.toImmutableList());
    }

    private GameProfile queryProfileByUUID(UUID uniqueId, boolean signed) throws IOException, ProfileNotFoundException {
        final URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/"
                + UUIDHelper.toFlatString(uniqueId) + (signed ? "?unsigned=false" : ""));

        int attempts = 0;
        while (true) {
            URLConnection uc = url.openConnection();
            InputStream is = uc.getInputStream();

            // Can be empty if the unique id invalid is
            if (is.available() == 0) {
                throw new ProfileNotFoundException("Failed to find a profile with the uuid: " + uniqueId);
            }

            JsonObject json = this.gson.fromJson(new InputStreamReader(is), JsonObject.class);
            if (json.has("error")) {
                // If it fails too many times, just leave it
                if (++attempts > 6) {
                    throw new IOException("Failed to retrieve the profile after 6 attempts: " + uniqueId);
                }
                // Too many requests, lets wait for 10 seconds
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new IOException("Something interrupted the next attempt delay.");
                }
                continue;
            }

            String name = json.get("name").getAsString();
            Multimap<String, ProfileProperty> properties = LinkedHashMultimap.create();

            if (json.has("properties")) {
                JsonArray array = json.get("properties").getAsJsonArray();
                for (JsonElement element : array) {
                    JsonObject property = (JsonObject) element;

                    String propName = property.get("name").getAsString();
                    String value = property.get("value").getAsString();
                    String signature = property.has("signature") ? property.get("signature").getAsString() : null;

                    properties.put(propName, new LanternProfileProperty(propName, value, signature));
                }
            }

            return new LanternGameProfile(uniqueId, name, properties);
        }
    }

    private Map<String, UUID> queryUUIDByName(Iterable<String> names) throws IOException {
        Map<String, UUID> results = Maps.newHashMap();
        if (!names.iterator().hasNext()) {
            return results;
        }
        List<String> namesList = Lists.newArrayList(names);
        int size = namesList.size();
        int count = 0;
        do {
            int index = count;
            count += 100;
            if (count > size) {
                count = size;
            }
            this.postNameToUUIDPart(results, namesList.subList(index, count));
        } while (namesList.size() - count > 0);
        return results;
    }

    private void postNameToUUIDPart(Map<String, UUID> results, List<String> names) throws IOException {
        String body = this.gson.toJson(names);
        URL url = new URL("https://api.mojang.com/profiles/minecraft");

        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        uc.setRequestMethod("POST");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setUseCaches(false);
        uc.setDoInput(true);
        uc.setDoOutput(true);

        DataOutputStream os = new DataOutputStream(uc.getOutputStream());
        os.write(body.getBytes());
        os.flush();
        os.close();

        JsonArray json = this.gson.fromJson(new InputStreamReader(uc.getInputStream()), JsonArray.class);
        for (JsonElement element : json) {
            JsonObject obj = element.getAsJsonObject();
            results.put(obj.get("name").getAsString(), UUIDHelper.fromFlatString(obj.get("id").getAsString()));
        }
    }
}
