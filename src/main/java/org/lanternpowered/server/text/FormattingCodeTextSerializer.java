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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2CharMap;
import it.unimi.dsi.fastutil.objects.Object2CharOpenHashMap;
import org.lanternpowered.server.text.format.FormattingCodeHolder;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.ScoreText;
import org.spongepowered.api.text.SelectorText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;
import org.spongepowered.api.text.TranslatableText;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextParseException;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.text.translation.locale.Locales;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.Nullable;

public class FormattingCodeTextSerializer implements org.spongepowered.api.text.serializer.FormattingCodeTextSerializer, LanternTextSerializer {

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

    public FormattingCodeTextSerializer(char formattingCode) {
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
        return this.serialize(text, Locales.DEFAULT);
    }

    @Override
    public String serialize(Text text, Locale locale) {
        return to(checkNotNull(text, "text"), checkNotNull(locale, "locale"), new StringBuilder(), this.formattingCode).toString();
    }

    @Override
    public Text deserialize(String input) throws TextParseException {
        return this.deserializeUnchecked(input);
    }

    @Override
    public Text deserializeUnchecked(String input) {
        checkNotNull(input, "input");

        int next = input.lastIndexOf(this.formattingCode, input.length() - 2);
        if (next == -1) {
            return Text.of(input);
        }

        List<Text> parts = Lists.newArrayList();

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

    static StringBuilder to(Text text, Locale locale, StringBuilder builder, @Nullable Character colorCode) {
        return to(text, locale, builder, colorCode, null);
    }

    private static StringBuilder to(Text text, Locale locale, StringBuilder builder, @Nullable Character colorCode,
            @Nullable ResolvedChatStyle current) {
        ResolvedChatStyle style = null;

        if (colorCode != null) {
            style = resolve(current, text.getFormat());

            if (current == null || (current.color != style.color) || (current.bold && !style.bold) ||
                    (current.italic && !style.italic) || (current.underlined && !style.underlined) ||
                    (current.strikethrough && !style.strikethrough) || (current.obfuscated && !style.obfuscated)) {
                if (style.color != null) {
                    apply(builder, colorCode, ((FormattingCodeHolder) style.color).getCode());
                } else if (current != null) {
                    apply(builder, colorCode, TextConstants.RESET);
                }

                apply(builder, colorCode, TextConstants.BOLD, style.bold);
                apply(builder, colorCode, TextConstants.ITALIC, style.italic);
                apply(builder, colorCode, TextConstants.UNDERLINE, style.underlined);
                apply(builder, colorCode, TextConstants.STRIKETHROUGH, style.strikethrough);
                apply(builder, colorCode, TextConstants.OBFUSCATED, style.obfuscated);
            } else {
                apply(builder, colorCode, TextConstants.BOLD, current.bold != style.bold);
                apply(builder, colorCode, TextConstants.ITALIC, current.italic != style.italic);
                apply(builder, colorCode, TextConstants.UNDERLINE, current.underlined != style.underlined);
                apply(builder, colorCode, TextConstants.STRIKETHROUGH, current.strikethrough != style.strikethrough);
                apply(builder, colorCode, TextConstants.OBFUSCATED, current.obfuscated != style.obfuscated);
            }
        }

        if (text instanceof LiteralText) {
            builder.append(((LiteralText) text).getContent());
        } else if (text instanceof SelectorText) {
            builder.append(((SelectorText) text).getSelector().toPlain());
        } else if (text instanceof TranslatableText) {
            TranslatableText text0 = (TranslatableText) text;

            Translation translation = text0.getTranslation();
            ImmutableList<Object> args = text0.getArguments();

            Object[] args0 = new Object[args.size()];
            for (int i = 0; i < args0.length; i++) {
                Object object = args.get(i);
                if (object instanceof Text || object instanceof Text.Builder || object instanceof TextRepresentable) {
                    if (object instanceof Text) {
                        // Ignore
                    } else if (object instanceof Text.Builder) {
                        object = ((Text.Builder) object).build();
                    } else {
                        object = ((TextRepresentable) object).toText();
                    }
                    args0[i] = to((Text) object, locale, new StringBuilder(), colorCode).toString();
                } else {
                    args0[i] = object;
                }
            }

            builder.append(translation.get(locale, args0));
        } else if (text instanceof ScoreText) {
            ScoreText text0 = (ScoreText) text;

            Optional<String> override = text0.getOverride();
            if (override.isPresent()) {
                builder.append(override.get());
            } else {
                builder.append(text0.getScore().getScore());
            }
        }

        for (Text child : text.getChildren()) {
            to(child, locale, builder, colorCode, style);
        }

        return builder;
    }

    private static void apply(StringBuilder builder, char code, char formattingCode) {
        builder.append(code).append(formattingCode);
    }
    
    private static void apply(StringBuilder builder, char code, char formattingCode, boolean state) {
        if (state) {
            apply(builder, code, formattingCode);
        }
    }

    private static ResolvedChatStyle resolve(@Nullable ResolvedChatStyle current, TextFormat format) {
        TextColor color = format.getColor();
        TextStyle style = format.getStyle();
        if (current == null) {
            if (color == TextColors.NONE) {
                color = null;
            }
            return new ResolvedChatStyle(color, style.isBold().orElse(false), style.isItalic().orElse(false),
                    style.hasUnderline().orElse(false), style.hasStrikethrough().orElse(false), style.isObfuscated().orElse(false));
        }
        if (color == TextColors.NONE) {
            color = current.color;
        }
        return new ResolvedChatStyle(color, style.isBold().orElse(current.bold), style.isItalic().orElse(current.italic),
                style.hasUnderline().orElse(current.underlined), style.hasStrikethrough().orElse(current.strikethrough),
                style.isObfuscated().orElse(current.obfuscated));
    }

    private static class ResolvedChatStyle {

        @Nullable public final TextColor color;
        public final boolean bold;
        public final boolean italic;
        public final boolean underlined;
        public final boolean strikethrough;
        public final boolean obfuscated;

        public ResolvedChatStyle(@Nullable TextColor color, boolean bold, boolean italic,
                boolean underlined, boolean strikethrough, boolean obfuscated) {
            this.color = color;
            this.bold = bold;
            this.italic = italic;
            this.underlined = underlined;
            this.strikethrough = strikethrough;
            this.obfuscated = obfuscated;
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
