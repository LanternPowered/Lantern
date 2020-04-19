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
package org.lanternpowered.server.text.format

import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers
import org.lanternpowered.api.text.TextBuilder
import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.api.text.format.TextColors
import org.lanternpowered.api.text.format.TextFormat
import org.lanternpowered.api.text.format.TextStyle
import org.lanternpowered.api.text.format.TextStyles
import org.lanternpowered.api.util.type.typeTokenOf
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
