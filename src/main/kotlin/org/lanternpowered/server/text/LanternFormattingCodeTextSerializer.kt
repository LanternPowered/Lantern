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

import org.lanternpowered.api.locale.Locale
import org.lanternpowered.api.text.serializer.FormattingCodeTextSerializer
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.text.translation.TranslationContext
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.text.LiteralText
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyle
import org.spongepowered.api.text.format.TextStyles

/**
 * TODO: Separate the usage of [TextColors.RESET] and [TextStyles.RESET].
 */
open class LanternFormattingCodeTextSerializer(key: CatalogKey, private val formattingCode: Char) : DefaultCatalogType(key), FormattingCodeTextSerializer {

    companion object {

        private fun applyStyle(builder: Text.Builder, format: Any): Boolean {
            return when (format) {
                is TextStyle -> {
                    builder.style(format)
                    false
                }
                TextColors.RESET.get() -> true
                else -> {
                    if (builder.color == TextColors.NONE.get()) {
                        builder.color(format as TextColor)
                    }
                    true
                }
            }
        }

        fun replace(text: String, from: Char, to: Char): String {
            var pos = text.indexOf(from)
            val last = text.length - 1
            if (pos == -1 || pos == last) {
                return text
            }
            val result = text.toCharArray()
            while (pos < last) {
                if (result[pos] == from && LanternFormattingCodes.contains(result[pos + 1])) {
                    result[pos] = to
                }
                pos++
            }
            return String(result)
        }

        @JvmOverloads
        fun strip(text: String, code: Char, all: Boolean = false): String {
            var next = text.indexOf(code)
            val last = text.length - 1
            if (next == -1 || next == last) {
                return text
            }
            val result = StringBuilder(text.length)
            var pos = 0
            do {
                if (pos != next) {
                    result.append(text, pos, next)
                }
                pos = next
                when {
                    LanternFormattingCodes.contains(text[next + 1]) -> {
                        next += 2
                        pos = next // Skip formatting
                    }
                    all -> {
                        next += 1
                        pos = next // Skip code only
                    }
                    else -> {
                        next++
                    }
                }
                next = text.indexOf(code, next)
            } while (next != -1 && next < last)
            return result.append(text, pos, text.length).toString()
        }
    }

    override fun getCharacter(): Char = this.formattingCode

    override fun stripCodes(text: String): String = strip(text, this.formattingCode)
    override fun replaceCodes(text: String, to: Char): String = replace(text, this.formattingCode, to)

    override fun serialize(text: Text): String = serialize(text, TranslationContext.current().locale)
    override fun serialize(text: Text, locale: Locale): String = LegacyTexts.toLegacy(locale, text, this.formattingCode)

    override fun deserialize(input: String): Text = deserializeUnchecked(input)

    override fun deserializeUnchecked(input: String): Text {
        var next = input.lastIndexOf(this.formattingCode, input.length - 2)
        if (next == -1)
            return Text.of(input)
        val parts = mutableListOf<Text>()
        var current: LiteralText.Builder? = null
        var reset = false
        var pos = input.length
        do {
            val format = LanternFormattingCodes.get(input[next + 1])
            if (format != null) {
                val from = next + 2
                if (from != pos) {
                    if (current != null) {
                        if (reset) {
                            parts.add(current.build())
                            reset = false
                            current = Text.builder("")
                        } else {
                            current = Text.builder("").append(current.build())
                        }
                    } else {
                        current = Text.builder("")
                    }
                    current.content(input.substring(from, pos))
                } else if (current == null) {
                    current = Text.builder("")
                }
                reset = reset or applyStyle(current!!, format)
                pos = next
            }
            next = input.lastIndexOf(formattingCode, next - 1)
        } while (next != -1)
        if (current != null) {
            parts.add(current.build())
        }
        parts.reverse()
        return Text.builder(if (pos > 0) input.substring(0, pos) else "").append(parts).build()
    }
}
