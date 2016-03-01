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
import org.lanternpowered.server.config.ConfigBase;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.scheduler.LanternScheduler;
import org.lanternpowered.server.util.UUIDHelper;
import org.lanternpowered.server.util.UniqueEvictingQueue;
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
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public final class LanternGameProfileManager implements GameProfileManager {

    private static final int CACHE_SIZE = 1000;

    // The duraton before a profile expires
    private static final Duration EXPIRATION_DURATION = Duration.ofDays(30);

    // The cache file
    private final ProfileCacheFile cacheFile;

    // The gson instance
    private final Gson gson = new Gson();

    // Lookup by name
    private final Map<String, CacheEntry> byName = Maps.newConcurrentMap();

    // Lookup by unique id
    private final Map<UUID, CacheEntry> byUUID = Maps.newConcurrentMap();

    // All the game profiles that where used
    private final Queue<GameProfile> profiles = UniqueEvictingQueue.createConcurrent((Equivalence) Equivalence.equals(), CACHE_SIZE);

    private Instant calculateExpirationDate() {
        return Instant.now().plus(EXPIRATION_DURATION);
    }

    private class ProfileCacheFile extends ConfigBase {

        @Setting(value = "entries")
        private List<CacheEntry> entries = new ArrayList<>();

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
    private static class CacheEntry {

        @Setting(value = "profile")
        private LanternGameProfile gameProfile;
        @Setting(value = "expiration-date")
        private Instant expirationDate;
        @Setting(value = "signed")
        private boolean signed;

        private CacheEntry() {
        }

        public CacheEntry(GameProfile profile, Instant expirationDate) {
            this.gameProfile = (LanternGameProfile) profile;
            this.expirationDate = expirationDate;
        }

        public boolean isExpired() {
            return Instant.now().compareTo(this.expirationDate) > 0;
        }
    }

    public LanternGameProfileManager(Path cacheFile) {
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

    /**
     * Stops the profile resolver service.
     */
    public void shutdown() {
        try {
            this.cacheFile.save();
        } catch (IOException e) {
            LanternGame.log().warn("An error occurred while saving the profile cache file.", e);
        }
    }

    /**
     * Puts the game profile into the cache.
     *
     * @param gameProfile the game profile
     * @param signed whether the properties of the profile are signed
     */
    public void putProfile(GameProfile gameProfile, boolean signed) {
        final CacheEntry entry = new CacheEntry(gameProfile, this.calculateExpirationDate());
        entry.signed = signed;
        this.byUUID.put(gameProfile.getUniqueId(), entry);
        gameProfile.getName().ifPresent(name -> this.byName.put(name, entry));
    }

    public Optional<GameProfile> getCachedProfile(String name) {
        CacheEntry entry = this.byName.get(name);
        if (entry == null || entry.isExpired()) {
            return Optional.empty();
        }
        return Optional.ofNullable(entry.gameProfile);
    }

    @Override
    public GameProfile createProfile(UUID uniqueId, @Nullable String name) {
        return new LanternGameProfile(uniqueId, name);
    }

    @Override
    public ProfileProperty createProfileProperty(String name, String value, @Nullable String signature) {
        return new LanternProfileProperty(name, value, signature);
    }

    private CacheEntry getById(UUID uniqueId, boolean useCache, boolean signed) throws Exception {
        CacheEntry entry;
        if (useCache) {
            entry = this.byUUID.get(uniqueId);
            if (entry != null && !entry.isExpired()
                    && (!signed || entry.signed == signed)) {
                return entry;
            }
        }
        // Can throw IOException or ProfileNotFoundException
        GameProfile gameProfile = this.queryProfileByUUID(uniqueId, signed);
        entry = new CacheEntry(gameProfile, this.calculateExpirationDate());
        if (useCache) {
            this.byUUID.put(uniqueId, entry);
            entry.signed = signed;
        }
        return entry;
    }

    @Override
    public CompletableFuture<GameProfile> get(UUID uniqueId, boolean useCache) {
        return LanternScheduler.getInstance().submitAsyncTask(() -> this.getById(uniqueId, useCache, true).gameProfile);
    }

    @Override
    public CompletableFuture<Collection<GameProfile>> getAllById(Iterable<UUID> uniqueIds, boolean useCache) {
        return LanternScheduler.getInstance().submitAsyncTask(() -> {
            final ImmutableList.Builder<GameProfile> builder = ImmutableList.builder();
            for (UUID uniqueId : uniqueIds) {
                builder.add(this.getById(uniqueId, useCache, true).gameProfile);
            }
            return builder.build();
        });
    }

    @Override
    public CompletableFuture<GameProfile> get(String name, boolean useCache) {
        return LanternScheduler.getInstance().submitAsyncTask(() -> {
            CacheEntry entry;
            if (useCache) {
                entry = this.byName.get(name);
                if (entry != null && !entry.isExpired()) {
                    return entry.gameProfile;
                }
            }
            final Map<String, UUID> result = this.queryUUIDByName(Collections.singletonList(name));
            if (!result.containsKey(name)) {
                throw new ProfileNotFoundException("Unable to find a profile with the name: " + name);
            }
            entry = this.getById(result.get(name), useCache, true);
            if (useCache) {
                this.byName.put(name, entry);
            }
            return entry.gameProfile;
        });
    }

    @Override
    public CompletableFuture<Collection<GameProfile>> getAllByName(Iterable<String> names, boolean useCache) {
        return LanternScheduler.getInstance().submitAsyncTask(() -> {
            final ImmutableList.Builder<GameProfile> builder = ImmutableList.builder();
            CacheEntry entry;

            List<String> rest;
            if (useCache) {
                rest = Lists.newArrayList();
                for (String name : names) {
                    entry = this.byName.get(name);
                    if (entry != null && !entry.isExpired()) {
                        builder.add(entry.gameProfile);
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
                    entry = this.getById(results.get(name), useCache, true);
                    if (useCache) {
                        this.byName.put(name, entry);
                    }
                    builder.add(entry.gameProfile);
                }
            }

            return builder.build();
        });
    }

    @Override
    public CompletableFuture<GameProfile> fill(GameProfile profile, boolean signed, boolean useCache) {
        return LanternScheduler.getInstance().submitAsyncTask(() -> {
            final GameProfile gameProfile = this.getById(profile.getUniqueId(), useCache, signed).gameProfile;
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

            // If it fails too many times, just leave it
            if (++attempts > 6) {
                throw new IOException("Failed to retrieve the profile after 6 attempts: " + uniqueId);
            }

            JsonObject json = this.gson.fromJson(new InputStreamReader(is), JsonObject.class);
            if (json.has("error")) {
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
