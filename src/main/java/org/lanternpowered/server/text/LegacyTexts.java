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

import com.google.common.collect.ImmutableList;
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
import org.spongepowered.api.text.translation.Translation;

import java.util.Locale;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class LegacyTexts {

    private static final class Style {

        @Nullable TextColor color;
        boolean bold;
        boolean italic;
        boolean underlined;
        boolean strikethrough;
        boolean obfuscated;

        /**
         * Creates a copy of this {@link Style}.
         *
         * @return The copy
         */
        Style copyTo(Style to) {
            to.color = this.color;
            to.bold = this.bold;
            to.italic = this.italic;
            to.underlined = this.underlined;
            to.strikethrough = this.strikethrough;
            to.obfuscated = this.obfuscated;
            return to;
        }
    }

    public static String toLegacy(Locale locale, Text text, char legacyChar) {
        return toLegacy(new StringBuilder(), locale, text, legacyChar, new Style(), new Style()).toString();
    }

    private static StringBuilder toLegacy(StringBuilder builder, Locale locale, Text text, char legacyChar, Style base, Style applied) {
        if (legacyChar != 0) {
            final TextFormat format = text.getFormat();

            final TextColor color = format.getColor();
            final TextStyle style = format.getStyle();

            // Create a new style object
            final Style newStyle = base.copyTo(new Style());
            base = newStyle;
            if (color != TextColors.NONE) {
                newStyle.color = color == TextColors.RESET ? null : color;
            }
            style.isBold().ifPresent(value -> newStyle.bold = value);
            style.isItalic().ifPresent(value -> newStyle.italic = value);
            style.isObfuscated().ifPresent(value -> newStyle.obfuscated = value);
            style.hasUnderline().ifPresent(value -> newStyle.underlined = value);
            style.hasStrikethrough().ifPresent(value -> newStyle.strikethrough = value);

            if ((applied.color != null && newStyle.color == null) ||
                    (applied.bold && !newStyle.bold) ||
                    (applied.italic && !newStyle.italic) ||
                    (applied.obfuscated && !newStyle.obfuscated) ||
                    (applied.underlined && !newStyle.underlined) ||
                    (applied.strikethrough && !newStyle.strikethrough)) {
                builder.append(legacyChar).append(TextConstants.RESET);

                if (newStyle.color != null) {
                    builder.append(legacyChar).append(((FormattingCodeHolder) newStyle.color).getCode());
                }
                if (newStyle.bold) {
                    builder.append(legacyChar).append(TextConstants.BOLD);
                }
                if (newStyle.italic) {
                    builder.append(legacyChar).append(TextConstants.ITALIC);
                }
                if (newStyle.obfuscated) {
                    builder.append(legacyChar).append(TextConstants.OBFUSCATED);
                }
                if (newStyle.underlined) {
                    builder.append(legacyChar).append(TextConstants.UNDERLINE);
                }
                if (newStyle.strikethrough) {
                    builder.append(legacyChar).append(TextConstants.STRIKETHROUGH);
                }
            } else {
                if (applied.color != newStyle.color) {
                    builder.append(legacyChar).append(((FormattingCodeHolder) newStyle.color).getCode());
                }
                if (applied.bold != newStyle.bold) {
                    builder.append(legacyChar).append(TextConstants.BOLD);
                }
                if (applied.italic != newStyle.italic) {
                    builder.append(legacyChar).append(TextConstants.ITALIC);
                }
                if (applied.obfuscated != newStyle.obfuscated) {
                    builder.append(legacyChar).append(TextConstants.OBFUSCATED);
                }
                if (applied.underlined != newStyle.underlined) {
                    builder.append(legacyChar).append(TextConstants.UNDERLINE);
                }
                if (applied.strikethrough != newStyle.strikethrough) {
                    builder.append(legacyChar).append(TextConstants.STRIKETHROUGH);
                }
            }

            newStyle.copyTo(applied);
        }

        if (text instanceof LiteralText) {
            builder.append(((LiteralText) text).getContent());
        } else if (text instanceof SelectorText) {
            builder.append(((SelectorText) text).getSelector().toPlain());
        } else if (text instanceof TranslatableText) {
            final TranslatableText text0 = (TranslatableText) text;

            final Translation translation = text0.getTranslation();
            final ImmutableList<Object> args = text0.getArguments();

            final Object[] args0 = new Object[args.size()];
            for (int i = 0; i < args0.length; i++) {
                Object object = args.get(i);
                if (object instanceof TextRepresentable) {
                    if (object instanceof Text) {
                        // Ignore
                    } else if (object instanceof Text.Builder) {
                        object = ((Text.Builder) object).build();
                    } else {
                        object = ((TextRepresentable) object).toText();
                    }
                    args0[i] = toLegacy(new StringBuilder(), locale, (Text) object, legacyChar, base, applied).toString();
                } else {
                    args0[i] = object;
                }
            }

            builder.append(translation.get(locale, args0));
        } else if (text instanceof ScoreText) {
            final ScoreText text0 = (ScoreText) text;

            final Optional<String> override = text0.getOverride();
            if (override.isPresent()) {
                builder.append(override.get());
            } else {
                builder.append(text0.getScore().getScore());
            }
        }

        for (Text child : text.getChildren()) {
            toLegacy(builder, locale, child, legacyChar, base, applied);
        }

        return builder;
    }
}
