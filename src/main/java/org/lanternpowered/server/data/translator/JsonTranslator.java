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
package org.lanternpowered.server.data.translator;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.lanternpowered.server.data.persistence.AbstractDataTranslator;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class JsonTranslator extends AbstractDataTranslator<JsonObject> {

    private static final JsonTranslator INSTANCE = new JsonTranslator();

    /**
     * Get the instance of this translator.
     *
     * @return The instance of this translator
     */
    public static JsonTranslator instance() {
        return INSTANCE;
    }

    private static final Pattern DOUBLE = Pattern.compile("^[-+]?[0-9]*\\.?[0-9]+[dD]$");
    private static final Pattern DOUBLE_UNTYPED = Pattern.compile("^[-+]?[0-9]*\\.?[0-9]+$");
    private static final Pattern FLOAT = Pattern.compile("^[-+]?[0-9]*\\.?[0-9]+[fF]$");
    private static final Pattern LONG = Pattern.compile("^[-+]?[0-9]+[lL]$");
    private static final Pattern BYTE = Pattern.compile("^[-+]?[0-9]+[bB]$");
    private static final Pattern SHORT = Pattern.compile("^[-+]?[0-9]+[sS]$");
    private static final Pattern INTEGER = Pattern.compile("^[-+]?[0-9]+$");

    private JsonTranslator() {
        super("lantern", "json", TypeToken.of(JsonObject.class));
    }

    @Override
    public JsonObject translate(DataView view) throws InvalidDataException {
        return toJson(view.getValues(false)).getAsJsonObject();
    }

    @Override
    public DataContainer translate(JsonObject obj) throws InvalidDataException {
        //noinspection ConstantConditions
        return (DataContainer) fromJson(obj);
    }

    @Nullable
    public static Object fromJson(JsonElement json) {
        return fromJson(null, json);
    }

    @Nullable
    private static Object fromJson(@Nullable DataView container, JsonElement json) {
        if (json.isJsonObject()) {
            if (container == null) {
                container = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
            }
            for (Entry<String, JsonElement> en : json.getAsJsonObject().entrySet()) {
                final String key = en.getKey();
                final JsonElement element = en.getValue();
                if (element.isJsonObject()) {
                    fromJson(container.createView(DataQuery.of(key)), element);
                } else {
                    //noinspection ConstantConditions
                    container.set(DataQuery.of(key), fromJson(element));
                }
            }
            return container;
        } else if (json.isJsonArray()) {
            final JsonArray array = json.getAsJsonArray();
            final List<Object> objects = Lists.newArrayListWithCapacity(array.size());
            int ints = 0;
            int bytes = 0;
            for (int i = 0; i < array.size(); i++) {
                final Object object = fromJson(array.get(i));
                objects.add(object);
                if (object instanceof Integer) {
                    ints++;
                }
                if (object instanceof Byte) {
                    bytes++;
                }
            }
            if (bytes == objects.size()) {
                final Byte[] array0 = new Byte[bytes];
                for (int i = 0; i < bytes; i++) {
                    array0[i] = (Byte) objects.get(i);
                }
                return array0;
            } else if (ints == objects.size()) {
                final Integer[] array0 = new Integer[ints];
                for (int i = 0; i < bytes; i++) {
                    array0[i] = (Integer) objects.get(i);
                }
                return array0;
            } else {
                return objects;
            }
        } else if (json.isJsonPrimitive()) {
            final String value = json.getAsString();
            if (DOUBLE.matcher(value).matches()) {
                return Double.parseDouble(value.substring(0, value.length() - 1));
            } else if (FLOAT.matcher(value).matches()) {
                return Float.parseFloat(value.substring(0, value.length() - 1));
            } else if (LONG.matcher(value).matches()) {
                return Long.parseLong(value.substring(0, value.length() - 1));
            } else if (SHORT.matcher(value).matches()) {
                return Short.parseShort(value.substring(0, value.length() - 1));
            } else if (BYTE.matcher(value).matches()) {
                return Byte.parseByte(value.substring(0, value.length() - 1));
            } else if (INTEGER.matcher(value).matches()) {
                return Integer.parseInt(value);
            } else if (DOUBLE_UNTYPED.matcher(value).matches()) {
                return Double.parseDouble(value);
            } else {
                if ("true".equalsIgnoreCase(value)) {
                    return true;
                } else if ("false".equalsIgnoreCase(value)) {
                    return false;
                }
                return value;
            }
        } else if (json.isJsonNull()) {
            return null;
        }
        throw new IllegalArgumentException();
    }

    @SuppressWarnings("unchecked")
    private JsonElement toJson(@Nullable Object object) {
        if (object == null) {
            return JsonNull.INSTANCE;
        } else if (object instanceof DataView || object instanceof DataSerializable || object instanceof Map<?,?>) {
            final Map<DataQuery, Object> map;
            if (object instanceof DataView) {
                map = ((DataView) object).getValues(false);
            } else if (object instanceof DataSerializable) {
                map = ((DataSerializable) object).toContainer().getValues(false);
            } else {
                map = (Map<DataQuery, Object>) object;
            }
            return toJson(new JsonObject(), map);
        } else if (object instanceof List<?>) {
            final JsonArray array = new JsonArray();
            final List<?> object0 = (List<?>) object;
            for (Object anObject0 : object0) {
                array.add(toJson(anObject0));
            }
            return array;
        } else if (object instanceof Byte) {
            return new JsonPrimitive(object.toString() + "b");
        } else if (object instanceof Short) {
            return new JsonPrimitive(object.toString() + "s");
        } else if (object instanceof Integer) {
            return new JsonPrimitive(object.toString());
        } else if (object instanceof Long) {
            return new JsonPrimitive(object.toString() + "l");
        } else if (object instanceof Double) {
            return new JsonPrimitive(object.toString() + "d");
        } else if (object instanceof Float) {
            return new JsonPrimitive(object.toString() + "f");
        } else if (object instanceof Integer[]) {
            final Integer[] object0 = (Integer[]) object;
            final JsonArray array = new JsonArray();
            for (Integer anObject0 : object0) {
                array.add(new JsonPrimitive(anObject0.toString()));
            }
            return array;
        } else if (object instanceof Byte[]) {
            final Byte[] object0 = (Byte[]) object;
            final JsonArray array = new JsonArray();
            for (Byte anObject0 : object0) {
                array.add(new JsonPrimitive(anObject0.toString() + "b"));
            }
            return array;
        } else {
            return new JsonPrimitive(object.toString());
        }
    }

    private JsonObject toJson(JsonObject json, Map<DataQuery, Object> map) {
        for (Entry<DataQuery, Object> en : map.entrySet()) {
            json.add(en.getKey().asString('.'), toJson(en.getValue()));
        }
        return json;
    }
}
