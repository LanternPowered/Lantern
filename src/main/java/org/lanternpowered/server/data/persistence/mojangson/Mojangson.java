package org.lanternpowered.server.data.persistence.mojangson;

/**
 * Mojangson is a variant of JSON developed by Mojang. All valid JSON is
 * valid mojangson, but mojangson also includes the following features:
 * <ul>
 *     <li>Arrays and objects can have trailing commas (<code>,</code>)</li>
 *     <li>Strings (name and value) can be used without quotes, if only the
 *     following characters are used: <code>a-zA-Z0-9.-_</code></li>
 *     <li>
 *         Numbers can be suffixed to represent a specific data type:
 *         <ul>
 *             <li><code>byte</code>: <code>b</code> or <code>B</code></li>
 *             <li><code>short</code>: <code>s</code> or <code>S</code></li>
 *             <li><code>int</code>: none</li>
 *             <li><code>long</code>: <code>l</code> or <code>L</code></li>
 *             <li><code>float</code>: <code>f</code> or <code>F</code></li>
 *             <li><code>double</code>: <code>d</code>, <code>D</code> or dotted notation e.g. <code>1.0</code></li>
 *         </ul>
 *     </li>
 *     <li>
 *         Arrays are represented by prefixing an array with a specific type, e.g. <code>[B;1b,2b,]</code>
 *         where <code>B;</code> is the prefix. Non prefixed arrays will be parsed as lists.
 *         The supported prefixes:
 *         <ul>
 *             <li><code>byte[]</code>: <code>B;</code>
 *             <li><code>int[]</code>: <code>I;</code>
 *             <li><code>long[]</code>: <code>L;</code>
 *         </ul>
 *     </li>
 * </ul>
 * Suffixes which are known in LanternNBT are also supporting within the Mojangson format.<br>
 * E.g. the following entry will be parsed as a double array with the <code>my_key</code> as key:<br>
 * <code>
 *     {<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;"my_key$double[]": [1,2,3]<br>
 *     }<br>
 * </code>
 */
public final class Mojangson {

    /**
     * A set of flags that can be used to customize the parsing behavior.
     */
    public static final class Flags {

        /**
         * This flags forces {@code true} and {@code false} to be stored as a
         * byte instead of a boolean, this is the case in default mojangson.
         * <p>If the value key is suffixed with {@code $Boolean} it will be
         * stored as a boolean and this flag will be ignored.
         */
        public static final int PARSE_BOOLEAN_AS_BYTE = 0x1;

        /**
         * Allows strings to be quoted using single quotes
         * instead of double quotes.
         */
        public static final int ALLOW_SINGLE_QUOTES = 0x2;

        /**
         * Allows the equal character (=) to be used as an optional
         * separator between keys and values within compounds.
         */
        public static final int ALLOW_EQUAL_KEY_VALUE_SEPARATOR = 0x4;

        /**
         * All the flags that are used by the mojangson format.
         */
        public static final int MOJANGSON =
                PARSE_BOOLEAN_AS_BYTE;

        /**
         * All the flags that are used by the lanterson format.
         */
        public static final int LANTERSON =
                ALLOW_SINGLE_QUOTES |
                        ALLOW_EQUAL_KEY_VALUE_SEPARATOR;
    }

    /**
     * Parses the mojangson/lanterson
     * string as a {@link Object}.
     *
     * @param value The value to parse
     * @return The parsed object
     */
    public static Object parseMojangson(String value) {
        return parse(value, Flags.MOJANGSON);
    }

    /**
     * Parses the mojangson/lanterson
     * string as a {@link Object}.
     *
     * @param value The value to parse
     * @return The parsed object
     */
    public static Object parseLanterson(String value) {
        return parse(value, Flags.LANTERSON);
    }

    /**
     * Parses the mojangson/lanterson
     * string as a {@link Object}.
     *
     * @param value The value to parse
     * @return The parsed object
     */
    public static Object parse(String value, int flags) {
        return new MojangsonParser(value, flags).parseCompleteObject();
    }

    public static String toMojangson(Object object) {
        return to(object, Flags.MOJANGSON);
    }

    public static String toLanterson(Object object) {
        return to(object, Flags.LANTERSON);
    }

    public static String to(Object object, int flags) {
        return null;
    }
}

