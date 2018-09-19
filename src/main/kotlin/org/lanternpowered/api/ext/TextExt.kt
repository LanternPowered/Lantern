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
@file:Suppress("DEPRECATION", "NOTHING_TO_INLINE")

package org.lanternpowered.api.ext

import org.lanternpowered.api.Lantern
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextBuilder
import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.api.text.format.TextFormat
import org.lanternpowered.api.text.format.TextStyle
import org.lanternpowered.api.text.serializer.TextSerializers
import org.lanternpowered.api.text.translation.Translation
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.TextActions

/**
 * Converts the [Text] into the legacy [String] format.
 */
fun Text.toLegacy(): String = TextSerializers.LEGACY_FORMATTING_CODE.serialize(this)

/**
 * Converts the legacy [String] into a [Text] object.
 */
fun String.fromLegacy(): Text = TextSerializers.LEGACY_FORMATTING_CODE.deserialize(this)

/**
 * Converts the [String] into a [Text] object.
 */
fun String.toText(): Text = Text(this)

/**
 * Converts the [String] into a [Text] object.
 */
fun String.toText(fn: TextBuilder.() -> Unit): Text = Text(this, fn)

/**
 * Applies a [ClickAction.ExecuteCallback] as click action.
 */
fun TextBuilder.onClick(fn: (CommandSource) -> Unit): TextBuilder = onClick(TextActions.executeCallback(fn))

inline operator fun TextFormat.plus(that: TextFormat): TextFormat = merge(that)
inline operator fun TextFormat.plus(that: TextStyle): TextFormat = style(that)
inline operator fun TextFormat.plus(that: TextColor): TextFormat = color(that)

inline operator fun TextStyle.plus(that: TextStyle): TextStyle = and(that)
inline operator fun TextStyle.minus(that: TextStyle): TextStyle = andNot(that)
inline operator fun TextStyle.unaryMinus(): TextStyle = negate()

fun translationOf(id: String): Translation = Lantern.translationRegistry[id]
