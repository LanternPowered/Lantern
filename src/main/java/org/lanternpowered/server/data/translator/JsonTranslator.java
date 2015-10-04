package org.lanternpowered.server.data.translator;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.translator.DataTranslator;
import org.spongepowered.api.util.Coerce;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonTranslator implements DataTranslator<JsonObject> {

    private static final JsonTranslator INSTANCE = new JsonTranslator();

    private static final Pattern DOUBLE = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[d|D]");
    private static final Pattern DOUBLE_UNTYPED = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
    private static final Pattern FLOAT = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[f|F]");
    private static final Pattern LONG = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[l|L]");
    private static final Pattern BYTE = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[b|B]");
    private static final Pattern SHORT = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[s|S]");
    private static final Pattern INTEGER = Pattern.compile("[-+]?[0-9]+");

    /**
     * Get the instance of this translator.
     *
     * @return The instance of this translator
     */
    public static JsonTranslator instance() {
        return INSTANCE;
    }

    private JsonTranslator() {
    }

    @Override
    public JsonObject translateData(DataView container) {
        return this.toJson(container.getValues(false)).getAsJsonObject();
    }

    @Override
    public void translateContainerToData(JsonObject node, DataView container) {
        this.toJson(node, container.getValues(false));
    }

    @Override
    public DataView translateFrom(JsonObject node) {
        return (DataView) this.fromJson(null, node);
    }

    private Object fromJson(@Nullable DataView container, JsonElement json) {
        if (json.isJsonObject()) {
            if (container == null) {
                container = new MemoryDataContainer();
            }
            for (Entry<String, JsonElement> en : json.getAsJsonObject().entrySet()) {
                String key = en.getKey();
                JsonElement element = en.getValue();
                if (element.isJsonObject()) {
                    this.fromJson(container.createView(DataQuery.of(key)), element);
                } else {
                    container.set(DataQuery.of(key), this.fromJson(null, json));
                }
            }
        } else if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            List<Object> objects = Lists.newArrayListWithCapacity(array.size());
            int ints = 0;
            int bytes = 0;
            for (int i = 0; i < array.size(); i++) {
                Object object = this.fromJson(null, array.get(i));
                objects.add(object);
                if (object instanceof Integer) {
                    ints++;
                }
                if (object instanceof Byte) {
                    bytes++;
                }
            }
            if (bytes == objects.size()) {
                Byte[] array0 = new Byte[bytes];
                for (int i = 0; i < bytes; i++) {
                    array0[i] = (Byte) objects.get(i);
                }
                return array0;
            } else if (ints == objects.size()) {
                Integer[] array0 = new Integer[ints];
                for (int i = 0; i < bytes; i++) {
                    array0[i] = (Integer) objects.get(i);
                }
                return array0;
            } else {
                return objects;
            }
        } else if (json.isJsonPrimitive()) {
            String value = json.getAsString();
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
                Optional<Boolean> value0 = Coerce.asBoolean(value);
                if (value0.isPresent()) {
                    return value0.get();
                }
                return value;
            }
        }
        return json;
    }

    @SuppressWarnings("unchecked")
    private JsonElement toJson(Object object) {
        if (object instanceof DataView || object instanceof DataSerializable || object instanceof Map<?,?>) {
            Map<DataQuery, Object> map;
            if (object instanceof DataView) {
                map = ((DataView) object).getValues(false);
            } else if (object instanceof DataSerializable) {
                map = ((DataSerializable) object).toContainer().getValues(false);
            } else {
                map = (Map<DataQuery, Object>) object;
            }
            return this.toJson(new JsonObject(), map);
        } else if (object instanceof List<?>) {
            JsonArray array = new JsonArray();
            List<?> object0 = (List<?>) object;
            for (int i = 0; i < object0.size(); i++) {
                array.add(this.toJson(object0.get(i)));
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
            Integer[] object0 = (Integer[]) object;
            JsonArray array = new JsonArray();
            for (int i = 0; i < object0.length; i++) {
                array.add(new JsonPrimitive(object0[i].toString()));
            }
            return array;
        } else if (object instanceof Byte[]) {
            Byte[] object0 = (Byte[]) object;
            JsonArray array = new JsonArray();
            for (int i = 0; i < object0.length; i++) {
                array.add(new JsonPrimitive(object0[i].toString() + "b"));
            }
            return array;
        } else {
            return new JsonPrimitive(object.toString());
        }
    }

    private JsonObject toJson(JsonObject json, Map<DataQuery, Object> map) {
        for (Entry<DataQuery, Object> en : map.entrySet()) {
            json.add(en.getKey().asString('.'), this.toJson(en.getValue()));
        }
        return json;
    }
}
