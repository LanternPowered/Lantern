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
package org.lanternpowered.server.console;

import static org.fusesource.jansi.Ansi.ansi;

import it.unimi.dsi.fastutil.chars.Char2ByteMap;
import it.unimi.dsi.fastutil.chars.Char2ByteOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;
import org.lanternpowered.server.text.TextConstants;

import java.util.function.Function;

final class ColoredConsoleFormatter implements Function<String, String> {

    private static final String reset = ansi().reset().toString();
    private static final Int2ObjectMap<String> replacements = new Int2ObjectOpenHashMap<>();
    private static final Char2ByteMap lookup = new Char2ByteOpenHashMap();

    private static void add(char code, Ansi replacement) {
        replacements.put(code, replacement.toString());
        // Add here one so we can check for 0 by default,
        // this requires also to subtract the one for lookups
        lookup.put(code, (byte) ((byte) code + 1));
    }

    static {
        add(TextConstants.BLACK, ansi().a(Attribute.RESET).fg(Color.BLACK).boldOff());
        add(TextConstants.DARK_BLUE, ansi().a(Attribute.RESET).fg(Color.BLUE).boldOff());
        add(TextConstants.DARK_GREEN, ansi().a(Attribute.RESET).fg(Color.GREEN).boldOff());
        add(TextConstants.DARK_AQUA, ansi().a(Attribute.RESET).fg(Color.CYAN).boldOff());
        add(TextConstants.DARK_RED, ansi().a(Attribute.RESET).fg(Color.RED).boldOff());
        add(TextConstants.DARK_PURPLE, ansi().a(Attribute.RESET).fg(Color.MAGENTA).boldOff());
        add(TextConstants.GOLD, ansi().a(Attribute.RESET).fg(Color.YELLOW).boldOff());
        add(TextConstants.GRAY, ansi().a(Attribute.RESET).fg(Color.WHITE).boldOff());
        add(TextConstants.DARK_GRAY, ansi().a(Attribute.RESET).fg(Color.BLACK).bold());
        add(TextConstants.BLUE, ansi().a(Attribute.RESET).fg(Color.BLUE).bold());
        add(TextConstants.GREEN, ansi().a(Attribute.RESET).fg(Color.GREEN).bold());
        add(TextConstants.AQUA, ansi().a(Attribute.RESET).fg(Color.CYAN).bold());
        add(TextConstants.RED, ansi().a(Attribute.RESET).fg(Color.RED).bold());
        add(TextConstants.LIGHT_PURPLE, ansi().a(Attribute.RESET).fg(Color.MAGENTA).bold());
        add(TextConstants.YELLOW, ansi().a(Attribute.RESET).fg(Color.YELLOW).bold());
        add(TextConstants.WHITE, ansi().a(Attribute.RESET).fg(Color.WHITE).bold());
        add(TextConstants.OBFUSCATED, ansi().a(Attribute.BLINK_SLOW));
        add(TextConstants.BOLD,ansi().a(Attribute.INTENSITY_BOLD));
        add(TextConstants.STRIKETHROUGH, ansi().a(Attribute.STRIKETHROUGH_ON));
        add(TextConstants.UNDERLINE, ansi().a(Attribute.UNDERLINE));
        add(TextConstants.ITALIC, ansi().a(Attribute.ITALIC));
        add(TextConstants.RESET, ansi().a(Attribute.RESET));
    }

    @Override
    public String apply(String text) {
        int next = text.indexOf(TextConstants.LEGACY_CHAR);
        int last = text.length() - 1;
        if (next == -1 || next == last) {
            return text;
        }

        StringBuilder result = new StringBuilder(text.length() + 20);

        int pos = 0;
        int format;
        do {
            if (pos != next) {
                result.append(text, pos, next);
            }

            pos = next;

            format = lookup.get(text.charAt(next + 1));
            if (format != 0) {
                result.append(replacements.get(format - 1));
                pos = next += 2;
            } else {
                next++;
            }

            next = text.indexOf(TextConstants.LEGACY_CHAR, next);
        } while (next != -1 && next < last);

        return result.append(text, pos, text.length()).append(reset).toString();
    }

}
