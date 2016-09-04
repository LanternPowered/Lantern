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

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.util.IdGenerator;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class SimpleDataTypeSerializerCollection implements DataTypeSerializerCollection {

    private final static DataQuery WRAPPED_VALUE = DataQuery.of("WrappedValue");

    /**
     * The {@link DataTypeSerializerContext}.
     */
    private final DataTypeSerializerContext context = () -> SimpleDataTypeSerializerCollection.this;

    private class WrappedDataSerializer<T> extends AbstractDataTranslator<T> {

        private final DataViewTypeSerializer<T> serializer;

        public WrappedDataSerializer(String pluginId, String name, TypeToken<T> typeToken, DataViewTypeSerializer<T> serializer) {
            super(pluginId, name, typeToken);
            this.serializer = serializer;
        }

        @Override
        public T translate(DataView view) throws InvalidDataException {
            return this.serializer.deserialize(this.typeToken, context, view);
        }

        @Override
        public DataContainer translate(T obj) throws InvalidDataException {
            final DataView dataView = this.serializer.serialize(this.typeToken, context, obj);
            return dataView instanceof DataContainer ? (DataContainer) dataView : dataView.copy();
        }
    }

    /**
     * Wraps around a regular {@link DataTypeSerializer}, so not extending {@link DataViewTypeSerializer}
     * and is used to be compatible with the {@link DataTranslator} api, it serializes the object that isn't a {@link DataView}
     * and puts it inside a newly created {@link DataContainer}.
     */
    private class WrappedContextualDataViewTypeSerializer<T, D> extends AbstractDataTranslator<T> implements DataViewTypeSerializer<T> {

        private final DataTypeSerializer<T, D> serializer;

        public WrappedContextualDataViewTypeSerializer(String pluginId, String name, TypeToken<T> typeToken,
                DataTypeSerializer<T, D> serializer) {
            super(pluginId, name, typeToken);
            this.serializer = serializer;
        }

        public WrappedContextualDataViewTypeSerializer(String pluginId, String id, String name, TypeToken<T> typeToken,
                DataTypeSerializer<T, D> serializer) {
            super(pluginId, id, name, typeToken);
            this.serializer = serializer;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, DataView dataView) throws InvalidDataException {
            final Optional<Object> optObj = dataView.get(WRAPPED_VALUE);
            if (optObj.isPresent()) {
                throw new InvalidDataException("The wrapped value object is missing");
            }
            return this.serializer.deserialize(type, ctx, (D) optObj.get());
        }

        @Override
        public DataView serialize(TypeToken<?> type, DataTypeSerializerContext ctx, T obj) throws InvalidDataException {
            return new MemoryDataContainer().set(WRAPPED_VALUE, this.serializer.serialize(type, ctx, obj));
        }

        @Override
        public T translate(DataView view) throws InvalidDataException {
            return this.deserialize(this.typeToken, context, view);
        }

        @Override
        public DataContainer translate(T obj) throws InvalidDataException {
            final DataView dataView = this.serialize(this.typeToken, context, obj);
            return dataView instanceof DataContainer ? (DataContainer) dataView : dataView.copy();
        }
    }

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
    private final Map<TypeToken<?>, DataTranslator<?>> dataSerializers = new ConcurrentHashMap<>();
    private final Set<Function<TypeToken<?>, Optional<DataTypeSerializer<?,?>>>> serializerFactories =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final LoadingCache<TypeToken<?>, Optional<DataTypeSerializer<?,?>>> dataTypeSerializersCache =
            Caffeine.newBuilder().build(new CacheLoader<TypeToken<?>, Optional<DataTypeSerializer<?,?>>>() {
                @SuppressWarnings("unchecked")
                @Override
                public Optional<DataTypeSerializer<?,?>> load(TypeToken<?> key) throws Exception {
                    for (Map.Entry<TypeToken<?>, DataTypeSerializer<?,?>> entry : dataTypeSerializers.entrySet()) {
                        if (entry.getKey().isAssignableFrom(key)) {
                            return Optional.of(entry.getValue());
                        }
                    }
                    for (Function<TypeToken<?>, Optional<DataTypeSerializer<?,?>>> function : serializerFactories) {
                        final DataTypeSerializer<?,?> serializer = function.apply(key).orElse(null);
                        if (serializer != null) {
                            final Optional<DataTranslator<?>> dataSerializer = dataTranslatorsCache.getIfPresent(key);
                            if (dataSerializer == null) {
                                final String id = IdGenerator.generate(key.getRawType().getName());
                                dataTranslatorsCache.put(key, Optional.of(serializer instanceof DataViewTypeSerializer ?
                                        new WrappedDataSerializer("lantern", id, (TypeToken) key, (DataViewTypeSerializer<?>) serializer) :
                                        new WrappedContextualDataViewTypeSerializer<>("lantern", id, (TypeToken) key, serializer)));
                            }
                            return Optional.of(serializer);
                        }
                    }
                    return Optional.empty();
                }
            });

    private final LoadingCache<TypeToken<?>, Optional<DataTranslator<?>>> dataTranslatorsCache =
            Caffeine.newBuilder().build(new CacheLoader<TypeToken<?>, Optional<DataTranslator<?>>>() {
                @SuppressWarnings("unchecked")
                @Override
                public Optional<DataTranslator<?>> load(TypeToken<?> key) throws Exception {
                    for (Map.Entry<TypeToken<?>, DataTranslator<?>> entry : dataSerializers.entrySet()) {
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
                            final String id = IdGenerator.generate(key.getRawType().getName());
                            return Optional.of(serializer instanceof DataViewTypeSerializer ?
                                    new WrappedDataSerializer("lantern", id, (TypeToken) key, (DataViewTypeSerializer<?>) serializer) :
                                    new WrappedContextualDataViewTypeSerializer<>("lantern", id, (TypeToken) key, serializer));
                        }
                    }
                    return Optional.empty();
                }
            });

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
        TypeToken<T> typeToken = checkNotNull(serializer.getToken(), "typeToken");
        this.dataTypeSerializers.put(typeToken, new DataSerializerToDataTypeSerializer<>(serializer));
        this.dataSerializers.put(typeToken, serializer);
        this.dataTranslatorsCache.invalidateAll();
        this.dataTypeSerializersCache.invalidateAll();
        return this;
    }

    @Override
    public <T, D> DataTypeSerializerCollection registerTypeSerializer(TypeToken<T> objectType, DataTypeSerializer<T, D> serializer) {
        checkNotNull(serializer, "serializer");
        checkNotNull(objectType, "objectType");
        this.dataTypeSerializers.put(objectType, serializer);
        // Register the proper wrapped data serializer for the object
        final String id = IdGenerator.generate(objectType.getRawType().getName());
        this.dataSerializers.put(objectType, serializer instanceof DataViewTypeSerializer ?
                new WrappedDataSerializer<>("lantern", id, objectType, (DataViewTypeSerializer<T>) serializer) :
                new WrappedContextualDataViewTypeSerializer<>("lantern", id, objectType, serializer));
        this.dataTranslatorsCache.invalidateAll();
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
