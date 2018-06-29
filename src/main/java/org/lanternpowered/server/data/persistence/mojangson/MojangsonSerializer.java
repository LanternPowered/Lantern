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
package org.lanternpowered.server.data.persistence.mojangson;

import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_ARRAY_CLOSE;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_ARRAY_OPEN;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_ARRAY_TYPE_SUFFIX;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_BOOLEAN;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_BYTE;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_BYTE_UPPER;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_CHAR;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_CHAR_QUOTE;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_DOUBLE;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_DOUBLE_QUOTED_STRING;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_FLOAT;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_INT;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_INT_UPPER;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_KEY_VALUE_SEPARATOR;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_LONG;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_LONG_UPPER;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_MAP_ARRAY;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_MAP_CLOSE;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_MAP_OPEN;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_NEW_ENTRY;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_SHORT;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_STRING_ARRAY;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_VIEW_ARRAY;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_VIEW_CLOSE;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_VIEW_OPEN;

import com.google.common.base.Joiner;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings({"unchecked", "StringBufferReplaceableByString"})
final class MojangsonSerializer {

    private static final String ENTRY_SEPARATOR = TOKEN_NEW_ENTRY + "";

    static String toMojangson(Object object) {
        return toMojangson(object, true).toString();
    }

    static String toLanterson(Object object) {
        return toMojangson(object, false).toString();
    }

    private static final class Entry {

        final String suffix;
        final String value;

        private Entry(ExtendedObjectType type, String value) {
            this(type.suffix, value);
        }

        private Entry(String suffix, String value) {
            this.suffix = suffix;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    /**
     * Converts the given {@link Object} into mojangson.
     *
     * @param object The object
     * @param vanilla Whether the format should be parsable on vanilla
     * @return The mojangson string
     */
    private static Object toMojangson(Object object, boolean vanilla) {
        if (object instanceof Boolean || object instanceof Integer) {
            return object.toString();
        } else if (object instanceof Character) {
            if (vanilla) {
                return new Entry(ExtendedObjectType.CHAR, quoteStringIfNeeded(object.toString()));
            }
            return TOKEN_CHAR_QUOTE + object.toString() + TOKEN_CHAR_QUOTE;
        } else if (object instanceof Byte) {
            return object.toString() + TOKEN_BYTE;
        } else if (object instanceof Short) {
            return object.toString() + TOKEN_SHORT;
        } else if (object instanceof Long) {
            return object.toString() + TOKEN_LONG;
        } else if (object instanceof Float) {
            return object.toString() + TOKEN_FLOAT;
        } else if (object instanceof Number) {
            final String value = object.toString();
            if (value.indexOf('.') == -1) {
                return value + TOKEN_DOUBLE;
            }
            return value;
        } else if (object instanceof String) {
            return quoteStringIfNeeded((String) object);
        } else if (object instanceof String[]) {
            final StringBuilder builder = new StringBuilder().append(TOKEN_ARRAY_OPEN);
            if (!vanilla) {
                builder.append(TOKEN_STRING_ARRAY).append(TOKEN_ARRAY_TYPE_SUFFIX);
            }
            builder.append(Joiner.on(TOKEN_NEW_ENTRY).join((String[]) object)).append(TOKEN_ARRAY_CLOSE);
            final String value = builder.toString();
            return vanilla ? new Entry(ExtendedObjectType.STRING_ARRAY, value) : value;
        } else if (object instanceof byte[]) {
            final StringBuilder builder = new StringBuilder();
            builder.append(TOKEN_ARRAY_OPEN).append(vanilla ? TOKEN_BYTE_UPPER : TOKEN_BYTE).append(TOKEN_ARRAY_TYPE_SUFFIX);
            final byte[] bytes = (byte[]) object;
            for (int i = 0; i < bytes.length; i++) {
                if (i != 0) {
                    builder.append(TOKEN_NEW_ENTRY);
                }
                builder.append(bytes[i]);
            }
            return builder.append(TOKEN_ARRAY_CLOSE).toString();
        } else if (object instanceof short[]) {
            final StringBuilder builder = new StringBuilder().append(TOKEN_ARRAY_OPEN);
            if (!vanilla) {
                builder.append(TOKEN_SHORT).append(TOKEN_ARRAY_TYPE_SUFFIX);
            }
            builder.append(Shorts.join(ENTRY_SEPARATOR, (short[]) object)).append(TOKEN_ARRAY_CLOSE).toString();
            final String value = builder.toString();
            return vanilla ? new Entry(ExtendedObjectType.SHORT_ARRAY, value) : value;
        } else if (object instanceof int[]) {
            return new StringBuilder()
                    .append(TOKEN_ARRAY_OPEN).append(vanilla ? TOKEN_INT_UPPER : TOKEN_INT).append(TOKEN_ARRAY_TYPE_SUFFIX)
                    .append(Ints.join(ENTRY_SEPARATOR, (int[]) object))
                    .append(TOKEN_ARRAY_CLOSE)
                    .toString();
        } else if (object instanceof long[]) {
            return new StringBuilder()
                    .append(TOKEN_ARRAY_OPEN).append(vanilla ? TOKEN_LONG_UPPER : TOKEN_LONG).append(TOKEN_ARRAY_TYPE_SUFFIX)
                    .append(Longs.join(ENTRY_SEPARATOR, (long[]) object))
                    .append(TOKEN_ARRAY_CLOSE)
                    .toString();
        } else if (object instanceof float[]) {
            final StringBuilder builder = new StringBuilder().append(TOKEN_ARRAY_OPEN);
            if (!vanilla) {
                builder.append(TOKEN_FLOAT).append(TOKEN_ARRAY_TYPE_SUFFIX);
            }
            builder.append(Floats.join(ENTRY_SEPARATOR, (float[]) object)).append(TOKEN_ARRAY_CLOSE);
            final String value = builder.toString();
            return vanilla ? new Entry(ExtendedObjectType.FLOAT_ARRAY, value) : value;
        } else if (object instanceof double[]) {
            final StringBuilder builder = new StringBuilder().append(TOKEN_ARRAY_OPEN);
            if (!vanilla) {
                builder.append(TOKEN_DOUBLE).append(TOKEN_ARRAY_TYPE_SUFFIX);
            }
            builder.append(Doubles.join(ENTRY_SEPARATOR, (double[]) object)).append(TOKEN_ARRAY_CLOSE);
            final String value = builder.toString();
            return vanilla ? new Entry(ExtendedObjectType.DOUBLE_ARRAY, value) : value;
        } else if (object instanceof boolean[]) {
            final StringBuilder builder = new StringBuilder().append(TOKEN_ARRAY_OPEN);
            if (!vanilla) {
                builder.append(TOKEN_BOOLEAN).append(TOKEN_ARRAY_TYPE_SUFFIX);
            }
            builder.append(Booleans.join(ENTRY_SEPARATOR, (boolean[]) object)).append(TOKEN_ARRAY_CLOSE);
            final String value = builder.toString();
            return vanilla ? new Entry(ExtendedObjectType.BOOLEAN_ARRAY, value) : value;
        } else if (object instanceof char[]) {
            final StringBuilder builder = new StringBuilder().append(TOKEN_ARRAY_OPEN);
            if (!vanilla) {
                builder.append(TOKEN_CHAR).append(TOKEN_ARRAY_TYPE_SUFFIX);
            }
            final char[] chars = (char[]) object;
            for (int i = 0; i < chars.length; i++) {
                if (i != 0) {
                    builder.append(TOKEN_NEW_ENTRY);
                }
                if (vanilla) {
                    builder.append(quoteStringIfNeeded(chars[i] + ""));
                } else {
                    builder.append(TOKEN_CHAR_QUOTE).append(chars[i]).append(TOKEN_CHAR_QUOTE);
                }
            }
            final String value = builder.append(TOKEN_ARRAY_CLOSE).toString();
            return vanilla ? new Entry(ExtendedObjectType.CHAR_ARRAY, value) : value;
        } else if (object instanceof Collection) {
            final StringBuilder builder = new StringBuilder();
            builder.append(TOKEN_ARRAY_OPEN);
            final Iterator<Object> it = ((Collection<Object>) object).iterator();
            String suffix = null;
            while (it.hasNext()) {
                final Object element = toMojangson(it.next(), vanilla);
                if (element instanceof Entry) {
                    suffix = ((Entry) element).suffix;
                }
                builder.append(element);
                if (it.hasNext()) {
                    builder.append(TOKEN_NEW_ENTRY);
                }
            }
            final String value = builder.append(TOKEN_ARRAY_CLOSE).toString();
            return vanilla && suffix != null ? new Entry("List$" + suffix, value) : value;
        } else if (object instanceof DataView) {
            final StringBuilder builder = new StringBuilder();
            builder.append(TOKEN_VIEW_OPEN);
            final Map<DataQuery, Object> map = ((DataView) object).getValues(false);
            final Iterator<Map.Entry<DataQuery, Object>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<DataQuery, Object> entry = it.next();
                String key = entry.getKey().toString();
                final Object value = toMojangson(entry.getValue(), vanilla);
                if (value instanceof Entry) {
                    key += '$' + ((Entry) value).suffix;
                }
                builder.append(quoteStringIfNeeded(key));
                builder.append(TOKEN_KEY_VALUE_SEPARATOR);
                builder.append(value);
                if (it.hasNext()) {
                    builder.append(TOKEN_NEW_ENTRY);
                }
            }
            return builder.append(TOKEN_VIEW_CLOSE).toString();
        } else if (object instanceof DataView[]) {
            final StringBuilder builder = new StringBuilder().append(TOKEN_ARRAY_OPEN);
            if (!vanilla) {
                builder.append(TOKEN_VIEW_ARRAY).append(TOKEN_ARRAY_TYPE_SUFFIX);
            }
            final DataView[] views = (DataView[]) object;
            for (int i = 0; i < views.length; i++) {
                if (i != 0) {
                    builder.append(TOKEN_NEW_ENTRY);
                }
                builder.append(toMojangson(views[i], vanilla));
            }
            final String value = builder.append(TOKEN_ARRAY_CLOSE).toString();
            return vanilla ? new Entry(ExtendedObjectType.VIEW_ARRAY, value) : value;
        } else if (object instanceof Map) {
            final StringBuilder builder = new StringBuilder();
            final Iterator<Map.Entry> it = ((Map) object).entrySet().iterator();
            if (vanilla) {
                builder.append(TOKEN_ARRAY_OPEN);
            } else {
                builder.append(TOKEN_MAP_OPEN);
            }
            while (it.hasNext()) {
                final Map.Entry entry = it.next();
                final Object key = toMojangson(entry.getKey(), vanilla);
                String keyName = ExtendedObjectType.mapKeyName;
                if (key instanceof Entry) {
                    keyName += '$' + ((Entry) key).suffix;
                }
                final Object value = toMojangson(entry.getValue(), vanilla);
                String valueName = ExtendedObjectType.mapValueName;
                if (key instanceof Entry) {
                    valueName += '$' + ((Entry) value).suffix;
                }
                if (vanilla) {
                    builder.append(TOKEN_VIEW_OPEN);
                    builder.append(quoteStringIfNeeded(keyName));
                    builder.append(TOKEN_KEY_VALUE_SEPARATOR);
                    builder.append(key);
                    builder.append(TOKEN_NEW_ENTRY);
                    builder.append(quoteStringIfNeeded(valueName));
                    builder.append(TOKEN_KEY_VALUE_SEPARATOR);
                    builder.append(value);
                    builder.append(TOKEN_VIEW_CLOSE);
                } else {
                    builder.append(key);
                    builder.append(TOKEN_KEY_VALUE_SEPARATOR);
                    builder.append(value);
                }
                if (it.hasNext()) {
                    builder.append(TOKEN_NEW_ENTRY);
                }
            }
            if (vanilla) {
                builder.append(TOKEN_ARRAY_CLOSE);
            } else {
                builder.append(TOKEN_MAP_CLOSE);
            }
            builder.append(TOKEN_VIEW_OPEN);
            final String value = builder.toString();
            return vanilla ? new Entry(ExtendedObjectType.MAP, value) : value;
        } else if (object instanceof Map[]) {
            final StringBuilder builder = new StringBuilder().append(TOKEN_ARRAY_OPEN);
            if (!vanilla) {
                builder.append(TOKEN_MAP_ARRAY).append(TOKEN_ARRAY_TYPE_SUFFIX);
            }
            final Map[] maps = (Map[]) object;
            for (int i = 0; i < maps.length; i++) {
                if (i != 0) {
                    builder.append(TOKEN_NEW_ENTRY);
                }
                builder.append(toMojangson(maps[i], vanilla));
            }
            final String value = builder.append(TOKEN_ARRAY_CLOSE).toString();
            return vanilla ? new Entry(ExtendedObjectType.MAP_ARRAY, value) : value;
        }
        throw new IllegalStateException("Unsupported object type: " + object.getClass().getName());
    }

    private static String quoteStringIfNeeded(String value) {
        if (shouldBeQuoted(value)) {
            // Put the string between quotes and escape quotes within the original string
            final char quote = TOKEN_DOUBLE_QUOTED_STRING;
            return quote + value.replace(quote + "", "\\" + quote) + quote;
        }
        return value;
    }

    /**
     * Checks whether the given {@link String} should be quoted.
     *
     * @param value The string
     * @return Should be quoted
     */
    private static boolean shouldBeQuoted(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (MojangsonParser.shouldCharBeQuoted(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
