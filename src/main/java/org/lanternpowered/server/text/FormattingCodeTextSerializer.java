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
package org.lanternpowered.server.text;

import static com.google.common.base.Preconditions.checkNotNull;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2CharMap;
import it.unimi.dsi.fastutil.objects.Object2CharOpenHashMap;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.server.text.translation.TranslationContext;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

/**
 * TODO: Separate the usage of {@link TextColors#RESET} and {@link TextStyles#RESET}.
 */
public class FormattingCodeTextSerializer extends DefaultCatalogType
        implements org.spongepowered.api.text.serializer.FormattingCodeTextSerializer, LanternTextSerializer {

    public static final Object2CharMap<Object> FORMATS_TO_CODE = new Object2CharOpenHashMap<>();
    public static final Char2ObjectMap<Object> CODE_TO_FORMATS = new Char2ObjectOpenHashMap<>();

    private static void addFormat(Object format, char code) {
        FORMATS_TO_CODE.put(format, code);
        CODE_TO_FORMATS.put(code, format);
    }

    static {
        addFormat(TextColors.BLACK, TextConstants.BLACK);
        addFormat(TextColors.DARK_BLUE, TextConstants.DARK_BLUE);
        addFormat(TextColors.DARK_GREEN, TextConstants.DARK_GREEN);
        addFormat(TextColors.DARK_AQUA, TextConstants.DARK_AQUA);
        addFormat(TextColors.DARK_RED, TextConstants.DARK_RED);
        addFormat(TextColors.DARK_PURPLE, TextConstants.DARK_PURPLE);
        addFormat(TextColors.GOLD, TextConstants.GOLD);
        addFormat(TextColors.GRAY, TextConstants.GRAY);
        addFormat(TextColors.DARK_GRAY, TextConstants.DARK_GRAY);
        addFormat(TextColors.BLUE, TextConstants.BLUE);
        addFormat(TextColors.GREEN, TextConstants.GREEN);
        addFormat(TextColors.AQUA, TextConstants.AQUA);
        addFormat(TextColors.RED, TextConstants.RED);
        addFormat(TextColors.LIGHT_PURPLE, TextConstants.LIGHT_PURPLE);
        addFormat(TextColors.YELLOW, TextConstants.YELLOW);
        addFormat(TextColors.WHITE, TextConstants.WHITE);
        addFormat(TextColors.RESET, TextConstants.RESET);
        addFormat(TextStyles.OBFUSCATED, TextConstants.OBFUSCATED);
        addFormat(TextStyles.BOLD, TextConstants.BOLD);
        addFormat(TextStyles.STRIKETHROUGH, TextConstants.STRIKETHROUGH);
        addFormat(TextStyles.UNDERLINE, TextConstants.UNDERLINE);
        addFormat(TextStyles.ITALIC, TextConstants.ITALIC);
    }

    private static boolean isFormat(char format) {
        boolean flag = CODE_TO_FORMATS.containsValue(format);
        if (!flag) {
            flag = CODE_TO_FORMATS.containsValue(Character.toLowerCase(format));
        }
        return flag;
    }

    @Nullable
    private static Object getFormat(char format) {
        Object obj = CODE_TO_FORMATS.get(format);
        if (obj == null) {
            obj = CODE_TO_FORMATS.get(Character.toLowerCase(format));
        }
        return obj;
    }

    private final char formattingCode;

    public FormattingCodeTextSerializer(CatalogKey key, char formattingCode) {
        super(key);
        this.formattingCode = formattingCode;
    }

    @Override
    public char getCharacter() {
        return this.formattingCode;
    }

    @Override
    public String stripCodes(String text) {
        return strip(text, this.formattingCode);
    }

    @Override
    public String replaceCodes(String text, char to) {
        return replace(text, this.formattingCode, to);
    }

    @Override
    public String serialize(Text text) {
        return serialize(text, TranslationContext.current().getLocale());
    }

    @Override
    public String serialize(Text text, Locale locale) {
        checkNotNull(text, "text");
        checkNotNull(locale, "locale");
        return LegacyTexts.toLegacy(locale, text, this.formattingCode);
    }

    @Override
    public Text deserialize(String input) throws TextParseException {
        return deserializeUnchecked(input);
    }

    @Override
    public Text deserializeUnchecked(String input) {
        checkNotNull(input, "input");

        int next = input.lastIndexOf(this.formattingCode, input.length() - 2);
        if (next == -1) {
            return Text.of(input);
        }

        final List<Text> parts = new ArrayList<>();

        LiteralText.Builder current = null;
        boolean reset = false;

        int pos = input.length();
        do {
            Object format = getFormat(input.charAt(next + 1));
            if (format != null) {
                int from = next + 2;
                if (from != pos) {
                    if (current != null) {
                        if (reset) {
                            parts.add(current.build());
                            reset = false;
                            current = Text.builder("");
                        } else {
                            current = Text.builder("").append(current.build());
                        }
                    } else {
                        current = Text.builder("");
                    }

                    current.content(input.substring(from, pos));
                } else if (current == null) {
                    current = Text.builder("");
                }

                reset |= applyStyle(current, format);
                pos = next;
            }

            next = input.lastIndexOf(this.formattingCode, next - 1);
        } while (next != -1);

        if (current != null) {
            parts.add(current.build());
        }

        Collections.reverse(parts);
        return Text.builder(pos > 0 ? input.substring(0, pos) : "").append(parts).build();
    }

    private static boolean applyStyle(Text.Builder builder, Object format) {
        if (format instanceof TextStyle) {
            builder.style((TextStyle) format);
            return false;
        } else if (format == TextColors.RESET) {
            return true;
        } else {
            if (builder.getColor() == TextColors.NONE) {
                builder.color((TextColor) format);
            }
            return true;
        }
    }

    static String replace(String text, char from, char to) {
        int pos = text.indexOf(from);
        int last = text.length() - 1;
        if (pos == -1 || pos == last) {
            return text;
        }

        char[] result = text.toCharArray();
        for (; pos < last; pos++) {
            if (result[pos] == from && isFormat(result[pos + 1])) {
                result[pos] = to;
            }
        }

        return new String(result);
    }

    public static String strip(String text, char code) {
        return strip(text, code, false);
    }

    public static String strip(String text, char code, boolean all) {
        int next = text.indexOf(code);
        int last = text.length() - 1;
        if (next == -1 || next == last) {
            return text;
        }

        StringBuilder result = new StringBuilder(text.length());

        int pos = 0;
        do {
            if (pos != next) {
                result.append(text, pos, next);
            }

            pos = next;

            if (isFormat(text.charAt(next + 1))) {
                pos = next += 2; // Skip formatting
            } else if (all) {
                pos = next += 1; // Skip code only
            } else {
                next++;
            }

            next = text.indexOf(code, next);
        } while (next != -1 && next < last);

        return result.append(text, pos, text.length()).toString();
    }

}
