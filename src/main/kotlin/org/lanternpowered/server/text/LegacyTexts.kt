/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.text

import org.spongepowered.api.text.LiteralText
import org.spongepowered.api.text.ScoreText
import org.spongepowered.api.text.SelectorText
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextRepresentable
import org.spongepowered.api.text.TranslatableText
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import java.util.Locale

object LegacyTexts {

    fun toLegacy(locale: Locale, text: Text, legacyChar: Char): String =
            toLegacy(StringBuilder(), locale, text, legacyChar, Style(), Style()).toString()

    private fun toLegacy(builder: StringBuilder, locale: Locale, text: Text, formattingCode: Char, baseStyle: Style, appliedStyle: Style): StringBuilder {
        @Suppress("NAME_SHADOWING")
        var baseStyle = baseStyle
        if (formattingCode.toInt() != 0) {
            val format = text.format
            val color = format.color
            val style = format.style

            // Create a new style object
            val newStyle = baseStyle.copyTo(Style())
            baseStyle = newStyle
            if (color != TextColors.NONE.get()) {
                newStyle.color = if (color == TextColors.RESET.get()) null else color
            }
            style.hasBold().ifPresent { value -> newStyle.bold = value }
            style.hasItalic().ifPresent { value -> newStyle.italic = value }
            style.hasObfuscated().ifPresent { value -> newStyle.obfuscated = value }
            style.hasUnderline().ifPresent { value -> newStyle.underlined = value }
            style.hasStrikethrough().ifPresent { value -> newStyle.strikethrough = value }
            if (appliedStyle.color != null && newStyle.color == null ||
                    appliedStyle.bold && !newStyle.bold ||
                    appliedStyle.italic && !newStyle.italic ||
                    appliedStyle.obfuscated && !newStyle.obfuscated ||
                    appliedStyle.underlined && !newStyle.underlined ||
                    appliedStyle.strikethrough && !newStyle.strikethrough) {
                builder.append(formattingCode).append(LanternFormattingCodes.RESET)
                if (newStyle.color != null)
                    builder.append(formattingCode).append(LanternFormattingCodes.getCode(newStyle.color!!))
                if (newStyle.bold)
                    builder.append(formattingCode).append(LanternFormattingCodes.BOLD)
                if (newStyle.italic)
                    builder.append(formattingCode).append(LanternFormattingCodes.ITALIC)
                if (newStyle.obfuscated)
                    builder.append(formattingCode).append(LanternFormattingCodes.OBFUSCATED)
                if (newStyle.underlined)
                    builder.append(formattingCode).append(LanternFormattingCodes.UNDERLINE)
                if (newStyle.strikethrough)
                    builder.append(formattingCode).append(LanternFormattingCodes.STRIKETHROUGH)
            } else {
                if (appliedStyle.color !== newStyle.color)
                    builder.append(formattingCode).append(LanternFormattingCodes.getCode(newStyle.color!!))
                if (appliedStyle.bold != newStyle.bold)
                    builder.append(formattingCode).append(LanternFormattingCodes.BOLD)
                if (appliedStyle.italic != newStyle.italic)
                    builder.append(formattingCode).append(LanternFormattingCodes.ITALIC)
                if (appliedStyle.obfuscated != newStyle.obfuscated)
                    builder.append(formattingCode).append(LanternFormattingCodes.OBFUSCATED)
                if (appliedStyle.underlined != newStyle.underlined)
                    builder.append(formattingCode).append(LanternFormattingCodes.UNDERLINE)
                if (appliedStyle.strikethrough != newStyle.strikethrough)
                    builder.append(formattingCode).append(LanternFormattingCodes.STRIKETHROUGH)
            }
            newStyle.copyTo(appliedStyle)
        }
        if (text is LiteralText) {
            builder.append(text.content)
        } else if (text is SelectorText) {
            builder.append(text.selector.toPlain())
        } else if (text is TranslatableText) {
            val translation = text.translation
            val args = text.arguments
            val args0 = arrayOfNulls<Any>(args.size)
            for (i in args0.indices) {
                var arg = args[i]
                if (arg is TextRepresentable) {
                    arg = when (arg) {
                        is Text.Builder -> arg.build()
                        else -> arg.toText()
                    }
                    args0[i] = toLegacy(StringBuilder(), locale, arg as Text, formattingCode, baseStyle, appliedStyle).toString()
                } else {
                    args0[i] = arg
                }
            }
            builder.append(translation.get(locale, args0))
        } else if (text is ScoreText) {
            val text0 = text
            val override = text0.override
            if (override.isPresent) {
                builder.append(override.get())
            } else {
                builder.append(text0.score.score)
            }
        }
        for (child in text.children) {
            toLegacy(builder, locale, child, formattingCode, baseStyle, appliedStyle)
        }
        return builder
    }

    private class Style {
        var color: TextColor? = null
        var bold = false
        var italic = false
        var underlined = false
        var strikethrough = false
        var obfuscated = false

        /**
         * Creates a copy of this [Style].
         *
         * @return The copy
         */
        fun copyTo(to: Style): Style {
            to.color = color
            to.bold = bold
            to.italic = italic
            to.underlined = underlined
            to.strikethrough = strikethrough
            to.obfuscated = obfuscated
            return to
        }
    }
}