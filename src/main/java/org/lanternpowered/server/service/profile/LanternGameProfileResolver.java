/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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
package org.lanternpowered.server.service.profile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.lanternpowered.server.game.LanternGameProfile;
import org.lanternpowered.server.game.LanternGameProfile.Property;
import org.lanternpowered.server.util.UUIDHelper;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.service.profile.GameProfileResolver;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class LanternGameProfileResolver implements GameProfileResolver {

    private final AtomicInteger counter = new AtomicInteger();
    private final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool(
            runnable -> new Thread(runnable, "profile-resolver-" + this.counter.getAndIncrement())));
    private final Gson gson = new Gson();

    private final LoadingCache<UUID, GameProfile> profileCache = 
            CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<UUID, GameProfile>() {

                @Override
                public GameProfile load(UUID key) throws Exception {
                    return new GetProfile(key).call();
                }
            });
    private final LoadingCache<String, UUID> uuidByNameCache = 
            CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<String, UUID>() {

                @Override
                public UUID load(String key) throws Exception {
                    return new GetUUID(Sets.newHashSet(key)).call().get(key);
                }
            });

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
    public void cacheProfile(GameProfile gameProfile) {
        this.uuidByNameCache.put(gameProfile.getName(), gameProfile.getUniqueId());
        this.profileCache.put(gameProfile.getUniqueId(), gameProfile);
    }

    @Override
    public ListenableFuture<GameProfile> get(UUID uniqueId) {
        return this.get(uniqueId, true);
    }

    @Override
    public ListenableFuture<GameProfile> get(UUID uniqueId, boolean useCache) {
        return this.service.submit(useCache ? () -> profileCache.get(uniqueId) : new GetProfile(uniqueId));
    }

    @Override
    public ListenableFuture<GameProfile> get(String name) {
        return this.get(name, true);
    }

    @Override
    public ListenableFuture<GameProfile> get(String name, boolean useCache) {
        return this.service.submit(new Callable<GameProfile>() {

            @Override
            public GameProfile call() throws Exception {
                UUID uniqueId = useCache ? uuidByNameCache.get(name) :
                    new GetUUID(Sets.newHashSet(name)).call().get(name);
                if (uniqueId == null) {
                    return null;
                }
                return useCache ? profileCache.get(uniqueId) : new GetProfile(uniqueId).call();
            }
        });
    }

    @Override
    public ListenableFuture<Collection<GameProfile>> getAllByName(Iterable<String> names, boolean useCache) {
        return this.service.submit(new Callable<Collection<GameProfile>>() {

            @Override
            public Collection<GameProfile> call() throws Exception {
                List<GameProfile> profiles = Lists.newArrayList();
                List<UUID> uniqueIds = Lists.newArrayList();
                List<String> rest = Lists.newArrayList();
                if (useCache) {
                    rest = Lists.newArrayList();
                    for (String name : names) {
                        UUID uniqueId = uuidByNameCache.getIfPresent(name);
                        if (uniqueId == null) {
                            rest.add(name);
                        } else {
                            uniqueIds.add(uniqueId);
                        }
                    }
                } else {
                    rest = Lists.newArrayList(names);
                }
                if (!rest.isEmpty()) {
                    Map<String, UUID> results = new GetUUID(rest).call();
                    if (useCache) {
                        uuidByNameCache.putAll(results);
                    }
                    uniqueIds.addAll(results.values());
                }
                for (UUID uniqueId : uniqueIds) {
                    profiles.add(useCache ? profileCache.get(uniqueId) : new GetProfile(uniqueId).call());
                }
                return ImmutableList.copyOf(profiles);
            }
        });
    }

    @Override
    public ListenableFuture<Collection<GameProfile>> getAllById(final Iterable<UUID> uniqueIds, final boolean useCache) {
        return this.service.submit(new Callable<Collection<GameProfile>>() {

            @Override
            public Collection<GameProfile> call() throws Exception {
                List<GameProfile> profiles = Lists.newArrayList();
                for (UUID uniqueId : uniqueIds) {
                    profiles.add(useCache ? profileCache.get(uniqueId) : new GetProfile(uniqueId).call());
                }
                return ImmutableList.copyOf(profiles);
            }
        });
    }

    @Override
    public Collection<GameProfile> getCachedProfiles() {
        return ImmutableList.copyOf(this.profileCache.asMap().values());
    }

    @Override
    public Collection<GameProfile> match(String lastKnownName) {
        List<GameProfile> profiles = Lists.newArrayList();
        UUID uniqueId = this.uuidByNameCache.getIfPresent(lastKnownName);
        if (uniqueId != null) {
            GameProfile profile = this.profileCache.getIfPresent(uniqueId);
            if (profile != null) {
                profiles.add(profile);
            }
        }
        return ImmutableList.copyOf(profiles);
    }

    private class GetProfile implements Callable<GameProfile> {

        private final UUID uniqueId;

        public GetProfile(UUID uniqueId) {
            this.uniqueId = uniqueId;
        }

        @Override
        public GameProfile call() throws Exception {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/"
                    + UUIDHelper.toFlatString(this.uniqueId));

            while (true) {
                URLConnection uc = url.openConnection();
                InputStream is = uc.getInputStream();

                // Can be empty if the unique id invalid is
                if (is.available() == 0) {
                    return null;
                }

                int attempts = 0;

                JsonObject json = gson.fromJson(new InputStreamReader(is), JsonObject.class);
                if (json.has("error")) {
                    // If it fails too many times, just leave it
                    if (++attempts > 6) {
                        return null;
                    }
                    // Too many requests, lets wait for 10 seconds
                    Thread.sleep(10000);
                    continue;
                }

                String name = json.get("name").getAsString();
                List<Property> properties = Lists.newArrayList();

                if (json.has("properties")) {
                    JsonArray array = json.get("properties").getAsJsonArray();
                    for (JsonElement element : array) {
                        JsonObject property = (JsonObject) element;

                        String propName = property.get("name").getAsString();
                        String value = property.get("value").getAsString();
                        String signature = property.has("signature") ? property.get("signature").getAsString() : null;

                        properties.add(new Property(propName, value, signature));
                    }
                }

                return new LanternGameProfile(this.uniqueId, name, properties);
            }
        }
    }

    private class GetUUID implements Callable<Map<String, UUID>> {

        private final Iterable<String> names;

        public GetUUID(Iterable<String> names) {
            this.names = names;
        }

        @Override
        public Map<String, UUID> call() throws Exception {
            Map<String, UUID> results = Maps.newHashMap();
            if (!this.names.iterator().hasNext()) {
                return results;
            }
            List<String> names = Lists.newArrayList(this.names);

            int size = names.size();
            int count = 0;
            do {
                int index = count;
                count += 100;
                if (count > size) {
                    count = size;
                }
                this.post(results, names.subList(index, count));
            } while (names.size() - count > 0);

            return results;
        }

        private void post(Map<String, UUID> results, List<String> names) throws IOException {
            String body = gson.toJson(names);
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

            JsonArray json = gson.fromJson(new InputStreamReader(uc.getInputStream()), JsonArray.class);
            for (JsonElement element : json) {
                JsonObject obj = element.getAsJsonObject();
                results.put(obj.get("name").getAsString(), UUIDHelper.fromFlatString(obj.get("id").getAsString()));
            }
        }
    }
}
