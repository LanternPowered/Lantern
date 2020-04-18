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
@file:JvmName("TextFormatFactory")
@file:Suppress("UNUSED_PARAMETER", "NOTHING_TO_INLINE", "FunctionName")

package org.lanternpowered.api.text.format

import java.util.function.Supplier

typealias TextColor = org.spongepowered.api.text.format.TextColor
typealias TextColors = org.spongepowered.api.text.format.TextColors
typealias TextFormat = org.spongepowered.api.text.format.TextFormat
typealias TextStyle = org.spongepowered.api.text.format.TextStyle
typealias TextStyleType = org.spongepowered.api.text.format.TextStyle.Type
typealias TextStyles = org.spongepowered.api.text.format.TextStyles

@JvmName("mergeWith")
inline operator fun TextFormat.plus(that: TextFormat): TextFormat = merge(that)

@JvmName("withStyle")
inline operator fun TextFormat.plus(that: TextStyle): TextFormat = style(style.and(that))

@JvmName("withStyle")
inline operator fun TextFormat.plus(that: Supplier<out TextStyle>): TextFormat = style(style.and(that.get()))

@JvmName("withColor")
inline operator fun TextFormat.plus(that: TextColor): TextFormat = color(that)

@JvmName("withColor")
inline operator fun TextFormat.plus(that: Supplier<out TextColor>): TextFormat = color(that.get())

inline operator fun TextStyle.plus(that: TextStyle): TextStyle = and(that)
inline operator fun TextStyle.plus(that: Supplier<out TextStyle>): TextStyle = and(that.get())
inline operator fun Supplier<out TextStyle>.plus(that: TextStyle): TextStyle = get().and(that)
inline operator fun Supplier<out TextStyle>.plus(that: Supplier<out TextStyle>): TextStyle = get().and(that.get())

inline operator fun TextStyle.minus(that: TextStyle): TextStyle = andNot(that)
inline operator fun TextStyle.minus(that: Supplier<out TextStyle>): TextStyle = andNot(that.get())
inline operator fun Supplier<out TextStyle>.minus(that: TextStyle): TextStyle = get().andNot(that)
inline operator fun Supplier<out TextStyle>.minus(that: Supplier<out TextStyle>): TextStyle = get().andNot(that.get())

inline operator fun TextStyle.contains(that: TextStyle): Boolean = contains(that)
inline operator fun TextStyle.contains(that: Supplier<out TextStyle>): Boolean = contains(that.get())

inline operator fun TextStyle.unaryMinus(): TextStyle = negate()
