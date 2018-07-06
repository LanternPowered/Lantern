/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.data.LanternDataManager;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.util.roman.IllegalRomanNumberException;
import org.lanternpowered.server.util.roman.RomanNumber;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.util.Coerce;

import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class DataTypeSerializers {

    @SuppressWarnings("unchecked")
    public static void registerSerializers(LanternDataManager dataManager) {
        dataManager.registerTypeSerializer(new TypeToken<Multimap<?,?>>() {}, new MultimapSerializer());
        dataManager.registerTypeSerializer(new TypeToken<Map<?,?>>() {}, new MapSerializer());
        dataManager.registerTypeSerializer(new TypeToken<List<?>>() {}, new ListSerializer());
        dataManager.registerTypeSerializer(new TypeToken<Set<?>>() {}, new SetSerializer());
        dataManager.registerTypeSerializer(new TypeToken<Optional<?>>() {}, new OptionalSerializer());
        dataManager.registerTypeSerializer((TypeToken) new TypeToken<Enum<?>>() {}, new EnumSerializer());
        dataManager.registerTypeSerializer(TypeToken.of(CatalogType.class), new CatalogTypeSerializer());
        dataManager.registerTypeSerializer(TypeToken.of(Number.class), new NumberSerializer());
        dataManager.registerTypeSerializer(TypeToken.of(String.class), new StringSerializer());
        dataManager.registerTypeSerializer(TypeToken.of(Boolean.class), new BooleanSerializer());
        dataManager.registerTypeSerializer(TypeToken.of(DataSerializable.class), new DataSerializableSerializer());
        dataManager.registerTypeSerializer(TypeToken.of(DataView.class), new DataViewSerializer());
        dataManager.registerTypeSerializer(TypeToken.of(CatalogKey.class), new CatalogKeySerializer());
    }

    private static class CatalogKeySerializer implements DataTypeSerializer<CatalogKey, String> {

        @Override
        public CatalogKey deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, String data) throws InvalidDataException {
            return CatalogKey.resolve(data);
        }

        @Override
        public String serialize(TypeToken<?> type, DataTypeSerializerContext ctx, CatalogKey obj) throws InvalidDataException {
            return obj.toString();
        }
    }

    private static class DataViewSerializer implements DataViewTypeSerializer<DataView> {

        @Override
        public DataView deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, DataView data) throws InvalidDataException {
            return data;
        }

        @Override
        public DataView serialize(TypeToken<?> type, DataTypeSerializerContext ctx, DataView obj) throws InvalidDataException {
            return obj;
        }
    }

    private static class DataSerializableSerializer implements DataViewTypeSerializer<DataSerializable> {

        @SuppressWarnings("unchecked")
        @Override
        public DataSerializable deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, DataView data) throws InvalidDataException {
            final DataBuilder<DataSerializable> dataBuilder = (DataBuilder<DataSerializable>) Lantern.getGame().getDataManager()
                    .getBuilder((Class<? extends DataSerializable>) type.getRawType())
                    .orElseThrow(() -> new IllegalStateException("Wasn't able to find a DataBuilder for the DataSerializable: " + type));
            return dataBuilder.build(data).orElseThrow(() -> new InvalidDataException("Unable to deserializer the " + type));
        }

        @Override
        public DataView serialize(TypeToken<?> type, DataTypeSerializerContext ctx, DataSerializable obj) throws InvalidDataException {
            return obj.toContainer();
        }
    }

    private static class CatalogTypeSerializer implements DataTypeSerializer<CatalogType, String> {

        @SuppressWarnings("unchecked")
        @Override
        public CatalogType deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, String data) throws InvalidDataException {
            final Optional<CatalogType> catalogType = Sponge.getRegistry().getType(
                    (Class<CatalogType>) type.getRawType(), CatalogKey.resolve(data));
            if (!catalogType.isPresent()) {
                throw new InvalidDataException("The catalog type " + data + " of type " + type.toString() + " is missing.");
            }
            return catalogType.get();
        }

        @Override
        public String serialize(TypeToken<?> type, DataTypeSerializerContext ctx, CatalogType obj) throws InvalidDataException {
            return obj.getKey().toString();
        }
    }

    private static class MultimapSerializer implements DataTypeSerializer<Multimap<?, ?>, List<DataView>> {

        private static final DataQuery KEY = DataQuery.of("K");
        private static final DataQuery VALUE = DataQuery.of("V");
        private static final DataQuery ENTRIES = DataQuery.of("E");

        private final TypeVariable<Class<Multimap>> keyTypeVariable = Multimap.class.getTypeParameters()[0];
        private final TypeVariable<Class<Multimap>> valueTypeVariable = Multimap.class.getTypeParameters()[1];

        @SuppressWarnings("unchecked")
        @Override
        public Multimap<?, ?> deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, List<DataView> entries) throws InvalidDataException {
            final TypeToken<?> keyType = type.resolveType(this.keyTypeVariable);
            final TypeToken<?> valueType = type.resolveType(this.valueTypeVariable);
            final DataTypeSerializer keySerial = ctx.getSerializers().getTypeSerializer(keyType)
                    .orElseThrow(() -> new IllegalStateException("Wasn't able to find a type serializer for: " + keyType.toString()));
            final DataTypeSerializer valueSerial = ctx.getSerializers().getTypeSerializer(valueType)
                    .orElseThrow(() -> new IllegalStateException("Wasn't able to find a type serializer for: " + valueType.toString()));
            final Multimap map = HashMultimap.create();
            for (DataView entry : entries) {
                final Object key = keySerial.deserialize(type, ctx, entry.get(KEY)
                        .orElseThrow(() -> new InvalidDataException("Entry is missing a key.")));
                if (!entry.contains(VALUE) && entry.contains(ENTRIES)) {
                    throw new InvalidDataException("Entry is missing values.");
                }
                final List<?> dataViews = entry.getList(ENTRIES).orElse(null);
                if (dataViews != null) {
                    dataViews.forEach(o -> {
                        Object value = valueSerial.deserialize(type, ctx, o);
                        map.put(key, value);
                    });
                } else {
                    final Object value = valueSerial.deserialize(type, ctx, entry.get(VALUE).get());
                    map.put(key, value);
                }
            }
            return map;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<DataView> serialize(TypeToken<?> type, DataTypeSerializerContext ctx, Multimap<?, ?> obj) throws InvalidDataException {
            final TypeToken<?> keyType = type.resolveType(this.keyTypeVariable);
            final TypeToken<?> valueType = type.resolveType(this.valueTypeVariable);
            final DataTypeSerializer keySerial = ctx.getSerializers().getTypeSerializer(keyType)
                    .orElseThrow(() -> new IllegalStateException("Wasn't able to find a type serializer for: " + keyType.toString()));
            final DataTypeSerializer valueSerial = ctx.getSerializers().getTypeSerializer(valueType)
                    .orElseThrow(() -> new IllegalStateException("Wasn't able to find a type serializer for: " + valueType.toString()));
            final List<DataView> dataViews = new ArrayList<>();
            for (Object key : obj.keySet()) {
                final DataContainer dataContainer = DataContainer.createNew();
                dataContainer.set(KEY, keySerial.serialize(keyType, ctx, key));
                final Collection<Object> values = ((Multimap) obj).get(key);
                if (values.size() == 1) {
                    dataContainer.set(VALUE, valueSerial.serialize(valueType, ctx, values.iterator().next()));
                } else {
                    dataContainer.set(ENTRIES, values.stream().map(v -> valueSerial.serialize(valueType, ctx, v)).collect(Collectors.toList()));
                }
                dataViews.add(dataContainer);
            }
            return dataViews;
        }
    }

    private static class MapSerializer implements DataTypeSerializer<Map<?, ?>, List<DataView>> {

        private static final DataQuery KEY = DataQuery.of("K");
        private static final DataQuery VALUE = DataQuery.of("V");

        private final TypeVariable<Class<Map>> keyTypeVariable = Map.class.getTypeParameters()[0];
        private final TypeVariable<Class<Map>> valueTypeVariable = Map.class.getTypeParameters()[1];

        @SuppressWarnings("unchecked")
        @Override
        public Map<?, ?> deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, List<DataView> entries) throws InvalidDataException {
            final TypeToken<?> keyType = type.resolveType(this.keyTypeVariable);
            final TypeToken<?> valueType = type.resolveType(this.valueTypeVariable);
            final DataTypeSerializer keySerial = ctx.getSerializers().getTypeSerializer(keyType)
                    .orElseThrow(() -> new IllegalStateException("Wasn't able to find a type serializer for: " + keyType.toString()));
            final DataTypeSerializer valueSerial = ctx.getSerializers().getTypeSerializer(valueType)
                    .orElseThrow(() -> new IllegalStateException("Wasn't able to find a type serializer for: " + valueType.toString()));
            final Map map = new HashMap<>();
            for (DataView entry : entries) {
                final Object key = keySerial.deserialize(type, ctx, entry.get(KEY)
                        .orElseThrow(() -> new InvalidDataException("Entry is missing a key.")));
                final Object value = valueSerial.deserialize(type, ctx, entry.get(VALUE)
                        .orElseThrow(() -> new InvalidDataException("Entry is missing a value.")));
                map.put(key, value);
            }
            return map;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<DataView> serialize(TypeToken<?> type, DataTypeSerializerContext ctx, Map<?, ?> obj) throws InvalidDataException {
            final TypeToken<?> keyType = type.resolveType(this.keyTypeVariable);
            final TypeToken<?> valueType = type.resolveType(this.valueTypeVariable);
            final DataTypeSerializer keySerial = ctx.getSerializers().getTypeSerializer(keyType)
                    .orElseThrow(() -> new IllegalStateException("Wasn't able to find a type serializer for: " + keyType.toString()));
            final DataTypeSerializer valueSerial = ctx.getSerializers().getTypeSerializer(valueType)
                    .orElseThrow(() -> new IllegalStateException("Wasn't able to find a type serializer for: " + valueType.toString()));
            return obj.entrySet().stream().map(entry -> DataContainer.createNew()
                    .set(KEY, keySerial.serialize(keyType, ctx, entry.getKey()))
                    .set(VALUE, valueSerial.serialize(valueType, ctx, entry.getValue()))).collect(Collectors.toList());
        }
    }

    private static class ListSerializer implements DataTypeSerializer<List<?>, List<Object>> {

        private final TypeVariable<Class<List>> typeVariable = List.class.getTypeParameters()[0];

        @SuppressWarnings("unchecked")
        @Override
        public List<?> deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, List<Object> data) throws InvalidDataException {
            final TypeToken<?> elementType = type.resolveType(this.typeVariable);
            final DataTypeSerializer elementSerial = ctx.getSerializers().getTypeSerializer(elementType)
                    .orElseThrow(() -> new IllegalStateException("Wasn't able to find a type serializer for: " + elementType.toString()));
            return (List) data.stream()
                    .map(object -> elementSerial.deserialize(elementType, ctx, object))
                    .collect((Collector) Collectors.toList());
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Object> serialize(TypeToken<?> type, DataTypeSerializerContext ctx, List<?> obj) throws InvalidDataException {
            return obj.stream()
                    .map(object -> {
                        final DataTypeSerializer serializer = ctx.getSerializers().getTypeSerializer(object.getClass())
                                .orElseThrow(() -> new IllegalStateException("Wasn't able to find a type serializer for: " +
                                        object.getClass().toString()));
                        return serializer.serialize(TypeToken.of(object.getClass()), ctx, object);
                    })
                    .collect(Collectors.toList());
        }
    }

    private static class SetSerializer implements DataTypeSerializer<Set<?>, List<Object>> {

        private final TypeVariable<Class<Set>> typeVariable = Set.class.getTypeParameters()[0];

        @SuppressWarnings("unchecked")
        @Override
        public Set<?> deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, List<Object> data) throws InvalidDataException {
            final TypeToken<?> elementType = type.resolveType(this.typeVariable);
            final DataTypeSerializer elementSerial = ctx.getSerializers().getTypeSerializer(elementType)
                    .orElseThrow(() -> new IllegalStateException("Wasn't able to find a type serializer for: " + elementType.toString()));
            return (Set) data.stream()
                    .map(object -> elementSerial.deserialize(elementType, ctx, object))
                    .collect((Collector) Collectors.toSet());
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Object> serialize(TypeToken<?> type, DataTypeSerializerContext ctx, Set<?> obj) throws InvalidDataException {
            return obj.stream()
                    .map(object -> {
                        final DataTypeSerializer serializer = ctx.getSerializers().getTypeSerializer(object.getClass())
                                .orElseThrow(() -> new IllegalStateException("Wasn't able to find a type serializer for: " +
                                        object.getClass().toString()));
                        return serializer.serialize(TypeToken.of(object.getClass()), ctx, object);
                    })
                    .collect(Collectors.toList());
        }
    }

    private static class OptionalSerializer implements DataTypeSerializer<Optional<?>, Object> {

        private final static String EMPTY = "@empty";
        private final TypeVariable<Class<Optional>> typeVariable = Optional.class.getTypeParameters()[0];

        @SuppressWarnings("unchecked")
        @Override
        public Optional<?> deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, Object data) throws InvalidDataException {
            final TypeToken<?> elementType = type.resolveType(this.typeVariable);
            final DataTypeSerializer elementSerial = ctx.getSerializers().getTypeSerializer(elementType)
                    .orElseThrow(() -> new IllegalStateException("Wasn't able to find a type serializer for: " + elementType.toString()));
            return data.toString().equals(EMPTY) ? Optional.empty() :
                    Optional.of(elementSerial.deserialize(elementType, ctx, data));
        }

        @SuppressWarnings({"unchecked", "OptionalUsedAsFieldOrParameterType"})
        @Override
        public Object serialize(TypeToken<?> type, DataTypeSerializerContext ctx, Optional<?> obj) throws InvalidDataException {
            return obj
                    .map(object -> {
                        final DataTypeSerializer serializer = ctx.getSerializers().getTypeSerializer(object.getClass())
                                .orElseThrow(() -> new IllegalStateException("Wasn't able to find a type serializer for: " +
                                        object.getClass().toString()));
                        return serializer.serialize(TypeToken.of(object.getClass()), ctx, object);
                    }).orElse(EMPTY);
        }
    }

    private static class EnumSerializer implements DataTypeSerializer<Enum, String> {

        @SuppressWarnings("unchecked")
        @Override
        public Enum deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, String data) throws InvalidDataException {
            // Enum values should be uppercase
            data = data.toUpperCase();

            final Enum ret;
            try {
                ret = Enum.valueOf(((TypeToken) type).getRawType().asSubclass(Enum.class), data);
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Invalid enum constant, expected a value of enum " + type + ", got " + data);
            }

            return ret;
        }

        @Override
        public String serialize(TypeToken<?> type, DataTypeSerializerContext ctx, Enum obj) throws InvalidDataException {
            return obj.name();
        }
    }

    private static class NumberSerializer implements DataTypeSerializer<Number, Object> {

        private static final DataQuery TYPE = DataQuery.of("Type");
        private static final DataQuery VALUE = DataQuery.of("Value");

        @Override
        public Number deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, Object data) throws InvalidDataException {
            final Class<?> raw = type.getRawType();
            if (data instanceof String) {
                if (double.class.equals(raw) || Double.class.equals(raw)) {
                    return Coerce.asDouble(data).orElseThrow(() -> new InvalidDataException("Invalid double format: " + data));
                } else if (short.class.equals(raw) || Short.class.equals(raw)) {
                    return Coerce.asShort(data).orElseThrow(() -> new InvalidDataException("Invalid short format: " + data));
                } else if (long.class.equals(raw) || Long.class.equals(raw)) {
                    return Coerce.asLong(data).orElseThrow(() -> new InvalidDataException("Invalid long format: " + data));
                } else if (int.class.equals(raw) || Integer.class.equals(raw)) {
                    return Coerce.asInteger(data).orElseThrow(() -> new InvalidDataException("Invalid int format: " + data));
                } else if (byte.class.equals(raw) || Byte.class.equals(raw)) {
                    return Coerce.asByte(data).orElseThrow(() -> new InvalidDataException("Invalid byte format: " + data));
                } else if (float.class.equals(raw) || Float.class.equals(raw)) {
                    return Coerce.asFloat(data).orElseThrow(() -> new InvalidDataException("Invalid float format: " + data));
                } else if (BigInteger.class.equals(raw)) {
                    return BigInteger.valueOf(Coerce.asLong(data)
                            .orElseThrow(() -> new InvalidDataException("Invalid big integer format: " + data)));
                } else if (BigDecimal.class.equals(raw)) {
                    return BigDecimal.valueOf(Coerce.asDouble(data)
                            .orElseThrow(() -> new InvalidDataException("Invalid big decimal format: " + data)));
                } else if (RomanNumber.class.equals(raw)) {
                    try {
                        try {
                            return RomanNumber.parse((String) data);
                        } catch (NumberFormatException e) {
                            return RomanNumber.valueOf(Coerce.asInteger(data)
                                    .orElseThrow(() -> new InvalidDataException("Invalid int format: " + data)));
                        }
                    } catch (IllegalRomanNumberException e) {
                        throw new InvalidDataException(e);
                    }
                }
            } else if (data instanceof DataView) {
                final DataView view = (DataView) data;
                if (view.contains(TYPE) && view.contains(VALUE)) {
                    final String numberType = view.getString(TYPE).get();
                    final String value = view.getString(VALUE).get();
                    if (numberType.equals(BigDecimal.class.getSimpleName())) {
                        return new BigDecimal(value);
                    } else if (numberType.equals(BigInteger.class.getSimpleName())) {
                        return new BigInteger(value);
                    } else {
                        throw new InvalidDataException("Unsupported number type: " + numberType);
                    }
                }
                throw new InvalidDataException("Unsupported number format: " + view);
            } else if (data instanceof Number) {
                final Number number = (Number) data;
                if (double.class.equals(raw) || Double.class.equals(raw)) {
                    return number.doubleValue();
                } else if (short.class.equals(raw) || Short.class.equals(raw)) {
                    return number.shortValue();
                } else if (long.class.equals(raw) || Long.class.equals(raw)) {
                    return number.longValue();
                } else if (int.class.equals(raw) || Integer.class.equals(raw)) {
                    return number.intValue();
                } else if (byte.class.equals(raw) || Byte.class.equals(raw)) {
                    return number.byteValue();
                } else if (float.class.equals(raw) || Float.class.equals(raw)) {
                    return number.floatValue();
                } else if (BigInteger.class.equals(raw)) {
                    return BigInteger.valueOf(number.longValue());
                } else if (BigDecimal.class.equals(raw)) {
                    return BigDecimal.valueOf(number.doubleValue());
                } else if (RomanNumber.class.equals(raw)) {
                    return RomanNumber.valueOf(number.intValue());
                }
                throw new InvalidDataException("Unsupported number type: " + type.getRawType().getName());
            }
            throw new InvalidDataException("Unsupported data type: " + data.getClass().getName());
        }

        @Override
        public Object serialize(TypeToken<?> type, DataTypeSerializerContext ctx, Number obj) throws InvalidDataException {
            if (obj instanceof BigDecimal || obj instanceof BigInteger) {
                return DataContainer.createNew()
                        .set(TYPE, obj.getClass().getSimpleName())
                        .set(VALUE, obj.toString());
            }
            return obj;
        }
    }

    private static class BooleanSerializer implements DataTypeSerializer<Boolean, Object> {

        @Override
        public Boolean deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, Object data) throws InvalidDataException {
            return Coerce.asBoolean(data).orElseThrow(() -> new InvalidDataException("Invalid boolean data format: " + data));
        }

        @Override
        public Object serialize(TypeToken<?> type, DataTypeSerializerContext ctx, Boolean obj) throws InvalidDataException {
            return obj;
        }
    }

    private static class StringSerializer implements DataTypeSerializer<String, Object> {

        @Override
        public String deserialize(TypeToken<?> type, DataTypeSerializerContext ctx, Object data) throws InvalidDataException {
            return data.toString();
        }

        @Override
        public Object serialize(TypeToken<?> type, DataTypeSerializerContext ctx, String obj) throws InvalidDataException {
            return obj;
        }
    }
}
