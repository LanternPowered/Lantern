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

import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class GenericArguments2 {

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
     * @return the element to match the input
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
