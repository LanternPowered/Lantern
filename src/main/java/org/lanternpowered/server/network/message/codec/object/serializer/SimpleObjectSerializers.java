package org.lanternpowered.server.network.message.codec.object.serializer;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import org.lanternpowered.server.network.message.codec.object.VarInt;
import org.lanternpowered.server.network.message.codec.object.VarLong;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.text.Text;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

public class SimpleObjectSerializers implements ObjectSerializers {

    public static final SimpleObjectSerializers DEFAULT = new SimpleObjectSerializers() {

        {
            this.register(DataView.class, new SerializerDataView());
            this.register(String.class, new SerializerString());
            this.register(Text.class, new SerializerText());
            this.register(UUID.class, new SerializerUUID());
            this.register(VarInt.class, new SerializerVarInt());
            this.register(VarLong.class, new SerializerVarLong());
            this.register(Vector3i.class, new SerializerVector3i());
        }

    };

    private final Map<Class<?>, ObjectSerializer<?>> serializers = Maps.newConcurrentMap();
    private final LoadingCache<Class<?>, Optional<ObjectSerializer<?>>> cache =
            CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, Optional<ObjectSerializer<?>>>() {

                @Override
                public Optional<ObjectSerializer<?>> load(Class<?> key) throws Exception {
                    ObjectSerializer<?> serializer = null;
                    for (Class<?> type : TypeToken.of(key).getTypes().rawTypes()) {
                        serializer = serializers.get(type);
                        if (serializer != null) {
                            return Optional.<ObjectSerializer<?>>of(serializer);
                        }
                    }
                    return Optional.absent();
                }

            });

    @Override
    public <T> void register(Class<T> type, ObjectSerializer<? super T> serializer) {
        checkNotNull(serializer, "serializer");
        checkNotNull(type, "type");
        this.serializers.put(type, serializer);
        this.cache.invalidateAll();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> ObjectSerializer<T> findExact(Class<T> type) {
        return (ObjectSerializer<T>) this.serializers.get(type);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> ObjectSerializer<? super T> find(Class<T> type) {
        try {
            return (ObjectSerializer<? super T>) this.cache.get(type).orNull();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

}
