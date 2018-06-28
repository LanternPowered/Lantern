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

import org.lanternpowered.server.data.MemoryDataContainer;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

final class MojangsonParser {

    public static void main(String... args) {
        final MojangsonParser parser = new MojangsonParser("{\n"
                + "  \"test\": \"aaaa\",\n"
                + "  b: [0:\"a\",1:b,2:c], 'c': 'dddd'\n"
                + "}");
        System.out.println(parser.parseView());
    }

    static final char TOKEN_VIEW_OPEN = '{';
    static final char TOKEN_VIEW_CLOSE = '}';

    static final char TOKEN_ARRAY_OPEN = '[';
    static final char TOKEN_ARRAY_CLOSE = ']';

    static final char TOKEN_DOUBLE_QUOTED_STRING = '"';
    static final char TOKEN_SINGLE_QUOTED_STRING = '\'';
    static final char TOKEN_NEW_ENTRY = ',';

    // https://www.regular-expressions.info/floatingpoint.html
    private static final Pattern FLOATING_POINT_PATTERN =
            Pattern.compile("^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$");
    private static final Pattern INTEGER_PATTERN =
            Pattern.compile("^[-+]?[0-9]+$");

    // The content that is being parsed.
    private final char[] content;

    private int pos;

    MojangsonParser(String content) {
        this(content.toCharArray());
    }

    private MojangsonParser(char[] content) {
        this.content = content;
    }

    DataContainer parseContainer() {
        return (DataContainer) parseView();
    }

    private Object parseArrayOrList() {
        expectChar(TOKEN_ARRAY_OPEN);
        nextChar();
        skipWhitespace();
        // Check for arrays
        char c = currentChar();
        char arrayType = '\0'; // null char means no array
        if ("bBsSiIlLdDfF".indexOf(arrayType) != -1) {
            char c1 = currentChar(1);
            if (c1 == ';') {
                // We got one, skip array type chars
                skipChars(2);
                // Keep it safe for later
                arrayType = c;
            }
        }
        final List<Object> objects = parseListObjects();
        switch (arrayType) {
            case 'b':
            case 'B':
                final byte[] bytes = new byte[objects.size()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = ((Number) objects).byteValue();
                }
                return bytes;
            case 's':
            case 'S':
                final short[] shorts = new short[objects.size()];
                for (int i = 0; i < shorts.length; i++) {
                    shorts[i] = ((Number) objects).shortValue();
                }
                return shorts;
            case 'i':
            case 'I':
                final int[] ints = new int[objects.size()];
                for (int i = 0; i < ints.length; i++) {
                    ints[i] = ((Number) objects).intValue();
                }
                return ints;
            case 'l':
            case 'L':
                final long[] longs = new long[objects.size()];
                for (int i = 0; i < longs.length; i++) {
                    longs[i] = ((Number) objects).longValue();
                }
                return longs;
            case 'f':
            case 'F':
                final float[] floats = new float[objects.size()];
                for (int i = 0; i < floats.length; i++) {
                    floats[i] = ((Number) objects).floatValue();
                }
                return floats;
            case 'd':
            case 'D':
                final double[] doubles = new double[objects.size()];
                for (int i = 0; i < doubles.length; i++) {
                    doubles[i] = ((Number) objects).doubleValue();
                }
                return doubles;
        }
        return objects;
    }

    private final static int ARRAY_TYPE_UNKNOWN = 0;
    private final static int ARRAY_TYPE_INDEXED = 1;
    private final static int ARRAY_TYPE_NON_INDEXED = 2;

    private List<Object> parseListObjects() {
        final List<Object> objects = new ArrayList<>();
        int type = ARRAY_TYPE_UNKNOWN;
        while (true) {
            skipWhitespace(); // Skip leading whitespaces
            char c = currentChar();
            // End of the array
            if (c == TOKEN_ARRAY_CLOSE) {
                return objects;
            }
            Object value = parseObject();
            skipWhitespace();
            c = currentChar();
            final int newType = c == ':' ? ARRAY_TYPE_INDEXED : ARRAY_TYPE_NON_INDEXED;
            if (type == ARRAY_TYPE_UNKNOWN) {
                type = newType;
            } else if (newType != type) {
                throw new MojangsonParseException("Indexed and non-indexed array elements cannot be mixed.");
            }
            // Check if we got a indexed array
            if (newType == ARRAY_TYPE_INDEXED) {
                final int index;
                if (value instanceof Integer) {
                    index = (Integer) value;
                } else {
                    throw new MojangsonParseException("Indexed arrays must use integers as indexes and not " + value);
                }
                nextChar();
                // Parse the new value
                value = parseObject();
                while (objects.size() <= index) {
                    objects.add(null);
                }
                objects.set(index, value);
                skipWhitespace();
                c = currentChar();
            } else {
                objects.add(value);
            }
            for (int i = 0; i < objects.size(); i++) {
                if (objects.get(i) == null) {
                    throw new MojangsonParseException("Array may not contain null elements (or missing indexed"
                            + "entries). Null element found at index: " + i);
                }
            }
            nextChar();
            switch (c) {
                case TOKEN_ARRAY_CLOSE:
                    return objects;
                case TOKEN_NEW_ENTRY:
                    break;
                default:
                    throw new MojangsonParseException("Got unexpected token: " + c);
            }
        }
    }

    private DataView parseView() {
        return parseView(null, null);
    }

    private DataView parseView(@Nullable DataView parent, @Nullable String key) {
        expectChar(TOKEN_VIEW_OPEN);
        nextChar();
        final DataView dataView;
        if (parent != null) {
            dataView = parent.createView(DataQuery.of(key));
        } else {
            dataView = new MemoryDataContainer(DataView.SafetyMode.NO_DATA_CLONED);
        }
        while (true) {
            skipWhitespace(); // Skip leading whitespaces
            char c = currentChar();
            // Check for the end of the data view
            if (c == TOKEN_VIEW_CLOSE) {
                return dataView;
            }
            final String name;
            // Check which name type should be parsed
            if (c == TOKEN_SINGLE_QUOTED_STRING ||
                    c == TOKEN_DOUBLE_QUOTED_STRING) {
                name = parseQuotedString();
            } else {
                name = parseUnquotedString();
            }
            expectChar(':');
            nextChar();
            final Object value = parseObject(dataView, name);
            // Only set the value if it's a view, DataViews
            // are already set internally by using createView
            // on it's parent
            if (!(value instanceof DataView) && value != null) { // Support null values?
                dataView.set(DataQuery.of(name), value);
            }
            skipWhitespace(); // Skip again whitespaces after object
            c = currentChar();
            nextChar();
            switch (c) {
                case TOKEN_VIEW_CLOSE:
                    return dataView; // Reached the end
                case TOKEN_NEW_ENTRY:
                    break;
                default:
                    throw new MojangsonParseException("Got unexpected token: " + c);
            }
        }
    }

    /**
     * Parses the next available object.
     *
     * @return The parsed object
     */
    @Nullable
    private Object parseObject() {
        return parseObject(null, null);
    }

    @Nullable
    private Object parseObject(@Nullable DataView parent, @Nullable String key) {
        skipWhitespace();
        final char c = currentChar();
        // Check which type should be parsed
        switch (c) {
            case TOKEN_VIEW_OPEN:
                return parseView(parent, key);
            case TOKEN_ARRAY_OPEN:
                return parseArrayOrList();
            case TOKEN_SINGLE_QUOTED_STRING:
            case TOKEN_DOUBLE_QUOTED_STRING:
                return parseQuotedString();
        }
        if (shouldCharBeQuoted(c)) {
            throw new MojangsonParseException("Got unexpected token: " + c);
        }
        // Parse as unquoted
        final String value = parseUnquotedString();
        // Check for booleans
        switch (value) {
            case "true":
                return true;
            case "false":
                return false;
            case "null":
                return null;
        }
        // Check if it's a int value
        if (INTEGER_PATTERN.matcher(value).matches()) {
            return Integer.parseInt(value);
        }
        // Check if it's a double
        if (FLOATING_POINT_PATTERN.matcher(value).matches()) {
            return Double.parseDouble(value);
        }
        final char numChar = value.charAt(value.length() - 1);
        if ("bBsSlL".indexOf(numChar) != -1) {
            final String numVal = value.substring(0, value.length() - 1);
            if (INTEGER_PATTERN.matcher(numVal).matches()) {
                switch (numChar) {
                    case 'b':
                    case 'B':
                        return Byte.parseByte(numVal);
                    case 's':
                    case 'S':
                        return Short.parseShort(numVal);
                    case 'l':
                    case 'L':
                        return Long.parseLong(numVal);
                }
            }
        } else if ("dDfF".indexOf(numChar) != -1) {
            final String numVal = value.substring(0, value.length() - 1);
            if (FLOATING_POINT_PATTERN.matcher(numVal).matches()) {
                switch (numChar) {
                    case 'd':
                    case 'D':
                        return Double.parseDouble(numVal);
                    case 'f':
                    case 'F':
                        return Float.parseFloat(numVal);
                }
            }
        }
        return value;
    }

    /**
     * Skips all characters until a non whitespace is found.
     */
    private void skipWhitespace() {
        // Read until we find a non whitespace
        while (isWhitespace(currentChar())) {
            nextChar();
        }
    }

    private static boolean isWhitespace(char c) {
        switch (c) {
            case '\n':
            case '\t':
            case '\r':
            case ' ':
                return true;
            default:
                return false;
        }
    }

    private void nextChar() {
        this.pos++;
    }

    private void skipChars(int count) {
        this.pos += count;
    }

    private char currentChar() {
        return currentChar(0);
    }

    private char currentChar(int offset) {
        final int index = this.pos + offset;
        // Check if we reached the end
        if (index >= this.content.length) {
            throw new MojangsonParseException("End of the content reached but expected another char.");
        }
        return this.content[index];
    }

    /**
     * Skips whitespace and then expects the given character,
     * if not found, a exception will be thrown.
     *
     * @param expected The character
     */
    private void expectChar(char expected) {
        skipWhitespace(); // Ignore whitespace

        // Check if we reached the end
        if (this.pos >= this.content.length) {
            throw new MojangsonParseException("End of the content reached but expected '" + expected + "'");
        }
        final char c = this.content[this.pos];
        if (this.content[this.pos] != c) {
            throw new MojangsonParseException("Expected '" + expected + "' but got " + c);
        }
    }

    /**
     * Parses a unquoted {@link String}.
     *
     * @return The string
     */
    private String parseUnquotedString() {
        final int start = this.pos;
        char c = currentChar();
        nextChar();
        // The first char must be valid
        if (shouldCharBeQuoted(c)) {
            throw new MojangsonParseException("Got unexpected token: " + c);
        }
        while (this.pos < this.content.length) {
            c = currentChar();
            if (shouldCharBeQuoted(c)) {
                return new String(this.content, start, this.pos - start);
            }
            nextChar();
        }
        return new String(this.content, start, this.content.length - start);
    }

    /**
     * Parses a quoted {@link String}.
     *
     * @return The string
     */
    private String parseQuotedString() {
        // Skip the quote
        final char quoteChar = currentChar();
        nextChar();
        final StringBuilder builder = new StringBuilder();
        while (true) {
            char c = currentChar();
            // If we find a quote, we reached the end of the string
            if (c == quoteChar) {
                nextChar(); // Skip quote for next value
                break;
            } else if (c == '\\') { // The next character is literal
                nextChar();
                c = currentChar();
            }
            builder.append(c);
            nextChar();
        }
        return builder.toString();
    }

    /**
     * Gets whether the given character can be used in unquoted strings.
     *
     * @param c The character to check
     * @return Whether the character can be used in unquoted strings
     */
    static boolean shouldCharBeQuoted(char c) {
        return !((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_' || c == '-' || c == '.' || c == '+');
    }
}
