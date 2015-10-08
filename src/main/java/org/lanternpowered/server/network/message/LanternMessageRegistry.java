package org.lanternpowered.server.network.message;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.message.processor.Processor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class LanternMessageRegistry implements MessageRegistry {

    private final TIntObjectMap<MessageRegistration<?>> byOpcode = new TIntObjectHashMap<MessageRegistration<?>>();
    private final Map<Class<?>, MessageRegistration<?>> byType = Maps.newHashMap();
    private final LoadingCache<Class<?>, Optional<MessageRegistration<?>>> byTypeLookup = 
            CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, Optional<MessageRegistration<?>>>() {

                @Override
                public Optional<MessageRegistration<?>> load(Class<?> key) throws Exception {
                    while (key != Object.class) {
                        if (byType.containsKey(key)) {
                            return Optional.of(byType.get(key));
                        }
                        key = key.getSuperclass();
                    }
                    return Optional.empty();
                }
            });

    private LanternMessageRegistration<?> getOrCreate(Class<? extends Message> type) {
        if (this.byType.containsKey(type)) {
            return (LanternMessageRegistration<?>) this.byType.get(type);
        }
        LanternMessageRegistration<?> registration = new LanternMessageRegistration(type);
        this.byType.put(type, registration);
        this.byTypeLookup.invalidate(TypeToken.of(type).getTypes().rawTypes());
        return registration;
    }

    @Override
    public <M extends Message, C extends Codec<? super M>> LanternMessageRegistration<M> register(int opcode,
            Class<M> message, Class<C> codec) {
        return this.register(opcode, message, codec, (Handler<M>) null);
    }

    @Override
    public <M extends Message, C extends Codec<? super M>, H extends Handler<? super M>> LanternMessageRegistration<M> register(
            int opcode, Class<M> message, Class<C> codec, Class<H> handler) {
        try {
            return this.register(opcode, message, codec, handler.newInstance());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <M extends Message, C extends Codec<? super M>, H extends Handler<? super M>> LanternMessageRegistration<M> register(
            int opcode, Class<M> message, Class<C> codec, H handler) {
        LanternMessageRegistration<M> registration = (LanternMessageRegistration<M>) this.getOrCreate(message);
        try {
            registration.opcode = opcode;
            registration.codec = codec.newInstance();
            registration.handler = handler;
            this.byOpcode.put(opcode, registration);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return registration;
    }

    @Override
    public <M extends Message, H extends Handler<? super M>> MessageRegistration<M> register(Class<M> message, H handler) {
        LanternMessageRegistration<M> registration = (LanternMessageRegistration<M>) this.getOrCreate(message);
        registration.handler = handler;
        return registration;
    }

    @Override
    public <M extends Message> MessageRegistration<M> find(Class<M> message) {
        try {
            return (MessageRegistration<M>) this.byTypeLookup.get(message).orElse(null);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <M extends Message> MessageRegistration<M> find(int opcode) {
        return (MessageRegistration<M>) this.byOpcode.get(opcode);
    }

    @Override
    public <M extends Message, P extends Processor<? super M>> LanternMessageRegistration<M> register(
            Class<M> message, P processor) {
        LanternMessageRegistration<M> registration = (LanternMessageRegistration<M>) this.getOrCreate(message);
        registration.processor = processor;
        return registration;
    }

    @Override
    public <M extends Message, P extends Processor<? super M>> LanternMessageRegistration<M> register(
            Class<M> message, Class<P> processor) {
        try {
            return this.register(message, processor.newInstance());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static final class LanternMessageRegistration<M extends Message> implements MessageRegistration<M> {

        private final Class<M> type;

        private Processor processor;
        private Codec<?> codec;
        private Handler<?> handler;
        private Integer opcode;

        public LanternMessageRegistration(Class<M> type) {
            this.type = type;
        }

        @Override
        public Integer getOpcode() {
            return this.opcode;
        }

        @Override
        public Class<M> getType() {
            return this.type;
        }

        @Override
        public <C extends Codec<? super M>> C getCodec() {
            return (C) this.codec;
        }

        @Override
        public <H extends Handler<? super M>> H getHandler() {
            return (H) this.handler;
        }

        @Override
        public <P extends Processor<? super M>> P getProcessor() {
            return (P) this.processor;
        }
    }

}
