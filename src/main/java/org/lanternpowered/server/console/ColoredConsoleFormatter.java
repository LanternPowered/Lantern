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
import org.lanternpowered.server.text.TextConstants;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Pattern;

@NonnullByDefault
public class ColoredConsoleFormatter implements Function<String, String> {

    private static Pattern c(char code) {
        return Pattern.compile(String.valueOf(TextConstants.LEGACY_CHAR) + code, Pattern.LITERAL | Pattern.CASE_INSENSITIVE);
    }

    private static final String RESET = ansi().reset().toString();
    private static final ImmutableMap<Pattern, String> REPLACEMENTS = ImmutableMap.<Pattern, String>builder()
            .put(c(TextConstants.BLACK), ansi().a(Attribute.RESET).fg(Color.BLACK).boldOff().toString())
            .put(c(TextConstants.DARK_BLUE), ansi().a(Attribute.RESET).fg(Color.BLUE).boldOff().toString())
            .put(c(TextConstants.DARK_GREEN), ansi().a(Attribute.RESET).fg(Color.GREEN).boldOff().toString())
            .put(c(TextConstants.DARK_AQUA), ansi().a(Attribute.RESET).fg(Color.CYAN).boldOff().toString())
            .put(c(TextConstants.DARK_RED), ansi().a(Attribute.RESET).fg(Color.RED).boldOff().toString())
            .put(c(TextConstants.DARK_PURPLE), ansi().a(Attribute.RESET).fg(Color.MAGENTA).boldOff().toString())
            .put(c(TextConstants.GOLD), ansi().a(Attribute.RESET).fg(Color.YELLOW).boldOff().toString())
            .put(c(TextConstants.GRAY), ansi().a(Attribute.RESET).fg(Color.WHITE).boldOff().toString())
            .put(c(TextConstants.DARK_GRAY), ansi().a(Attribute.RESET).fg(Color.BLACK).bold().toString())
            .put(c(TextConstants.BLUE), ansi().a(Attribute.RESET).fg(Color.BLUE).bold().toString())
            .put(c(TextConstants.GREEN), ansi().a(Attribute.RESET).fg(Color.GREEN).bold().toString())
            .put(c(TextConstants.AQUA), ansi().a(Attribute.RESET).fg(Color.CYAN).bold().toString())
            .put(c(TextConstants.RED), ansi().a(Attribute.RESET).fg(Color.RED).bold().toString())
            .put(c(TextConstants.LIGHT_PURPLE), ansi().a(Attribute.RESET).fg(Color.MAGENTA).bold().toString())
            .put(c(TextConstants.YELLOW), ansi().a(Attribute.RESET).fg(Color.YELLOW).bold().toString())
            .put(c(TextConstants.WHITE), ansi().a(Attribute.RESET).fg(Color.WHITE).bold().toString())
            .put(c(TextConstants.OBFUSCATED), ansi().a(Attribute.BLINK_SLOW).toString())
            .put(c(TextConstants.BOLD), ansi().a(Attribute.INTENSITY_BOLD).toString())
            .put(c(TextConstants.STRIKETHROUGH), ansi().a(Attribute.STRIKETHROUGH_ON).toString())
            .put(c(TextConstants.UNDERLINE), ansi().a(Attribute.UNDERLINE).toString())
            .put(c(TextConstants.ITALIC), ansi().a(Attribute.ITALIC).toString())
            .put(c(TextConstants.RESET), ansi().a(Attribute.RESET).toString())
            .build();

    @Override
    public String apply(String text) {
        for (Entry<Pattern, String> entry : REPLACEMENTS.entrySet()) {
            text = entry.getKey().matcher(text).replaceAll(entry.getValue());
        }
        return text + RESET;
    }

}
