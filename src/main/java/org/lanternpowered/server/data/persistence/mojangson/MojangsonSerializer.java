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
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_BYTE;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_CHAR_QUOTE;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_DOUBLE;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_DOUBLE_QUOTED_STRING;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_DOUBLE_UPPER;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_FLOAT;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_FLOAT_UPPER;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_INT_UPPER;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_KEY_VALUE_SEPARATOR;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_LONG;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_LONG_UPPER;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_NEW_ENTRY;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_SHORT;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_SHORT_UPPER;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_VIEW_CLOSE;
import static org.lanternpowered.server.data.persistence.mojangson.MojangsonParser.TOKEN_VIEW_OPEN;

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

    /**
     * Converts the given {@link Object} into mojangson.
     *
     * @param object The object
     * @return The mojangson string
     */
    static String toMojangson(Object object) {
        if (object instanceof Boolean || object instanceof Integer) {
            return object.toString();
        } else if (object instanceof Character) {
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
            return object.toString() + TOKEN_DOUBLE;
        } else if (object instanceof String) {
            return quoteStringIfNeeded((String) object);
        } else if (object instanceof byte[]) {
            final StringBuilder builder = new StringBuilder();
            builder.append(TOKEN_ARRAY_OPEN).append(TOKEN_DOUBLE_UPPER).append(TOKEN_ARRAY_TYPE_SUFFIX);
            final byte[] bytes = (byte[]) object;
            for (int i = 0; i < bytes.length; i++) {
                if (i != 0) {
                    builder.append(TOKEN_NEW_ENTRY);
                }
                builder.append(bytes[i]);
            }
            return builder.append(TOKEN_ARRAY_CLOSE).toString();
        } else if (object instanceof short[]) {
            return new StringBuilder()
                    .append(TOKEN_ARRAY_OPEN).append(TOKEN_SHORT_UPPER).append(TOKEN_ARRAY_TYPE_SUFFIX)
                    .append(Shorts.join(ENTRY_SEPARATOR, (short[]) object))
                    .append(TOKEN_ARRAY_CLOSE)
                    .toString();
        } else if (object instanceof int[]) {
            return new StringBuilder()
                    .append(TOKEN_ARRAY_OPEN).append(TOKEN_INT_UPPER).append(TOKEN_ARRAY_TYPE_SUFFIX)
                    .append(Ints.join(ENTRY_SEPARATOR, (int[]) object))
                    .append(TOKEN_ARRAY_CLOSE)
                    .toString();
        } else if (object instanceof long[]) {
            return new StringBuilder()
                    .append(TOKEN_ARRAY_OPEN).append(TOKEN_LONG_UPPER).append(TOKEN_ARRAY_TYPE_SUFFIX)
                    .append(Longs.join(ENTRY_SEPARATOR, (long[]) object))
                    .append(TOKEN_ARRAY_CLOSE)
                    .toString();
        } else if (object instanceof float[]) {
            return new StringBuilder()
                    .append(TOKEN_ARRAY_OPEN).append(TOKEN_FLOAT_UPPER).append(TOKEN_ARRAY_TYPE_SUFFIX)
                    .append(Floats.join(ENTRY_SEPARATOR, (float[]) object))
                    .append(TOKEN_ARRAY_CLOSE)
                    .toString();
        } else if (object instanceof double[]) {
            return new StringBuilder()
                    .append(TOKEN_ARRAY_OPEN).append(TOKEN_DOUBLE_UPPER).append(TOKEN_ARRAY_TYPE_SUFFIX)
                    .append(Doubles.join(ENTRY_SEPARATOR, (double[]) object))
                    .append(TOKEN_ARRAY_CLOSE)
                    .toString();
        } else if (object instanceof Collection) {
            final StringBuilder builder = new StringBuilder();
            builder.append(TOKEN_ARRAY_OPEN);
            final Iterator<Object> it = ((Collection<Object>) object).iterator();
            while (it.hasNext()) {
                builder.append(toMojangson(it.next()));
                if (it.hasNext()) {
                    builder.append(TOKEN_NEW_ENTRY);
                }
            }
            return builder.append(TOKEN_ARRAY_CLOSE).toString();
        } else if (object instanceof DataView) {
            final StringBuilder builder = new StringBuilder();
            builder.append(TOKEN_VIEW_OPEN);
            final Map<DataQuery, Object> map = ((DataView) object).getValues(false);
            final Iterator<Map.Entry<DataQuery, Object>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<DataQuery, Object> entry = it.next();
                builder.append(quoteStringIfNeeded(entry.getKey().toString()));
                builder.append(TOKEN_KEY_VALUE_SEPARATOR);
                builder.append(toMojangson(entry.getValue()));
                if (it.hasNext()) {
                    builder.append(TOKEN_NEW_ENTRY);
                }
            }
            return builder.append(TOKEN_VIEW_CLOSE).toString();
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
