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

import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2CharOpenHashMap
import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.api.text.format.TextColors
import org.lanternpowered.api.text.format.TextStyleType
import org.lanternpowered.api.text.format.TextStyles
import java.util.function.Supplier

object LanternFormattingCodes {

    const val LEGACY_CODE = '\u00A7'

    const val RESET = 'r'
    const val OBFUSCATED = 'k'
    const val BOLD = 'l'
    const val STRIKETHROUGH = 'm'
    const val UNDERLINE = 'n'
    const val ITALIC = 'o'

    private val mapping by lazy { loadMappings() }

    fun getCode(any: Any) = this.mapping.toCode.getChar(any)
    fun get(code: Char): Any? = this.mapping.fromCode.get(code)
    fun contains(code: Char): Boolean = this.mapping.fromCode.containsKey(code.toLowerCase())

    private fun loadMappings() = Mappings().apply {
        fun addEntry(code: Char, any: Any) {
            this.toCode[any] = code
            this.fromCode[code] = any
        }

        fun add(code: Char, supplier: Supplier<out TextColor>) = addEntry(code, supplier.get())
        fun add(code: Char, supplier: Supplier<out TextStyleType>) = addEntry(code, supplier.get())

        add('0', TextColors.BLACK)
        add('1', TextColors.DARK_BLUE)
        add('2', TextColors.DARK_GREEN)
        add('3', TextColors.DARK_AQUA)
        add('4', TextColors.DARK_RED)
        add('5', TextColors.DARK_PURPLE)
        add('6', TextColors.GOLD)
        add('7', TextColors.GRAY)
        add('8', TextColors.DARK_GRAY)
        add('9', TextColors.BLUE)
        add('a', TextColors.GREEN)
        add('b', TextColors.AQUA)
        add('c', TextColors.RED)
        add('d', TextColors.LIGHT_PURPLE)
        add('e', TextColors.YELLOW)
        add('f', TextColors.WHITE)

        add(RESET, TextStyles.RESET)
        add(OBFUSCATED, TextStyles.OBFUSCATED)
        add(BOLD, TextStyles.BOLD)
        add(STRIKETHROUGH, TextStyles.STRIKETHROUGH)
        add(UNDERLINE, TextStyles.UNDERLINE)
        add(ITALIC, TextStyles.ITALIC)
    }

    private class Mappings {
        val toCode = Object2CharOpenHashMap<Any>()
        val fromCode = Char2ObjectOpenHashMap<Any>()

        init {
            this.toCode.defaultReturnValue(0.toChar())
        }
    }
}
