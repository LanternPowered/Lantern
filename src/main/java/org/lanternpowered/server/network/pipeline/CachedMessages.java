package org.lanternpowered.server.network.pipeline;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.caching.CachingHashGenerator;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.TypeToken;

public class CachedMessages {

    // Using a cache to cleanup the objects if they are no longer used
    private static final LoadingCache<Class<?>, Optional<CachingHashGenerator<?>>> types = 
            CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES)
                    .build(new CacheLoader<Class<?>, Optional<CachingHashGenerator<?>>>() {

                @SuppressWarnings({"unchecked", "rawtypes"})
                @Override
                public Optional<CachingHashGenerator<?>> load(Class<?> key) throws Exception {
                    for (Class<?> key0 : TypeToken.of(key).getTypes().rawTypes()) {
                        Caching caching = key0.getAnnotation(Caching.class);
                        if (caching != null) {
                            // This cast is strange, but necessary for some reason
                            return (Optional) Optional.of(caching.value().newInstance());
                        }
                    }
                    return Optional.absent();
                }

            });

    private static final LoadingCache<Message, CachedMessage> messages = 
            // Do not keep the objects too long in memory, even if it has weak keys
            CacheBuilder.newBuilder().weakKeys().expireAfterAccess(1, TimeUnit.MINUTES)
                    .build(new CacheLoader<Message, CachedMessage>() {

                @Override
                public CachedMessage load(Message key) throws Exception {
                    return new CachedMessage();
                }

            });

    public static Optional<CachingHashGenerator<?>> getHashGenerator(Class<?> type) {
        try {
            return types.get(type);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    public static CachedMessage getCachedMessage(Message message) {
        try {
            return messages.get(message);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }
}
