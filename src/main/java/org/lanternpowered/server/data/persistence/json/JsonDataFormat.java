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
package org.lanternpowered.server.data.persistence.json;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.lanternpowered.server.data.persistence.AbstractStringDataFormat;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

public class JsonDataFormat extends AbstractStringDataFormat {

    public JsonDataFormat(String identifier) {
        super(identifier);
    }

    @Override
    public DataContainer readFrom(Reader input) throws InvalidDataException, IOException {
        try (JsonReader reader = new JsonReader(input)) {
            return readContainer(reader);
        }
    }

    public static DataContainer serialize(Gson gson, Object o) throws IOException {
        final DataViewJsonWriter writer = new DataViewJsonWriter();
        gson.toJson(o, o.getClass(), writer);
        return writer.getResult();
    }

    /**
     * Reads a {@link Object} from the {@link JsonReader}.
     *
     * @param reader The json reader
     * @return The object
     */
    public static Optional<Object> read(JsonReader reader) throws IOException {
        return Optional.ofNullable(read0(reader));
    }

    /**
     * Reads a {@link Object} from the {@link JsonReader}.
     *
     * @param input The input
     * @param lenient Enable lenient parsing
     * @return The object
     */
    public static Optional<Object> read(String input, boolean lenient) throws IOException {
        return read(createReader(input, lenient));
    }

    /**
     * Reads a {@link DataContainer} from the {@link JsonReader}.
     *
     * @param reader The json reader
     * @return The data container
     */
    public static DataContainer readContainer(JsonReader reader) throws IOException {
        final DataContainer container = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        readView(reader, container);
        return container;
    }

    /**
     * Parses the input string.
     *
     * @param input The input
     * @param lenient Enable lenient parsing
     * @return The data container
     */
    public static DataContainer readContainer(String input, boolean lenient) throws IOException {
        return readContainer(createReader(input, lenient));
    }

    private static JsonReader createReader(String content, boolean lenient) {
        final JsonReader jsonReader = new JsonReader(new StringReader(content));
        if (lenient) {
            jsonReader.setLenient(true);
        }
        return jsonReader;
    }

    private static void readView(JsonReader reader, DataView view) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            DataQuery key = DataQuery.of(reader.nextName());

            if (reader.peek() == JsonToken.BEGIN_OBJECT) {
                // Check this early so we don't need to copy the view
                readView(reader, view.createView(key));
            } else {
                view.set(key, read0(reader));
            }
        }
        reader.endObject();
    }

    @Nullable
    private static Object read0(JsonReader reader) throws IOException {
        final JsonToken token = reader.peek();
        switch (token) {
            case BEGIN_OBJECT:
                return readContainer(reader);
            case BEGIN_ARRAY:
                return readArray(reader);
            case BOOLEAN:
                return reader.nextBoolean();
            case NULL:
                reader.nextNull();
                return null;
            case STRING:
                return reader.nextString();
            case NUMBER:
                return readNumber(reader);
            default:
                throw new IOException("Unexpected token: " + token);
        }
    }

    private static Number readNumber(JsonReader reader) throws IOException {
        // Similar to https://github.com/zml2008/configurate/blob/master/configurate-gson/src/main/java/ninja/leaping/configurate/gson/GsonConfigurationLoader.java#L113
        // Not sure what's the best way to detect the type of number
        double nextDouble = reader.nextDouble();
        int nextInt = (int) nextDouble;
        if (nextInt == nextDouble) {
            return nextInt;
        }

        long nextLong = (long) nextDouble;
        if (nextLong == nextDouble) {
            return nextLong;
        }

        return nextDouble;
    }

    private static List<?> readArray(JsonReader reader) throws IOException {
        reader.beginArray();
        final List<Object> result = new ArrayList<>();
        while (reader.hasNext()) {
            result.add(read0(reader));
        }
        reader.endArray();
        return result;
    }

    @Override
    public void writeTo(Writer output, DataView data) throws IOException {
        try (JsonWriter writer = new JsonWriter(output)) {
            writeView(writer, data);
        }
    }

    private static void writeView(JsonWriter writer, DataView view) throws IOException {
        writer.beginObject();
        for (Map.Entry<DataQuery, Object> entry : view.getValues(false).entrySet()) {
            writer.name(entry.getKey().asString('.'));
            write(writer, entry.getValue());
        }
        writer.endObject();
    }

    /**
     * Writes the {@link Object} as a string.
     *
     * @param value The value
     * @return The string
     */
    public static String writeAsString(@Nullable Object value) throws IOException {
        final StringWriter writer = new StringWriter();
        final JsonWriter jsonWriter = new JsonWriter(writer);
        write(jsonWriter, value);
        return writer.toString();
    }

    public static void write(JsonWriter writer, @Nullable Object value) throws IOException {
        if (value == null) {
            writer.nullValue();
        } else if (value instanceof Boolean) {
            writer.value((Boolean) value);
        } else if (value instanceof Number) {
            writer.value((Number) value);
        } else if (value instanceof String) {
            writer.value((String) value);
        } else if (value instanceof Iterable) {
            writeArray(writer, (Iterable<?>) value);
        } else if (value instanceof Map) {
            writeMap(writer, (Map<?, ?>) value);
        } else if (value instanceof DataSerializable) {
            writeView(writer, ((DataSerializable) value).toContainer());
        } else if (value instanceof DataView) {
            writeView(writer, (DataView) value);
        } else {
            throw new IllegalArgumentException("Unable to translate object to JSON: " + value);
        }
    }

    private static void writeArray(JsonWriter writer, Iterable<?> iterable) throws IOException {
        writer.beginArray();
        for (Object value : iterable) {
            write(writer, value);
        }
        writer.endArray();
    }

    private static void writeMap(JsonWriter writer, Map<?, ?> map) throws IOException {
        writer.beginObject();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            if (key instanceof DataQuery) {
                key = ((DataQuery) key).asString('.');
            }
            writer.name(key.toString());
            write(writer, entry.getValue());
        }
        writer.endObject();
    }
}
