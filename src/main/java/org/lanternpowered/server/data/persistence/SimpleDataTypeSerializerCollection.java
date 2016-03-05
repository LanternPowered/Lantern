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
package org.lanternpowered.server.data.persistence;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.DataSerializer;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class SimpleDataTypeSerializerCollection implements DataTypeSerializerCollection {

    private final static DataQuery WRAPPED_VALUE = DataQuery.of("WrappedValue");

    /**
     * The {@link DataTypeSerializerContext}.
     */
    private final DataTypeSerializerContext context = () -> SimpleDataTypeSerializerCollection.this;

    private class WrappedDataSerializer<T> implements DataSerializer<T> {

        private final DataViewTypeSerializer<T> serializer;
        private final TypeToken<T> typeToken;

        private WrappedDataSerializer(TypeToken<T> typeToken, DataViewTypeSerializer<T> serializer) {
            this.serializer = serializer;
            this.typeToken = typeToken;
        }

        @Override
        public TypeToken<T> getToken() {
            return this.typeToken;
        }

        @Override
        public Optional<T> deserialize(DataView view) throws InvalidDataException {
            return this.serializer.deserialize(this.typeToken, context, view);
        }

        @Override
        public DataContainer serialize(T obj) throws InvalidDataException {
            DataView dataView = this.serializer.serialize(this.typeToken, context, obj);
            return dataView instanceof DataContainer ? (DataContainer) dataView : dataView.copy();
        }
    }

    /**
     * Wraps around a regular {@link DataTypeSerializer}, so not extending {@link DataViewTypeSerializer}
     * and is used to be compatible with the {@link DataSerializer} api, it serializes the object that isn't a {@link DataView}
     * and puts it inside a newly created {@link DataContainer}.
     */
    private class WrappedContextualDataViewTypeSerializer<T, D> implements DataViewTypeSerializer<T>, DataSerializer<T> {

        private final DataTypeSerializer<T, D> serializer;
        private final TypeToken<T> typeToken;

        private WrappedContextualDataViewTypeSerializer(TypeToken<T> typeToken, DataTypeSerializer<T, D> serializer) {
            this.serializer = serializer;
            this.typeToken = typeToken;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Optional<T> deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, DataView dataView) throws InvalidDataException {
            return dataView.get(WRAPPED_VALUE).flatMap(v -> this.serializer.deserialize(type, ctx, (D) v));
        }

        @Override
        public DataView serialize(TypeToken<?> type, DataTypeSerializerContext ctx, T obj) throws InvalidDataException {
            return new MemoryDataContainer().set(WRAPPED_VALUE, this.serializer.serialize(type, ctx, obj));
        }

        @Override
        public TypeToken<T> getToken() {
            return this.typeToken;
        }

        @Override
        public Optional<T> deserialize(DataView view) throws InvalidDataException {
            return this.deserialize(this.typeToken, context, view);
        }

        @Override
        public DataContainer serialize(T obj) throws InvalidDataException {
            DataView dataView = this.serialize(this.typeToken, context, obj);
            return dataView instanceof DataContainer ? (DataContainer) dataView : dataView.copy();
        }
    }

    private class DataSerializerToDataTypeSerializer<T> implements DataViewTypeSerializer<T> {

        private final DataSerializer<T> dataSerializer;

        private DataSerializerToDataTypeSerializer(DataSerializer<T> dataSerializer) {
            this.dataSerializer = dataSerializer;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Optional<T> deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, DataView dataView) throws InvalidDataException {
            return this.dataSerializer.deserialize(dataView);
        }

        @Override
        public DataView serialize(TypeToken<?> type, DataTypeSerializerContext ctx, T obj) throws InvalidDataException {
            return this.dataSerializer.serialize(obj);
        }
    }

    private final Map<TypeToken<?>, DataTypeSerializer<?,?>> dataTypeSerializers = new ConcurrentHashMap<>();
    private final Map<TypeToken<?>, DataSerializer<?>> dataSerializers = new ConcurrentHashMap<>();
    private final Set<Function<TypeToken<?>, Optional<DataTypeSerializer<?,?>>>> serializerFactories =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final LoadingCache<TypeToken<?>, Optional<DataTypeSerializer<?,?>>> dataTypeSerializersCache =
            CacheBuilder.newBuilder().build(new CacheLoader<TypeToken<?>, Optional<DataTypeSerializer<?,?>>>() {
                @SuppressWarnings("unchecked")
                @Override
                public Optional<DataTypeSerializer<?,?>> load(TypeToken<?> key) throws Exception {
                    for (Map.Entry<TypeToken<?>, DataTypeSerializer<?,?>> entry : dataTypeSerializers.entrySet()) {
                        if (entry.getKey().isAssignableFrom(key)) {
                            return Optional.of(entry.getValue());
                        }
                    }
                    for (Function<TypeToken<?>, Optional<DataTypeSerializer<?,?>>> function : serializerFactories) {
                        DataTypeSerializer<?,?> serializer = function.apply(key).orElse(null);
                        if (serializer != null) {
                            Optional<DataSerializer<?>> dataSerializer = dataSerializersCache.getIfPresent(key);
                            if (dataSerializer == null) {
                                dataSerializersCache.put(key, Optional.of(serializer instanceof DataViewTypeSerializer ?
                                        new WrappedDataSerializer((TypeToken) key, (DataViewTypeSerializer<?>) serializer) :
                                        new WrappedContextualDataViewTypeSerializer<>((TypeToken) key, serializer)));
                            }
                            return Optional.of(serializer);
                        }
                    }
                    return Optional.empty();
                }
            });

    private final LoadingCache<TypeToken<?>, Optional<DataSerializer<?>>> dataSerializersCache =
            CacheBuilder.newBuilder().build(new CacheLoader<TypeToken<?>, Optional<DataSerializer<?>>>() {
                @SuppressWarnings("unchecked")
                @Override
                public Optional<DataSerializer<?>> load(TypeToken<?> key) throws Exception {
                    for (Map.Entry<TypeToken<?>, DataSerializer<?>> entry : dataSerializers.entrySet()) {
                        if (entry.getKey().isAssignableFrom(key)) {
                            return Optional.of(entry.getValue());
                        }
                    }
                    for (Function<TypeToken<?>, Optional<DataTypeSerializer<?,?>>> function : serializerFactories) {
                        DataTypeSerializer<?,?> serializer = function.apply(key).orElse(null);
                        if (serializer != null) {
                            Optional<DataTypeSerializer<?,?>> dataTypeSerializer = dataTypeSerializersCache.getIfPresent(key);
                            if (dataTypeSerializer == null) {
                                dataTypeSerializersCache.put(key, Optional.of(serializer));
                            }
                            return Optional.of(serializer instanceof DataViewTypeSerializer ?
                                    new WrappedDataSerializer((TypeToken) key, (DataViewTypeSerializer<?>) serializer) :
                                    new WrappedContextualDataViewTypeSerializer<>((TypeToken) key, serializer));
                        }
                    }
                    return Optional.empty();
                }
            });

    @Override
    public DataTypeSerializerCollection registerTypeSerializerFactory(Function<TypeToken<?>, Optional<DataTypeSerializer<?, ?>>> factory) {
        this.serializerFactories.add(checkNotNull(factory, "factory"));
        this.dataSerializersCache.invalidateAll();
        this.dataTypeSerializersCache.invalidateAll();
        return this;
    }

    @Override
    public <T> DataTypeSerializerCollection registerSerializer(DataSerializer<T> serializer) {
        checkNotNull(serializer, "serializer");
        TypeToken<T> typeToken = checkNotNull(serializer.getToken(), "typeToken");
        this.dataTypeSerializers.put(typeToken, new DataSerializerToDataTypeSerializer<>(serializer));
        this.dataSerializers.put(typeToken, serializer);
        this.dataSerializersCache.invalidateAll();
        this.dataTypeSerializersCache.invalidateAll();
        return this;
    }

    @Override
    public <T, D> DataTypeSerializerCollection registerTypeSerializer(TypeToken<T> objectType, DataTypeSerializer<T, D> serializer) {
        checkNotNull(serializer, "serializer");
        checkNotNull(objectType, "objectType");
        this.dataTypeSerializers.put(objectType, serializer);
        // Register the proper wrapped data serializer for the object
        this.dataSerializers.put(objectType, serializer instanceof DataViewTypeSerializer ?
                new WrappedDataSerializer<>(objectType, (DataViewTypeSerializer<T>) serializer) :
                new WrappedContextualDataViewTypeSerializer<>(objectType, serializer));
        this.dataSerializersCache.invalidateAll();
        this.dataTypeSerializersCache.invalidateAll();
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, D> Optional<DataTypeSerializer<T, D>> getTypeSerializer(TypeToken<T> objectType) {
        try {
            return (Optional) this.dataTypeSerializersCache.get(checkNotNull(objectType, "objectType"));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<DataSerializer<T>> getSerializer(TypeToken<T> objectType) {
        try {
            return (Optional) this.dataSerializersCache.get(checkNotNull(objectType, "objectType"));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public DataTypeSerializerContext getTypeSerializerContext() {
        return this.context;
    }
}
