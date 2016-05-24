/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.command.element;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.util.StartsWithPredicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public final class GenericArguments2 {

    /**
     * Gives a {@link Color}, the color can be parsed
     * in 3 different formats: literal which will match
     * some inbuilt mappings, rgb which will use 3 components
     * to parse the color and hex which will parse the
     * hex format of a color.
     *
     * @param key The key to store the color under
     * @return The element to match the input
     */
    public static CommandElement color(Text key) {
        return new ColorElement(key, null);
    }

    /**
     * Gives a {@link Color}, the color can be parsed
     * in 3 different formats: literal which will match
     * some inbuilt mappings, rgb which will use 3 components
     * to parse the color and hex which will parse the
     * hex format of a color.
     *
     * <p>The default color will be used in the tab completation,
     * it will be the first entry when completing a empty color
     * element or the only entry when completing hex or rgb.</p>
     *
     * @param key The key to store the color under
     * @param defaultColor The default color used in tab completation
     * @return The element to match the input
     */
    public static CommandElement color(Text key, @Nullable Color defaultColor) {
        return new ColorElement(key, defaultColor);
    }

    private static class ColorElement extends CommandElement {

        private static final Pattern RGB_PATTERN = Pattern.compile("^[0-9,]+$");
        private static final Map<String, Color> INBUILT_COLORS = ImmutableMap.<String, Color>builder()
                .put("black", Color.BLACK)
                .put("blue", Color.BLUE)
                .put("cyan", Color.CYAN)
                .put("darkcyan", Color.DARK_CYAN)
                .put("darkgreen", Color.DARK_GREEN)
                .put("darkmagenta", Color.DARK_MAGENTA)
                .put("gray", Color.GRAY)
                .put("green", Color.GREEN)
                .put("lime", Color.LIME)
                .put("magenta", Color.MAGENTA)
                .put("navy", Color.NAVY)
                .put("pink", Color.PINK)
                .put("purple", Color.PURPLE)
                .put("red", Color.RED)
                .put("white", Color.WHITE)
                .put("yellow", Color.YELLOW)
                .build();
        private static final List<String> INBUILT_COLOR_NAMES = ImmutableList.copyOf(INBUILT_COLORS.keySet());

        public static String findClosestColorName(String target) {
            int distance = Integer.MAX_VALUE;
            String closest = null;
            for (String name : INBUILT_COLORS.keySet()) {
                int currentDistance = StringUtils.getLevenshteinDistance(name, target);
                if (currentDistance < distance) {
                    distance = currentDistance;
                    closest = name;
                }
            }
            return closest;
        }

        @Nullable private final Color defaultColor;
        @Nullable private final ImmutableList<String> sortedColorNames;

        ColorElement(Text key, @Nullable Color defaultColor) {
            super(key);
            this.defaultColor = defaultColor;
            if (defaultColor != null) {
                for (Map.Entry<String, Color> entry : INBUILT_COLORS.entrySet()) {
                    if (entry.getValue().equals(defaultColor)) {
                        final ImmutableList.Builder<String> entries = ImmutableList.builder();
                        entries.add(entry.getKey());
                        // Add all the other colors except the first element
                        INBUILT_COLORS.keySet().stream()
                                .filter(colorName -> !colorName.equals(entry.getKey()))
                                .forEach(entries::add);
                        this.sortedColorNames = entries.build();
                        return;
                    }
                }
            }
            this.sortedColorNames = null;
        }

        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            String rStr = args.next();
            // Check for hex format if allowed
            if (rStr.startsWith("0x") || rStr.startsWith("#")) {
                // Get the hex value without the prefix
                String value = rStr.substring(rStr.startsWith("0x") ? 2 : 1);
                int hex;
                try {
                    hex = Integer.parseInt(value, 16);
                } catch (NumberFormatException e) {
                    throw args.createError(t("Expected input %s to be hexadecimal, but it was not"));
                }
                return Color.ofRgb(hex);
            }
            // Check whether the format matches
            if (RGB_PATTERN.matcher(rStr).matches()) {
                String gStr;
                String bStr;
                // Try for the comma-separated format
                if (rStr.contains(",")) {
                    String[] split = rStr.split(",");
                    if (split.length != 3) {
                        throw args.createError(t("Comma-separated color must have 3 elements, not %s", split.length));
                    }
                    rStr = split[0];
                    gStr = split[1];
                    bStr = split[2];
                } else {
                    gStr = args.next();
                    bStr = args.next();
                }
                int r = parseComponent(args, rStr, "r");
                int g = parseComponent(args, gStr, "g");
                int b = parseComponent(args, bStr, "b");
                return Color.of(new Vector3i(r, g, b));
            }
            Color color = INBUILT_COLORS.get(rStr.toLowerCase());
            if (color == null) {
                throw args.createError(t("Unknown inbuilt color: %s Did you mean: %s ?", rStr, findClosestColorName(rStr)));
            }
            return color;
        }

        private static int parseComponent(CommandArgs args, String arg, String name) throws ArgumentParseException {
            try {
                int value = Integer.parseInt(arg);
                if (value < 0) {
                    throw args.createError(t("Number %s for %s component is too small, it must be at least %s", value, name, 0));
                }
                if (value > 255) {
                    throw args.createError(t("Number %s for %s component is too big, it must be at most %s", value, name, 255));
                }
                return value;
            } catch (NumberFormatException e) {
                throw args.createError(t("Expected input %s for %s component to be a number, but it was not", arg, name));
            }
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            Optional<String> arg = args.nextIfPresent();
            if (!arg.isPresent()) {
                return Collections.emptyList();
            }
            final String rStr = arg.get();
            if (args.nextIfPresent().isPresent()) {
                if (rStr.startsWith("0x") || rStr.startsWith("#") ||
                        !RGB_PATTERN.matcher(rStr).matches() || rStr.contains(",")) {
                    return Collections.emptyList();
                }
                if (args.nextIfPresent().isPresent()) {
                    // Store the current state
                    Object state = args.getState();
                    if (args.nextIfPresent().isPresent()) {
                        // We finished the vector3d, reset before the last arg
                        args.setState(state);
                    } else {
                        // The blue is being completed
                        if (this.defaultColor != null) {
                            return Collections.singletonList(Integer.toString(this.defaultColor.getBlue()));
                        } else {
                            return Collections.emptyList();
                        }
                    }
                } else {
                    // The green is being completed
                    if (this.defaultColor != null) {
                        return Collections.singletonList(Integer.toString(this.defaultColor.getGreen()));
                    } else {
                        return Collections.emptyList();
                    }
                }
            } else {
                if (rStr.isEmpty()) {
                    if (this.sortedColorNames == null) {
                        return INBUILT_COLOR_NAMES;
                    }
                    return this.sortedColorNames;
                }
                if (rStr.startsWith("0x") || rStr.startsWith("#")) {
                    if (this.defaultColor == null) {
                        return Collections.emptyList();
                    }
                    final String prefix = rStr.charAt(0) == '#' ? "#" : "0x";
                    return Collections.singletonList(prefix + Integer.toHexString(this.defaultColor.getRgb()));
                }
                if (RGB_PATTERN.matcher(rStr).matches()) {
                    if (this.defaultColor == null) {
                        return Collections.emptyList();
                    }
                    if (rStr.contains(",")) {
                        int partCount = rStr.split(",").length;
                        int index = rStr.lastIndexOf(',');
                        if (partCount > 3) {
                            return Collections.emptyList();
                        }
                        String begin = index == -1 ? "" : rStr.substring(0, index + 1);
                        int value = partCount == 0 ? this.defaultColor.getRed() :
                                partCount == 1 ? this.defaultColor.getGreen() : this.defaultColor.getBlue();
                        return Collections.singletonList(begin + Integer.toString(value));
                    }
                    return Collections.singletonList(Integer.toString(this.defaultColor.getRed()));
                } else {
                    return INBUILT_COLOR_NAMES.stream().filter(new StartsWithPredicate(rStr.toLowerCase()))
                            .collect(GuavaCollectors.toImmutableList());
                }
            }
            return Collections.emptyList();
        }
    }

    /**
     * Gives a string array which contains all the
     * remaining arguments.
     *
     * @param key The key to store the string array under
     * @return The element to match the input
     */
    public static CommandElement remainingStringArray(Text key) {
        return new StringArrayElement(key);
    }

    private static class StringArrayElement extends CommandElement {

        private StringArrayElement(Text key) {
            super(key);
        }

        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            List<String> values = new ArrayList<>();
            // Move the position to the end
            while (args.hasNext()) {
                String arg = args.next();
                if (!arg.isEmpty()) {
                    Lantern.getLogger().info(arg);
                    values.add(arg);
                }
            }
            return values.toArray(new String[values.size()]);
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            return Collections.emptyList();
        }
    }

    /**
     * Gives a string that includes all the remaining
     * arguments. This will include the original
     * spacing.
     *
     * @param key The key to store the string under
     * @return The element to match the input
     */
    public static CommandElement remainingString(Text key) {
        return new RemainingStringElement(key);
    }

    private static class RemainingStringElement extends CommandElement {

        private RemainingStringElement(Text key) {
            super(key);
        }

        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            args.next();
            String text = args.getRaw().substring(args.getRawPosition());
            // Move the position to the end
            while (args.hasNext()) {
                args.next();
            }
            return text;
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            return Collections.emptyList();
        }
    }

    /**
     * Require the argument to be a key under the provided
     * enum. Gives values of type T.
     *
     * Unlike the {@link GenericArguments#enumValue(Text, Class)} command element
     * are the enum values case insensitive and will the default names of the enum
     * be mapped to {@link Object#toString()}.
     *
     * @param key The key to store the matched enum value under
     * @param type The enum class to get enum constants from
     * @param <T> The type of enum
     * @return The element to match the input
     */
    public static <T extends Enum<T>> CommandElement enumValue(Text key, Class<T> type) {
        return new EnumValueElement<>(key, type);
    }

    private static class EnumValueElement<T extends Enum<T>> extends PatternMatchingCommandElement {

        private final Map<String, T> mappings;

        EnumValueElement(Text key, Class<T> type) {
            super(key);

            final ImmutableMap.Builder<String, T> builder = ImmutableMap.builder();
            for (T enumValue : type.getEnumConstants()) {
                builder.put(enumValue.toString().toLowerCase(), enumValue);
            }
            this.mappings = builder.build();
        }

        @Override
        protected Iterable<String> getChoices(CommandSource source) {
            return this.mappings.values().stream().map(Object::toString).collect(Collectors.toList());
        }

        @Override
        protected Object getValue(String choice) throws IllegalArgumentException {
            return this.mappings.get(choice.toLowerCase());
        }
    }

    private GenericArguments2() {
    }
}
