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

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unchecked")
final class MojangsonSerializer {

    /**
     * Converts the given {@link Object} into mojangson.
     *
     * @param object The object
     * @return The mojangson string
     */
    static String toMojangson(Object object) {
        if (object instanceof Boolean || object instanceof Integer) {
            return object + "";
        } else if (object instanceof Byte) {
            return object + "b";
        } else if (object instanceof Short) {
            return object + "s";
        } else if (object instanceof Long) {
            return object + "l";
        } else if (object instanceof Float) {
            return object + "f";
        } else if (object instanceof Number) {
            return object + "d";
        } else if (object instanceof String) {
            return quoteStringIfNeeded((String) object);
        } else if (object instanceof byte[]) {
            final StringBuilder builder = new StringBuilder();
            builder.append(MojangsonParser.TOKEN_ARRAY_OPEN).append("B;");
            final byte[] bytes = (byte[]) object;
            for (int i = 0; i < bytes.length; i++) {
                if (i != 0) {
                    builder.append(MojangsonParser.TOKEN_NEW_ENTRY);
                }
                builder.append(bytes[i]);
            }
            return builder.append(MojangsonParser.TOKEN_ARRAY_CLOSE).toString();
        } else if (object instanceof short[]) {
            final StringBuilder builder = new StringBuilder();
            builder.append(MojangsonParser.TOKEN_ARRAY_OPEN).append("S;");
            final short[] shorts = (short[]) object;
            for (int i = 0; i < shorts.length; i++) {
                if (i != 0) {
                    builder.append(MojangsonParser.TOKEN_NEW_ENTRY);
                }
                builder.append(shorts[i]);
            }
            return builder.append(MojangsonParser.TOKEN_ARRAY_CLOSE).toString();
        } else if (object instanceof int[]) {
            final StringBuilder builder = new StringBuilder();
            builder.append(MojangsonParser.TOKEN_ARRAY_OPEN).append("I;");
            final int[] ints = (int[]) object;
            for (int i = 0; i < ints.length; i++) {
                if (i != 0) {
                    builder.append(MojangsonParser.TOKEN_NEW_ENTRY);
                }
                builder.append(ints[i]);
            }
            return builder.append(MojangsonParser.TOKEN_ARRAY_CLOSE).toString();
        } else if (object instanceof long[]) {
            final StringBuilder builder = new StringBuilder();
            builder.append(MojangsonParser.TOKEN_ARRAY_OPEN).append("L;");
            final long[] longs = (long[]) object;
            for (int i = 0; i < longs.length; i++) {
                if (i != 0) {
                    builder.append(MojangsonParser.TOKEN_NEW_ENTRY);
                }
                builder.append(longs[i]);
            }
            return builder.append(MojangsonParser.TOKEN_ARRAY_CLOSE).toString();
        } else if (object instanceof float[]) {
            final StringBuilder builder = new StringBuilder();
            builder.append(MojangsonParser.TOKEN_ARRAY_OPEN).append("F;");
            final float[] floats = (float[]) object;
            for (int i = 0; i < floats.length; i++) {
                if (i != 0) {
                    builder.append(MojangsonParser.TOKEN_NEW_ENTRY);
                }
                builder.append(floats[i]);
            }
            return builder.append(MojangsonParser.TOKEN_ARRAY_CLOSE).toString();
        } else if (object instanceof double[]) {
            final StringBuilder builder = new StringBuilder();
            builder.append(MojangsonParser.TOKEN_ARRAY_OPEN).append("D;");
            final double[] doubles = (double[]) object;
            for (int i = 0; i < doubles.length; i++) {
                if (i != 0) {
                    builder.append(MojangsonParser.TOKEN_NEW_ENTRY);
                }
                builder.append(doubles[i]);
            }
            return builder.append(MojangsonParser.TOKEN_ARRAY_CLOSE).toString();
        } else if (object instanceof Collection) {
            final StringBuilder builder = new StringBuilder();
            builder.append(MojangsonParser.TOKEN_ARRAY_OPEN);
            final Iterator<Object> it = ((Collection<Object>) object).iterator();
            while (it.hasNext()) {
                builder.append(toMojangson(it.next()));
                if (it.hasNext()) {
                    builder.append(MojangsonParser.TOKEN_NEW_ENTRY);
                }
            }
            return builder.append(MojangsonParser.TOKEN_ARRAY_CLOSE).toString();
        } else if (object instanceof DataView) {
            final StringBuilder builder = new StringBuilder();
            builder.append(MojangsonParser.TOKEN_VIEW_OPEN);
            final Map<DataQuery, Object> map = ((DataView) object).getValues(false);
            final Iterator<Map.Entry<DataQuery, Object>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<DataQuery, Object> entry = it.next();
                builder.append(quoteStringIfNeeded(entry.getKey().toString()));
                builder.append(':');
                builder.append(toMojangson(entry.getValue()));
                if (it.hasNext()) {
                    builder.append(MojangsonParser.TOKEN_NEW_ENTRY);
                }
            }
            return builder.append(MojangsonParser.TOKEN_VIEW_CLOSE).toString();
        }
        throw new IllegalStateException("Unsupported object type");
    }

    private static String quoteStringIfNeeded(String value) {
        if (shouldBeQuoted(value)) {
            // Put the string between quotes and escape quotes
            return "\"" + value.replace("\"", "\\\"") + "\"";
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
