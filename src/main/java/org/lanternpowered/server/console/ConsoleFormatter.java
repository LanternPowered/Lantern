/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.console;

import static org.fusesource.jansi.Ansi.ansi;

import com.google.common.collect.ImmutableMap;
import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;
import org.lanternpowered.server.text.LegacyTextRepresentation;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Map.Entry;
import java.util.regex.Pattern;

@NonnullByDefault
public class ConsoleFormatter implements Formatter {

    private static Pattern c(char code) {
        return Pattern.compile(String.valueOf(LegacyTextRepresentation.DEFAULT_CHAR) + code, Pattern.LITERAL | Pattern.CASE_INSENSITIVE);
    }

    private static final String RESET = ansi().reset().toString();
    private static final ImmutableMap<Pattern, String> REPLACEMENTS = ImmutableMap.<Pattern, String>builder()
            .put(c('0'), ansi().a(Attribute.RESET).fg(Color.BLACK).boldOff().toString())
            .put(c('1'), ansi().a(Attribute.RESET).fg(Color.BLUE).boldOff().toString())
            .put(c('2'), ansi().a(Attribute.RESET).fg(Color.GREEN).boldOff().toString())
            .put(c('3'), ansi().a(Attribute.RESET).fg(Color.CYAN).boldOff().toString())
            .put(c('4'), ansi().a(Attribute.RESET).fg(Color.RED).boldOff().toString())
            .put(c('5'), ansi().a(Attribute.RESET).fg(Color.MAGENTA).boldOff().toString())
            .put(c('6'), ansi().a(Attribute.RESET).fg(Color.YELLOW).boldOff().toString())
            .put(c('7'), ansi().a(Attribute.RESET).fg(Color.WHITE).boldOff().toString())
            .put(c('8'), ansi().a(Attribute.RESET).fg(Color.BLACK).bold().toString())
            .put(c('9'), ansi().a(Attribute.RESET).fg(Color.BLUE).bold().toString())
            .put(c('a'), ansi().a(Attribute.RESET).fg(Color.GREEN).bold().toString())
            .put(c('b'), ansi().a(Attribute.RESET).fg(Color.CYAN).bold().toString())
            .put(c('c'), ansi().a(Attribute.RESET).fg(Color.RED).bold().toString())
            .put(c('d'), ansi().a(Attribute.RESET).fg(Color.MAGENTA).bold().toString())
            .put(c('e'), ansi().a(Attribute.RESET).fg(Color.YELLOW).bold().toString())
            .put(c('f'), ansi().a(Attribute.RESET).fg(Color.WHITE).bold().toString())
            .put(c('k'), ansi().a(Attribute.BLINK_SLOW).toString())
            .put(c('l'), ansi().a(Attribute.INTENSITY_BOLD).toString())
            .put(c('m'), ansi().a(Attribute.STRIKETHROUGH_ON).toString())
            .put(c('n'), ansi().a(Attribute.UNDERLINE).toString())
            .put(c('o'), ansi().a(Attribute.ITALIC).toString())
            .put(c('r'), ansi().a(Attribute.RESET).toString())
            .build();

    @Override
    public String format(String text) {
        for (Entry<Pattern, String> entry : REPLACEMENTS.entrySet()) {
            text = entry.getKey().matcher(text).replaceAll(entry.getValue());
        }
        return text + RESET;
    }

}
