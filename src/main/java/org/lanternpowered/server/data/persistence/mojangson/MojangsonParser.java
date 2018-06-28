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

import com.google.common.primitives.Chars;
import org.lanternpowered.server.data.MemoryDataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
final class MojangsonParser {

    public static void main(String... args) {
        final MojangsonParser parser = new MojangsonParser("{\n"
                + "  \"test\": \"aaaa\",\n"
                + "  b: [0:\"a\",1:b,2:c], 'c': 'dd--213464\"*dd', d:false, q:{z:10.0f}, w=`\u2639`\n"
                + "}");
        final Object object = parser.parseCompleteObject();
        System.out.println(object);
        System.out.println(MojangsonSerializer.toMojangson(object));
    }

    static final char TOKEN_VIEW_OPEN = '{';
    static final char TOKEN_VIEW_CLOSE = '}';

    static final char TOKEN_ARRAY_OPEN = '[';
    static final char TOKEN_ARRAY_CLOSE = ']';

    static final char TOKEN_DOUBLE_QUOTED_STRING = '"';
    static final char TOKEN_SINGLE_QUOTED_STRING = '\'';
    static final char TOKEN_NEW_ENTRY = ',';

    static final char TOKEN_BYTE = 'b';
    static final char TOKEN_BYTE_UPPER = 'B';

    static final char TOKEN_SHORT = 's';
    static final char TOKEN_SHORT_UPPER = 'S';

    static final char TOKEN_INT = 'i';
    static final char TOKEN_INT_UPPER = 'I';

    static final char TOKEN_LONG = 'l';
    static final char TOKEN_LONG_UPPER = 'L';

    static final char TOKEN_FLOAT = 'f';
    static final char TOKEN_FLOAT_UPPER = 'F';

    static final char TOKEN_DOUBLE = 'd';
    static final char TOKEN_DOUBLE_UPPER = 'D';

    static final char TOKEN_ARRAY_TYPE_SUFFIX = ';';

    static final char TOKEN_KEY_VALUE_SEPARATOR = ':';
    static final char TOKEN_KEY_VALUE_SEPARATOR_ALT = '=';

    static final char TOKEN_CHAR_QUOTE = '`';

    private final static char[] INTEGER_TOKENS = {
            TOKEN_BYTE,
            TOKEN_BYTE_UPPER,
            TOKEN_SHORT,
            TOKEN_SHORT_UPPER,
            TOKEN_INT,
            TOKEN_INT_UPPER,
            TOKEN_LONG,
            TOKEN_LONG_UPPER,
    };

    private final static char[] FLOATING_POINT_TOKENS = {
            TOKEN_FLOAT,
            TOKEN_FLOAT_UPPER,
            TOKEN_DOUBLE,
            TOKEN_DOUBLE_UPPER,
    };

    private final static char[] NUMBER_TOKENS = Chars.concat(INTEGER_TOKENS, FLOATING_POINT_TOKENS);

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

    private Object parseArrayOrList() {
        nextChar(); // Skip [
        skipWhitespace();
        // Check for arrays
        char arrayType = currentChar();
        if (Chars.contains(NUMBER_TOKENS, arrayType) &&
                currentChar(1) == TOKEN_ARRAY_TYPE_SUFFIX) {
            // We got one, skip array type chars
            nextChar();
            nextChar();
        } else {
            return parseListObjects();
        }
        final Collection<? extends Number> numbers = (Collection) parseListObjects();
        int i = 0;
        switch (arrayType) {
            case TOKEN_BYTE:
            case TOKEN_BYTE_UPPER:
                final byte[] bytes = new byte[numbers.size()];
                for (Number number : numbers) {
                    bytes[i++] = number.byteValue();
                }
                return bytes;
            case TOKEN_SHORT:
            case TOKEN_SHORT_UPPER:
                final short[] shorts = new short[numbers.size()];
                for (Number number : numbers) {
                    shorts[i++] = number.shortValue();
                }
                return shorts;
            case TOKEN_INT:
            case TOKEN_INT_UPPER:
                final int[] ints = new int[numbers.size()];
                for (Number number : numbers) {
                    ints[i++] = number.intValue();
                }
                return ints;
            case TOKEN_LONG:
            case TOKEN_LONG_UPPER:
                final long[] longs = new long[numbers.size()];
                for (Number number : numbers) {
                    longs[i++] = number.longValue();
                }
                return longs;
            case TOKEN_FLOAT:
            case TOKEN_FLOAT_UPPER:
                final float[] floats = new float[numbers.size()];
                for (Number number : numbers) {
                    floats[i++] = number.floatValue();
                }
                return floats;
            case TOKEN_DOUBLE:
            case TOKEN_DOUBLE_UPPER:
                final double[] doubles = new double[numbers.size()];
                for (Number number : numbers) {
                    doubles[i++] = number.doubleValue();
                }
                return doubles;
        }
        throw new MojangsonParseException("Array type '" + arrayType + "' is not being handled.");
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
            final int newType = c == TOKEN_KEY_VALUE_SEPARATOR ? ARRAY_TYPE_INDEXED : ARRAY_TYPE_NON_INDEXED;
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

    private DataView parseView(@Nullable DataView parent, @Nullable String key) {
        nextChar(); // Skip {
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
            skipWhitespace();
            c = currentChar();
            if (c != TOKEN_KEY_VALUE_SEPARATOR &&
                    c != TOKEN_KEY_VALUE_SEPARATOR_ALT) {
                throw new MojangsonParseException("Expected a '" + TOKEN_KEY_VALUE_SEPARATOR + "' or '" +
                        TOKEN_KEY_VALUE_SEPARATOR_ALT + "' but got '" + c + "'");
            }
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
     * Parses the object and throws an exception
     * if there were trailing characters.
     *
     * @return The parsed object
     */
    public Object parseCompleteObject() {
        final Object object = parseObject(null, null);
        while (true) {
            if (this.pos == this.content.length) {
                return object;
            } else if (isWhitespace(currentChar())) {
                nextChar();
            } else {
                throw new MojangsonParseException("Found trailing content: " +
                        new String(this.content, this.pos, this.content.length - this.pos));
            }
        }
    }

    /**
     * Parses the next available object.
     *
     * @return The parsed object
     */
    private Object parseObject() {
        return parseObject(null, null);
    }

    private Object parseObject(@Nullable DataView parent, @Nullable String key) {
        skipWhitespace();
        char c = currentChar();
        // Check which type should be parsed
        switch (c) {
            case TOKEN_VIEW_OPEN:
                return parseView(parent, key);
            case TOKEN_ARRAY_OPEN:
                return parseArrayOrList();
            case TOKEN_SINGLE_QUOTED_STRING:
            case TOKEN_DOUBLE_QUOTED_STRING:
                return parseQuotedString();
            case TOKEN_CHAR_QUOTE:
                nextChar(); // Skip the token
                c = currentChar(); // Get the next char
                nextChar();
                if (currentChar() == TOKEN_CHAR_QUOTE) { // Optionally skip the end quote
                    nextChar();
                }
                return c;
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
        if (Chars.contains(INTEGER_TOKENS, numChar)) {
            final String numVal = value.substring(0, value.length() - 1);
            if (INTEGER_PATTERN.matcher(numVal).matches()) {
                switch (numChar) {
                    case TOKEN_BYTE:
                    case TOKEN_BYTE_UPPER:
                        return Byte.parseByte(numVal);
                    case TOKEN_SHORT:
                    case TOKEN_SHORT_UPPER:
                        return Short.parseShort(numVal);
                    case TOKEN_INT:
                    case TOKEN_INT_UPPER:
                        return Integer.parseInt(numVal);
                    case TOKEN_LONG:
                    case TOKEN_LONG_UPPER:
                        return Long.parseLong(numVal);
                }
            }
        } else if (Chars.contains(FLOATING_POINT_TOKENS, numChar)) {
            final String numVal = value.substring(0, value.length() - 1);
            if (FLOATING_POINT_PATTERN.matcher(numVal).matches()) {
                switch (numChar) {
                    case TOKEN_DOUBLE:
                    case TOKEN_DOUBLE_UPPER:
                        return Double.parseDouble(numVal);
                    case TOKEN_FLOAT:
                    case TOKEN_FLOAT_UPPER:
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

    /**
     * Increases the reader index by one, so pointing to the next character.
     */
    private void nextChar() {
        this.pos++;
    }

    /**
     * Gets the character at the current reader index.
     *
     * @return The character
     */
    private char currentChar() {
        return currentChar(0);
    }

    /**
     * Gets the character at the current reader index + the given offset.
     *
     * @param offset The offset to get the character at
     * @return The character
     */
    private char currentChar(int offset) {
        final int index = this.pos + offset;
        // Check if we reached the end
        if (index >= this.content.length) {
            throw new MojangsonParseException("End of the content reached but expected another char.");
        }
        return this.content[index];
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
     * Gets whether the given character should be quoted to be used in strings or names.
     *
     * @param c The character to check
     * @return Should be quoted
     */
    static boolean shouldCharBeQuoted(char c) {
        return (c < '0' || c > '9') && (c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && c != '_' && c != '-' && c != '.' && c != '+';
    }
}
