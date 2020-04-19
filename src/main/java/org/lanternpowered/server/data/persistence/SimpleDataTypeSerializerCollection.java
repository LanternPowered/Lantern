/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.data.persistence;

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class SimpleDataTypeSerializerCollection implements DataTypeSerializerCollection {

    /**
     * The {@link DataTypeSerializerContext}.
     */
    private final DataTypeSerializerContext context = () -> SimpleDataTypeSerializerCollection.this;

    private class DataSerializerToDataTypeSerializer<T> implements DataViewTypeSerializer<T> {

        private final DataTranslator<T> dataTranslator;

        private DataSerializerToDataTypeSerializer(DataTranslator<T> dataTranslator) {
            this.dataTranslator = dataTranslator;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, DataView dataView) throws InvalidDataException {
            return this.dataTranslator.translate(dataView);
        }

        @Override
        public DataView serialize(TypeToken<?> type, DataTypeSerializerContext ctx, T obj) throws InvalidDataException {
            return this.dataTranslator.translate(obj);
        }
    }

    private final Map<TypeToken<?>, DataTypeSerializer<?,?>> dataTypeSerializers = new ConcurrentHashMap<>();
    private final Map<TypeToken<?>, DataTranslator<?>> dataTranslators = new ConcurrentHashMap<>();
    private final Set<Function<TypeToken<?>, Optional<DataTypeSerializer<?,?>>>> serializerFactories =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final LoadingCache<TypeToken<?>, Optional<DataTypeSerializer<?,?>>> dataTypeSerializersCache =
            Caffeine.newBuilder().build(this::loadTypeSerializer);

    private Optional<DataTypeSerializer<?,?>> loadTypeSerializer(TypeToken<?> key) {
        for (Map.Entry<TypeToken<?>, DataTypeSerializer<?,?>> entry : this.dataTypeSerializers.entrySet()) {
            if (entry.getKey().isSupertypeOf(key)) {
                return Optional.of(entry.getValue());
            }
        }
        for (Function<TypeToken<?>, Optional<DataTypeSerializer<?,?>>> function : this.serializerFactories) {
            final DataTypeSerializer<?,?> serializer = function.apply(key).orElse(null);
            if (serializer != null) {
                return Optional.of(serializer);
            }
        }
        for (Map.Entry<TypeToken<?>, DataTranslator<?>> entry : this.dataTranslators.entrySet()) {
            if (entry.getKey().isSupertypeOf(key)) {
                return Optional.of(new DataSerializerToDataTypeSerializer<>(entry.getValue()));
            }
        }
        return Optional.empty();
    }

    private final LoadingCache<TypeToken<?>, Optional<DataTranslator<?>>> dataTranslatorsCache =
            Caffeine.newBuilder().build(this::loadTranslator);

    private Optional<DataTranslator<?>> loadTranslator(TypeToken<?> key) {
        for (Map.Entry<TypeToken<?>, DataTranslator<?>> entry : this.dataTranslators.entrySet()) {
            if (entry.getKey().isSupertypeOf(key)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public DataTypeSerializerCollection registerTypeSerializerFactory(Function<TypeToken<?>, Optional<DataTypeSerializer<?, ?>>> factory) {
        this.serializerFactories.add(checkNotNull(factory, "factory"));
        this.dataTranslatorsCache.invalidateAll();
        this.dataTypeSerializersCache.invalidateAll();
        return this;
    }

    @Override
    public <T> DataTypeSerializerCollection registerTranslator(DataTranslator<T> serializer) {
        checkNotNull(serializer, "serializer");
        final TypeToken<T> typeToken = checkNotNull(serializer.getToken(), "typeToken");
        this.dataTranslators.put(typeToken, serializer);
        this.dataTypeSerializersCache.invalidateAll();
        this.dataTranslatorsCache.invalidateAll();
        return this;
    }

    @Override
    public <T, D> DataTypeSerializerCollection registerTypeSerializer(TypeToken<T> objectType, DataTypeSerializer<T, D> serializer) {
        checkNotNull(serializer, "serializer");
        checkNotNull(objectType, "objectType");
        this.dataTypeSerializers.put(objectType, serializer);
        this.dataTypeSerializersCache.invalidateAll();
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, D> Optional<DataTypeSerializer<T, D>> getTypeSerializer(TypeToken<T> objectType) {
        return (Optional) this.dataTypeSerializersCache.get(checkNotNull(objectType, "objectType"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<DataTranslator<T>> getTranslator(TypeToken<T> objectType) {
        return (Optional) this.dataTranslatorsCache.get(checkNotNull(objectType, "objectType"));
    }

    public DataTypeSerializerContext getTypeSerializerContext() {
        return this.context;
    }
}
