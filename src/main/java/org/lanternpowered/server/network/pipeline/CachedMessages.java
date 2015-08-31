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

public class CachedMessages {

    // Using a cache to cleanup the objects if they are no longer used
    private static final LoadingCache<Class<?>, Optional<CachingHashGenerator<?>>> types = 
            CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES)
                    .build(new CacheLoader<Class<?>, Optional<CachingHashGenerator<?>>>() {

                @SuppressWarnings("unchecked")
                @Override
                public Optional<CachingHashGenerator<?>> load(Class<?> key) throws Exception {
                    Caching[] caching = key.getAnnotationsByType(Caching.class);
                    if (caching.length == 0) {
                        return Optional.absent();
                    }
                    return (Optional<CachingHashGenerator<?>>) Optional.of(caching[0].value().newInstance());
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
