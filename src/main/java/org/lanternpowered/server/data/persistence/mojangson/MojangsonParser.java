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
import it.unimi.dsi.fastutil.objects.Object2CharMap;
import it.unimi.dsi.fastutil.objects.Object2CharOpenHashMap;
import org.lanternpowered.server.data.MemoryDataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

@SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
final class MojangsonParser {

    public static void main(String... args) {
        final Object object = Mojangson.parseLanterson("{\n"
                + "  \"test\": \"aaaa\",\n"
                + "  b: [\"a\",b,c], 'c': 'dd--213464\"*dd', d:false, q:{z:10.0f}, w=`\u2639`, 'e$Boolean'='true',"
                + "'ee$boolean[]': [0,1,0,1], m=(1=20.0, 2=30.0),tt:[C;`q`,`p,`7,`9], 'tw$List$char': [q,w,u,y], sss=[string;test,a,b,c,d]\n"
                + "}");
        System.out.println(object);
        final DataView dataView = (DataView) object;
        System.out.println(Arrays.toString((boolean[]) dataView.get(DataQuery.of("ee")).get()));
        System.out.println(Arrays.toString((String[]) dataView.get(DataQuery.of("sss")).get()));
        System.out.println(MojangsonSerializer.toMojangson(object));
        System.out.println(MojangsonSerializer.toLanterson(object));
    }

    static final char TOKEN_VIEW_OPEN = '{';
    static final char TOKEN_VIEW_CLOSE = '}';

    static final char TOKEN_ARRAY_OPEN = '[';
    static final char TOKEN_ARRAY_CLOSE = ']';

    static final char TOKEN_MAP_OPEN = '(';
    static final char TOKEN_MAP_CLOSE = ')';

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

    static final char TOKEN_CHAR = 'c';
    static final char TOKEN_CHAR_UPPER = 'C';
    static final char TOKEN_CHAR_QUOTE = '`';

    static final char TOKEN_BOOLEAN = 'z';
    static final char TOKEN_BOOLEAN_UPPER = 'Z';

    static final char TOKEN_MAP_ARRAY = 'm';
    static final char TOKEN_MAP_ARRAY_UPPER = 'M';

    static final char TOKEN_VIEW_ARRAY = 'v';
    static final char TOKEN_VIEW_ARRAY_UPPER = 'V';

    static final char TOKEN_STRING_ARRAY = 'w';
    static final char TOKEN_STRING_ARRAY_UPPER = 'W';

    static final char TOKEN_INVALID_ARRAY_TYPE = '\0';

    private final static Object2CharMap<String> NAMED_ARRAY_TOKENS = new Object2CharOpenHashMap<>();
    private static int LONGEST_NAMED_ARRAY_TOKEN = 0;

    private static void addNamedArrayToken(String name, char token) {
        NAMED_ARRAY_TOKENS.put(name, token);
        if (name.length() > LONGEST_NAMED_ARRAY_TOKEN) {
            LONGEST_NAMED_ARRAY_TOKEN = name.length();
        }
    }

    static {
        NAMED_ARRAY_TOKENS.defaultReturnValue(TOKEN_INVALID_ARRAY_TYPE);
        addNamedArrayToken("boolean", TOKEN_BOOLEAN);
        addNamedArrayToken("bool", TOKEN_BOOLEAN);
        addNamedArrayToken("byte", TOKEN_BYTE);
        addNamedArrayToken("short", TOKEN_SHORT);
        addNamedArrayToken("int", TOKEN_INT);
        addNamedArrayToken("integer", TOKEN_INT);
        addNamedArrayToken("long", TOKEN_LONG);
        addNamedArrayToken("double", TOKEN_DOUBLE);
        addNamedArrayToken("string", TOKEN_STRING_ARRAY);
        addNamedArrayToken("view", TOKEN_VIEW_ARRAY);
        addNamedArrayToken("compound", TOKEN_VIEW_ARRAY);
        addNamedArrayToken("container", TOKEN_VIEW_ARRAY);
        addNamedArrayToken("char", TOKEN_CHAR);
        addNamedArrayToken("character", TOKEN_CHAR);
    }

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

    private final static char[] ARRAY_TYPE_TOKENS = Chars.concat(INTEGER_TOKENS, FLOATING_POINT_TOKENS, new char[] {
            TOKEN_CHAR,
            TOKEN_CHAR_UPPER,
            TOKEN_BOOLEAN,
            TOKEN_BOOLEAN_UPPER,
            TOKEN_MAP_ARRAY,
            TOKEN_MAP_ARRAY_UPPER,
            TOKEN_VIEW_ARRAY,
            TOKEN_VIEW_ARRAY_UPPER,
            TOKEN_STRING_ARRAY,
            TOKEN_STRING_ARRAY_UPPER,
    });

    // https://www.regular-expressions.info/floatingpoint.html
    private static final Pattern FLOATING_POINT_PATTERN =
            Pattern.compile("^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$");
    private static final Pattern INTEGER_PATTERN =
            Pattern.compile("^[-+]?[0-9]+$");

    // The content that is being parsed.
    private final char[] content;
    private final int flags;

    private int pos;

    MojangsonParser(String content, int flags) {
        this(content.toCharArray(), flags);
    }

    private MojangsonParser(char[] content, int flags) {
        this.content = content;
        this.flags = flags;
    }

    private boolean hasFlag(int flag) {
        return (this.flags & flag) != 0;
    }

    private static double[] toDoubleArray(Collection objects) {
        int i = 0;
        final double[] doubles = new double[objects.size()];
        for (Number number : (Collection<Number>) objects) {
            doubles[i++] = number.doubleValue();
        }
        return doubles;
    }

    private static float[] toFloatArray(Collection objects) {
        int i = 0;
        final float[] floats = new float[objects.size()];
        for (Number number : (Collection<Number>) objects) {
            floats[i++] = number.floatValue();
        }
        return floats;
    }

    private static short[] toShortArray(Collection objects) {
        int i = 0;
        final short[] shorts = new short[objects.size()];
        for (Number number : (Collection<Number>) objects) {
            shorts[i++] = number.shortValue();
        }
        return shorts;
    }

    private static char[] toCharArray(Collection objects) {
        int i = 0;
        final char[] chars = new char[objects.size()];
        for (Object object : objects) {
            final char c;
            if (object instanceof Character) {
                c = (char) object;
            } else if (object instanceof String) {
                c = ((String) object).charAt(0);
            } else {
                throw new MojangsonParseException("Cannot convert " + object.getClass().getName() + " into a char");
            }
            chars[i++] = c;
        }
        return chars;
    }

    private static boolean[] toBooleanArray(Collection objects) {
        int i = 0;
        final boolean[] booleans = new boolean[objects.size()];
        for (Object object : objects) {
            final boolean b;
            if (object instanceof Boolean) {
                b = (boolean) object;
            } else if (object instanceof Number) {
                b = ((Number) object).intValue() > 0;
            } else {
                throw new MojangsonParseException("Cannot convert " + object.getClass().getName() + " into a char");
            }
            booleans[i++] = b;
        }
        return booleans;
    }

    private static String[] toStringArray(Collection objects) {
        int i = 0;
        final String[] strings = new String[objects.size()];
        for (Object object : (Collection<Object>) objects) {
            strings[i++] = object.toString();
        }
        return strings;
    }

    private Map[] parseMapArray() {
        final Collection<Map> maps = (Collection) parseListObjects(ExtendedObjectType.MAP);
        return maps.toArray(new Map[0]);
    }

    private Object parseArrayOrList(@Nullable ExtendedObjectType arrayObjectType, @Nullable ExtendedObjectType elementType) {
        nextChar(); // Skip [
        skipWhitespace();
        // Check for arrays using a single token
        char arrayType = currentChar();
        if (currentChar(1) == TOKEN_ARRAY_TYPE_SUFFIX) {
            if (!Chars.contains(ARRAY_TYPE_TOKENS, arrayType)) {
                throw new MojangsonParseException("Unsupported array type token: " + arrayType);
            }
            // Skip array type chars
            nextChar();
            nextChar();
        } else {
            int offset = 0;
            String arrayTypeName = null;
            while (true) {
                final char c = currentChar(offset);
                if (c == TOKEN_ARRAY_TYPE_SUFFIX) {
                    arrayTypeName = new String(this.content, this.pos, offset);
                    this.pos += offset; // Skip name
                    this.pos++; // Skip ;
                    break;
                } else if (offset >= LONGEST_NAMED_ARRAY_TOKEN || shouldCharBeQuoted(c)) {
                    break;
                }
                offset++;
            }
            if (arrayTypeName != null) {
                arrayType = NAMED_ARRAY_TOKENS.getChar(arrayTypeName.toLowerCase());
                if (arrayType == TOKEN_INVALID_ARRAY_TYPE) {
                    throw new MojangsonParseException("Unsupported array type name: " + arrayTypeName);
                }
            } else if (arrayObjectType == null) {
                return parseListObjects(elementType);
            }
        }
        if (arrayObjectType != null) {
            if (arrayObjectType == ExtendedObjectType.MAP_ARRAY) {
                return parseMapArray();
            }
            final Collection<Object> objects = parseListObjects(elementType);
            switch (arrayObjectType) {
                case VIEW_ARRAY:
                    return objects.toArray(new DataView[0]);
                case STRING_ARRAY:
                    return toStringArray(objects);
                case BOOLEAN_ARRAY:
                    return toBooleanArray(objects);
                case CHAR_ARRAY:
                    return toCharArray(objects);
                case SHORT_ARRAY:
                    return toShortArray(objects);
                case FLOAT_ARRAY:
                    return toFloatArray(objects);
                case DOUBLE_ARRAY:
                    return toDoubleArray(objects);
            }
            throw new MojangsonParseException("Unexpected array object type: " + arrayObjectType);
        }
        switch (arrayType) {
            case TOKEN_MAP_ARRAY:
            case TOKEN_MAP_ARRAY_UPPER:
                return parseMapArray();
        }
        final Collection<Object> objects = parseListObjects(null);
        int i = 0;
        switch (arrayType) {
            case TOKEN_VIEW_ARRAY:
            case TOKEN_VIEW_ARRAY_UPPER:
                return objects.toArray(new DataView[0]);
            case TOKEN_CHAR:
            case TOKEN_CHAR_UPPER:
                return toCharArray(objects);
            case TOKEN_BOOLEAN:
            case TOKEN_BOOLEAN_UPPER:
                return toBooleanArray(objects);
            case TOKEN_STRING_ARRAY:
            case TOKEN_STRING_ARRAY_UPPER:
                return toStringArray(objects);
        }
        final Collection<? extends Number> numbers = (Collection) objects;
        switch (arrayType) {
            case TOKEN_BYTE:
            case TOKEN_BYTE_UPPER:
                final byte[] bytes = new byte[numbers.size()];
                for (Number number : numbers) {
                    bytes[i++] = number.byteValue();
                }
                return bytes;
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
            case TOKEN_SHORT:
            case TOKEN_SHORT_UPPER:
                return toShortArray(numbers);
            case TOKEN_FLOAT:
            case TOKEN_FLOAT_UPPER:
                return toFloatArray(numbers);
            case TOKEN_DOUBLE:
            case TOKEN_DOUBLE_UPPER:
                return toDoubleArray(numbers);
        }
        throw new MojangsonParseException("Array type '" + arrayType + "' is not being handled.");
    }

    private List<Object> parseListObjects(@Nullable ExtendedObjectType type) {
        final List<Object> objects = new ArrayList<>();
        while (true) {
            skipWhitespace(); // Skip leading whitespaces
            char c = currentChar();
            // End of the array
            if (c == TOKEN_ARRAY_CLOSE) {
                return objects;
            }
            objects.add(parseObject(type, null, null));
            skipWhitespace();
            c = currentChar();
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

    private void checkAndSkipKeyValueSeparator() {
        skipWhitespace();
        final char c = currentChar();
        if (c != TOKEN_KEY_VALUE_SEPARATOR) {
            final boolean equalAlt = hasFlag(Mojangson.Flags.ALLOW_EQUAL_KEY_VALUE_SEPARATOR);
            if (!equalAlt || c != TOKEN_KEY_VALUE_SEPARATOR_ALT) {
                throw new MojangsonParseException("Expected '" + TOKEN_KEY_VALUE_SEPARATOR + "' " +
                        (equalAlt ? "or '" + TOKEN_KEY_VALUE_SEPARATOR_ALT + "' " : "") + "but got '" + c + "'");
            }
        }
        nextChar();
    }

    private Map parseMap() {
        final char c = currentChar();
        if (c == TOKEN_MAP_OPEN) {
            return parseFancyMap();
        } else if (c == TOKEN_ARRAY_OPEN) {
            return parseCompatibleMap();
        } else if (c == TOKEN_VIEW_OPEN) {
            return parseFancyMap(TOKEN_VIEW_CLOSE);
        }
        throw new MojangsonParseException("Expected '" + TOKEN_MAP_OPEN + "' or '" +
                TOKEN_ARRAY_OPEN + "' but got '" + c + "'");
    }

    /**
     * Parses a fancy map, using ( ) brackets, these will be parsed a (key: value)
     * supporting every value type as key, even arrays and views.
     *
     * @return The map
     */
    private Map parseFancyMap() {
        return parseFancyMap(TOKEN_MAP_CLOSE);
    }

    private Map parseFancyMap(char endToken) {
        nextChar(); // Skip (
        final Map map = new HashMap();
        while (true) {
            skipWhitespace(); // Skip leading whitespaces
            char c = currentChar();
            // End of the array
            if (c == TOKEN_MAP_CLOSE) {
                return map;
            }
            final Object key = parseObject();
            checkAndSkipKeyValueSeparator();
            final Object value = parseObject();
            map.put(key, value);
            c = currentChar();
            nextChar();
            if (c == endToken) {
                return map;
            } else if (c == TOKEN_NEW_ENTRY) {
                continue;
            }
            throw new MojangsonParseException("Got unexpected token: " + c);
        }
    }

    /**
     * Parses a "compatible" map, which is supported in the original mojangson.
     *
     * @return The map
     */
    private Map parseCompatibleMap() {
        nextChar(); // Skip [
        final Map map = new HashMap();
        while (true) {
            skipWhitespace(); // Skip leading whitespaces
            char c = currentChar();
            // End of the array
            if (c == TOKEN_ARRAY_CLOSE) {
                return map;
            }
            skipWhitespace();
            // 0: key
            // 1: value
            final Object[] entry = new Object[2];
            parseViewOrMap(null, (k, v) -> {
                if (k.equalsIgnoreCase(ExtendedObjectType.mapKeyName)) {
                    entry[0] = v;
                } else if (k.equalsIgnoreCase(ExtendedObjectType.mapValueName)) {
                    entry[1] = v;
                }
            });
            if (entry[0] == null) {
                throw new MojangsonParseException("Map entry is missing a key");
            }
            if (entry[1] == null) {
                throw new MojangsonParseException("Map entry is missing a value");
            }
            map.put(entry[0], entry[1]);
            c = currentChar();
            nextChar();
            switch (c) {
                case TOKEN_ARRAY_CLOSE:
                    return map;
                case TOKEN_NEW_ENTRY:
                    break;
                default:
                    throw new MojangsonParseException("Got unexpected token: " + c);
            }
        }
    }

    private DataView parseView(@Nullable DataView parent, @Nullable String key) {
        final DataView dataView;
        if (parent != null) {
            dataView = parent.createView(DataQuery.of(key));
        } else {
            dataView = new MemoryDataContainer(DataView.SafetyMode.NO_DATA_CLONED);
        }
        parseViewOrMap(dataView, (k, v) -> dataView.set(DataQuery.of(k), v));
        return dataView;
    }

    private void parseViewOrMap(@Nullable DataView dataView, BiConsumer<String, Object> valueAdder) {
        nextChar(); // Skip {
        while (true) {
            skipWhitespace(); // Skip leading whitespaces
            char c = currentChar();
            // Check for the end of the data view
            if (c == TOKEN_VIEW_CLOSE) {
                return;
            }
            String name = parseString();
            ExtendedObjectType objectType = null;
            boolean forceList = false;
            final int index = name.indexOf('$');
            if (index != -1) {
                String suffix = name.substring(index + 1);
                final int index1 = suffix.indexOf('$');
                if (index1 != -1 && suffix.substring(0, index1).equals("List")) {
                    forceList = true;
                    suffix = suffix.substring(index1 + 1);
                }
                objectType = ExtendedObjectType.bySuffix.get(suffix);
                if (objectType != null) {
                    name = name.substring(0, index);
                }
            }
            checkAndSkipKeyValueSeparator();
            final Object value;
            if (forceList) {
                skipWhitespace();
                if (currentChar() != TOKEN_ARRAY_OPEN) {
                    throw new MojangsonParseException("Expected a [ but got " + c);
                }
                value = parseArrayOrList(null, objectType);
            } else {
                value = parseObject(objectType, dataView, name);
            }
            // Only set the value if it's a view, DataViews
            // are already set internally by using createView
            // on it's parent
            if (dataView == null || !(value instanceof DataView)) {
                valueAdder.accept(name, value);
            }
            skipWhitespace(); // Skip again whitespaces after object
            c = currentChar();
            nextChar();
            switch (c) {
                case TOKEN_VIEW_CLOSE:
                    return; // Reached the end
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
        final Object object = parseObject(null, null, null);
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
        return parseObject(null, null, null);
    }

    private String parseString() {
        final char c = currentChar();
        if (c == TOKEN_DOUBLE_QUOTED_STRING ||
                (hasFlag(Mojangson.Flags.ALLOW_SINGLE_QUOTES) && c == TOKEN_SINGLE_QUOTED_STRING)) {
            return parseQuotedString();
        } else {
            return parseUnquotedString();
        }
    }

    private Object parseObject(@Nullable ExtendedObjectType type, @Nullable DataView parent, @Nullable String key) {
        skipWhitespace();
        char c = currentChar();
        // Check which type should be parsed
        if (type != null) { // Handle suffixes first
            switch (type) {
                // Custom handling for suffixes
                case BOOLEAN:
                    final Object object = parseObject(null, null, null);
                    if (object instanceof Boolean) {
                        return object;
                    } else if (object instanceof String) {
                        return Boolean.valueOf((String) object);
                    } else if (object instanceof Number) {
                        return ((Number) object).longValue() > 0;
                    }
                    throw new MojangsonParseException("Cannot convert " + object.getClass().getName() + " into a boolean");
                case CHAR:
                    return parseString().charAt(0);
                case MAP:
                    return parseMap();
                // Validate arrays
                case MAP_ARRAY:
                case VIEW_ARRAY:
                case SHORT_ARRAY:
                case FLOAT_ARRAY:
                case DOUBLE_ARRAY:
                case BOOLEAN_ARRAY:
                case CHAR_ARRAY:
                    if (c != TOKEN_ARRAY_OPEN) {
                        throw new MojangsonParseException("Expected a [ but got " + c);
                    }
                    break;
                default:
                    throw new MojangsonParseException("Got unexpected object type: " + type);
            }
        }
        switch (c) {
            case TOKEN_VIEW_OPEN:
                return parseView(parent, key);
            case TOKEN_ARRAY_OPEN:
                return parseArrayOrList(type, null);
            case TOKEN_MAP_OPEN:
                return parseFancyMap();
            case TOKEN_SINGLE_QUOTED_STRING:
                if (!hasFlag(Mojangson.Flags.ALLOW_SINGLE_QUOTES)) {
                    break;
                }
                // fall through
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
                if (hasFlag(Mojangson.Flags.PARSE_BOOLEAN_AS_BYTE)) {
                    return (byte) 1;
                }
                return true;
            case "false":
                if (hasFlag(Mojangson.Flags.PARSE_BOOLEAN_AS_BYTE)) {
                    return (byte) 0;
                }
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
