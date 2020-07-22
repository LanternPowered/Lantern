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
@file:JvmName("TextFormatFactory")
@file:Suppress("UNUSED_PARAMETER", "NOTHING_TO_INLINE", "FunctionName")

package org.lanternpowered.api.text.format

typealias TextColor = net.kyori.adventure.text.format.TextColor
typealias NamedTextColor = net.kyori.adventure.text.format.NamedTextColor
typealias TextDecoration = net.kyori.adventure.text.format.TextDecoration
typealias TextDecorationState = net.kyori.adventure.text.format.TextDecoration.State
typealias TextStyle = net.kyori.adventure.text.format.Style

fun textStyleOf(color: TextColor): TextStyle = TextStyle.of(color as TextColor?)
fun textStyleOf(color: TextColor, vararg decorations: TextDecoration): TextStyle = TextStyle.of(color as TextColor?, *decorations)
fun textStyleOf(vararg decorations: TextDecoration): TextStyle = TextStyle.of(*decorations)

inline operator fun TextStyle.plus(decoration: TextDecoration): TextStyle = decoration(decoration, true)
inline operator fun TextStyle.plus(color: TextColor): TextStyle = color(color)
inline operator fun TextStyle.plus(style: TextStyle): TextStyle = merge(style)
inline operator fun TextStyle.contains(decoration: TextDecoration): Boolean = hasDecoration(decoration)

inline operator fun TextDecoration.plus(decoration: TextDecoration): TextStyle = TextStyle.of(this, decoration)
inline operator fun TextDecoration.plus(color: TextColor): TextStyle = TextStyle.of(this, color)
inline operator fun TextColor.plus(decoration: TextDecoration): TextStyle = TextStyle.of(decoration, this)
