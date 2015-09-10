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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

public class LanternGameProfileResolver implements GameProfileResolver {

    private final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
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
                        if (uuidByNameCache.getIfPresent(name) == null) {
                            rest.add(name);
                        } else {
                            uniqueIds.add(uuidByNameCache.get(name));
                        }
                    }
                } else {
                    rest = Lists.newArrayList(names);
                }
                uniqueIds.addAll(new GetUUID(names).call().values());
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
        if (this.uuidByNameCache.getIfPresent(lastKnownName) != null) {
            try {
                profiles.add(this.profileCache.get(this.uuidByNameCache.get(lastKnownName)));
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
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
            URLConnection uc = url.openConnection();

            /**
             * Can return null if the uuid invalid is.
             */
            InputStream is = uc.getInputStream();
            if (is.available() == 0) {
                return null;
            }

            JsonObject json = gson.fromJson(new InputStreamReader(is), JsonObject.class);

            String name = json.get("name").getAsString();
            List<Property> properties = Lists.newArrayList();

            /**
             * Search for the textures entry.
             */
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
                this.post(results, names.subList(count, Math.min(size, count + 100)));
                count += 100;
            } while (names.size() - count > 100);

            return results;
        }

        public void post(Map<String, UUID> results, List<String> names) throws IOException {
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
