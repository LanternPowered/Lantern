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
package org.lanternpowered.server.util

import org.lanternpowered.api.locale.Locale
import org.lanternpowered.api.locale.Locales
import java.text.FieldPosition
import java.text.Format
import java.text.MessageFormat
import java.text.ParsePosition
import java.util.Formatter

private val formatter = Formatter(Locales.DEFAULT)
private val formatRegex = "%(\\d+\\$)?([-#+ 0,(<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])".toRegex()

object StringFormat {

    /**
     * Creates a [MessageFormat] from a [String.format] format and the given locale.
     */
    fun toMessageFormat(format: String, locale: Locale): MessageFormat {
        val messageFormatPatternBuilder = StringBuilder()
        val arguments = mutableListOf<StringFormatArgument>()
        var argIndexCounter = 1
        var index = 0
        while (true) {
            val result = formatRegex.find(format, startIndex = index)
            if (result != null) {
                if (index != result.range.first)
                    messageFormatPatternBuilder.append(format, index, result.range.first)
                val indexGroup = result.groups[1]
                val argIndex: Int
                val argFormat: String
                if (indexGroup == null) {
                    argIndex = argIndexCounter++
                    argFormat = result.value
                } else {
                    // -1 to remove trailing $
                    argIndex = format.substring(indexGroup.range.first, indexGroup.range.last - 1).toInt()
                    // remove the argument index from the argument format
                    argFormat = result.value.removeRange(
                            startIndex = indexGroup.range.first - result.range.first,
                            endIndex = indexGroup.range.last - result.range.first)
                }
                arguments += StringFormatArgument(argFormat)
                messageFormatPatternBuilder.append('{').append(argIndex).append('}')
                index = result.range.last
            } else {
                if (index != format.length)
                    messageFormatPatternBuilder.append(format, index, format.length)
                break
            }
        }
        val messageFormat = MessageFormat(messageFormatPatternBuilder.toString(), locale)
        messageFormat.formats = arguments.toTypedArray()
        return messageFormat
    }
}

private class StringFormatArgument(private val format: String) : Format() {

    override fun format(obj: Any?, toAppendTo: StringBuffer, pos: FieldPosition): StringBuffer {
        val args = if (obj is Array<*>) obj else arrayOf(obj)
        val formatted = formatter.format(this.format, obj, *args)
        return toAppendTo.append(formatted)
    }

    override fun parseObject(source: String, pos: ParsePosition): Any = source
}
