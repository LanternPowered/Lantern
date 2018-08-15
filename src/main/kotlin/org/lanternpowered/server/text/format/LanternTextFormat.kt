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
package org.lanternpowered.server.text.format

import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.text.TextBuilder
import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.api.text.format.TextColors
import org.lanternpowered.api.text.format.TextFormat
import org.lanternpowered.api.text.format.TextStyle
import org.lanternpowered.api.text.format.TextStyles
import org.lanternpowered.server.text.serializer.TextFormatConfigSerializer

data class LanternTextFormat(
        private val color: TextColor,
        private val style: TextStyle
) : TextFormat {

    override fun isEmpty() = this.color == TextColors.NONE && this.style.isEmpty

    override fun getColor() = this.color
    override fun getStyle() = this.style

    override fun color(color: TextColor) = LanternTextFormat(color, this.style)
    override fun style(style: TextStyle) = LanternTextFormat(this.color, style)

    override fun merge(format: TextFormat): TextFormat {
        var color = format.color
        // If the given format's color is NONE use this ones
        if (color == TextColors.NONE) {
            color = this.color
        // If the given format's color is RESET use NONE
        } else if (color == TextColors.RESET) {
            color = TextColors.NONE
        }
        return LanternTextFormat(color, this.style.and(format.style))
    }

    override fun applyTo(builder: TextBuilder) { builder.format(this) }

    companion object {

        init {
            TypeSerializers.getDefaultSerializers().registerType(typeTokenOf<TextFormat>(), TextFormatConfigSerializer())
        }

        /**
         * The no formatting format. This is lazy loaded to prevent issues with
         * static field initialization, mostly during tests due to such early
         * class referencing occurring.
         */
        val EMPTY: LanternTextFormat by lazy { LanternTextFormat(TextColors.NONE, TextStyles.NONE) }
    }
}
