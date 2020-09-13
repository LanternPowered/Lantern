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
package org.lanternpowered.server.service.pagination

import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.loader.HeaderMode
import org.lanternpowered.api.text.LiteralText
import org.lanternpowered.api.text.LiteralTextBuilder
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.format.TextDecoration
import org.lanternpowered.api.text.format.TextDecorationState
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.text.toPlain
import org.spongepowered.math.GenericMath
import kotlin.math.ceil

/**
 * Pagination calculator for players.
 *
 * @property linesPerPage The amount of lines per page there should be
 */
internal class PaginationCalculator(val linesPerPage: Int) {

    companion object {

        private var NON_UNICODE_CHARS: String
        private val NON_UNICODE_CHAR_WIDTHS: IntArray
        private val UNICODE_CHAR_WIDTHS: ByteArray
        private const val LINE_WIDTH = 320

        init {
            val loader: ConfigurationLoader<CommentedConfigurationNode> = HoconConfigurationLoader.builder()
                    .setURL(PaginationCalculator::class.java.getResource("/internal/font_sizes.json"))
                    .setHeaderMode(HeaderMode.PRESET)
                    .build()

            val node: ConfigurationNode = loader.load()
            NON_UNICODE_CHARS = node.getNode("non-unicode").string!!

            val charWidths = node.getNode("char-widths").childrenList
            NON_UNICODE_CHAR_WIDTHS = IntArray(charWidths.size)
            for (i in NON_UNICODE_CHAR_WIDTHS.indices)
                NON_UNICODE_CHAR_WIDTHS[i] = charWidths[i].int

            val glyphWidths = node.getNode("glyph-widths").childrenList
            UNICODE_CHAR_WIDTHS = ByteArray(glyphWidths.size)
            for (i in UNICODE_CHAR_WIDTHS.indices)
                UNICODE_CHAR_WIDTHS[i] = glyphWidths[i].int.toByte()
        }
    }

    /**
     * Gets the number of lines the specified text flows into.
     *
     * @param text The text to calculate the number of lines for
     * @return The number of lines that this text flows into
     */
    fun getLines(text: Text): Int =
            ceil(this.getWidth(text).toDouble() / LINE_WIDTH).toInt()

    /**
     * Gets the width of a character with the specified code
     * point, accounting for if its text is bold our not.
     *
     * @param codePoint The code point of the character
     * @param bold Whether or not the character is bold or not
     * @return The width of the character at the code point
     */
    private fun getWidth(codePoint: Int, bold: Boolean): Int {
        val nonUnicodeIdx = NON_UNICODE_CHARS.indexOf(codePoint.toChar())
        var width: Int
        if (codePoint == 167) {
            // Color code character, this has no width
            width = 0
        } else if (codePoint == 32) {
            // Space
            width = 4
        } else if (codePoint > 0 && nonUnicodeIdx != -1) {
            width = NON_UNICODE_CHAR_WIDTHS[nonUnicodeIdx]
        } else if (UNICODE_CHAR_WIDTHS[codePoint].toInt() != 0) {
            // from 1.9 & 255 to avoid strange signed int math ruining things.
            // https://bugs.mojang.com/browse/MC-7181
            val temp = UNICODE_CHAR_WIDTHS[codePoint].toInt() and 255
            // Split into high and low nibbles.
            // bit digits
            // 87654321 >>> 4 = 00008765
            val startColumn = temp ushr 4
            // 87654321 & 00001111 = 00004321
            val endColumn = temp and 15
            width = (endColumn + 1) - startColumn
            // Why does this scaling happen?
            // I believe it makes unicode fonts skinnier to better match the character widths of the default Minecraft
            // font however there is a int math vs float math bug in the Minecraft FontRenderer.
            // The float math is adjusted for rendering, they attempt to do the same thing for calculating string widths
            // using integer math, this has potential rounding errors, but we should copy it and use ints as well.
            width = (width / 2) + 1
        } else {
            width = 0
        }
        // If bold, the width gets 1 added
        if (bold && width > 0)
            width++
        return width
    }

    /**
     * Gets the length of a text.
     *
     * @param text The text to get the length of
     * @return The length of the text
     */
    private fun getWidth(text: Text): Int =
            this.getWidth(text, false)

    private fun getWidth(text: Text, bold: Boolean): Int {
        @Suppress("NAME_SHADOWING")
        var bold = bold

        val boldState = text.style().decoration(TextDecoration.BOLD)
        if (boldState != TextDecorationState.NOT_SET)
            bold = boldState == TextDecorationState.TRUE

        val content = if (text is LiteralText) {
            text.content()
        } else {
            text.children(emptyList()).toPlain()
        }

        var length = this.getWidth(content, bold)

        // Get the length of all the children
        for (child in text.children())
            length += this.getWidth(child, bold)

        return length
    }

    private fun getWidth(text: String, bold: Boolean): Int {
        var total = 0
        var newLine = false
        val it = text.codePoints().iterator()
        while (it.hasNext()) {
            val codePoint = it.nextInt()
            if (codePoint == '\n'.toInt()) {
                // If the previous character is a '\n'
                if (newLine) {
                    total += LINE_WIDTH
                } else {
                    total = ceil(total.toDouble() / LINE_WIDTH).toInt() * LINE_WIDTH
                    newLine = true
                }
            } else {
                val width = this.getWidth(codePoint, bold)
                total += width
                newLine = false
            }
        }
        return total
    }

    /**
     * Centers a text within the middle of the chat box.
     *
     * Generally used for titles and footers.
     *
     * To use no heading, just pass in a 0 width text for
     * the first argument.
     *
     * @param text The text to center
     * @param padding A padding character with a width >1
     * @return The centered text, or if too big, the original text
     */
    fun center(text: Text, padding: Text): Text {
        @Suppress("NAME_SHADOWING")
        var text = text
        @Suppress("NAME_SHADOWING")
        var padding = padding

        var inputLength = this.getWidth(text)
        if (inputLength >= LINE_WIDTH)
            return text

        val textWithSpaces = this.addSpaces(LiteralText.space(), text)
        val addSpaces = this.getWidth(textWithSpaces) <= LINE_WIDTH

        var paddingLength = this.getWidth(padding)
        val output = LiteralText.builder()

        // Using 0 width unicode symbols as padding throws us into an unending loop, replace them with the default padding
        if (paddingLength < 1) {
            padding = textOf("=")
            paddingLength = this.getWidth(padding)
        }

        // If we only need padding
        if (inputLength == 0) {
            this.addPadding(padding, output, GenericMath.floor(LINE_WIDTH.toDouble() / paddingLength))
        } else {
            if (addSpaces) {
                text = textWithSpaces
                inputLength = this.getWidth(textWithSpaces)
            }
            val paddingNecessary = LINE_WIDTH - inputLength
            val paddingCount = GenericMath.floor(paddingNecessary / paddingLength.toFloat())
            // Pick a halfway point
            val beforePadding = GenericMath.floor(paddingCount / 2.0)
            // Do not use ceil, this prevents floating point errors.
            val afterPadding = paddingCount - beforePadding
            this.addPadding(padding, output, beforePadding)
            output.append(text)
            this.addPadding(padding, output, afterPadding)
        }
        return output.style(text.style()).build()
    }

    /**
     * Adds spaces to both sides of the specified text.
     *
     *
     * Overrides all color and style with the
     * text's color and style.
     *
     * @param spaces The spaces to use
     * @param text The text to add to
     * @return The text with the added spaces
     */
    private fun addSpaces(spaces: Text, text: Text): Text = LiteralText.builder()
            .append(spaces)
            .append(text)
            .append(spaces)
            .build()

    /**
     * Adds the specified padding text to a piece of text being built
     * up to a certain amount specified by a count.
     *
     * @param padding The padding text to use
     * @param builder The work in progress text to add to
     * @param count The amount of padding to add
     */
    private fun addPadding(padding: Text, builder: LiteralTextBuilder, count: Int) {
        if (count <= 0)
            return
        if (padding is LiteralText && padding.children().isEmpty()) {
            // Can be added as a single text component
            val fullPadding = LiteralText.builder()
                    .content(padding.content().repeat(count))
                    .style(padding.style())
                    .build()
            builder.append(fullPadding)
        } else {
            repeat(count) { builder.append(padding) }
        }
    }
}
