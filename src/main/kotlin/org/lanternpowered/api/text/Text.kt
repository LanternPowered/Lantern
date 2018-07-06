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
@file:JvmName("TextFactory")
@file:Suppress("NOTHING_TO_INLINE", "FunctionName", "UNUSED_PARAMETER")

package org.lanternpowered.api.text

import org.lanternpowered.api.text.translation.Translatable
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.api.util.Unused

typealias LiteralText = org.spongepowered.api.text.LiteralText
typealias LiteralTextBuilder = org.spongepowered.api.text.LiteralText.Builder
typealias ScoreText = org.spongepowered.api.text.ScoreText
typealias SelectorText = org.spongepowered.api.text.SelectorText
typealias Text = org.spongepowered.api.text.Text
typealias TextBuilder = org.spongepowered.api.text.Text.Builder
typealias TextElement = org.spongepowered.api.text.TextElement
typealias TextRepresentable = org.spongepowered.api.text.TextRepresentable
typealias TextTemplate = org.spongepowered.api.text.TextTemplate
typealias TextTemplateArgumentException = org.spongepowered.api.text.TextTemplateArgumentException
typealias TranslatableText = org.spongepowered.api.text.TranslatableText
typealias TranslatableTextBuilder = org.spongepowered.api.text.TranslatableText.Builder

/**
 * Constructs a new [LiteralText] from the given content.
 *
 * @param content The content
 * @return The constructed text
 */
@JvmName("of")
inline fun Text(content: String): LiteralText = Text.of(content)

/**
 * Constructs a new [LiteralText] from the given content and builder function.
 *
 * @param content The content
 * @param fn The builder function
 * @return The constructed text
 */
@JvmName("of")
inline fun Text(content: String, fn: LiteralTextBuilder.() -> Unit): LiteralText =
        Text.builder(content).apply(fn).build()

/**
 * Constructs a new [LiteralText] from the given content.
 *
 * @param content The content
 * @return The constructed text
 */
@JvmName("literalOf")
inline fun LiteralText(content: String, unused: Unused = null): LiteralText = Text.of(content)

/**
 * Constructs a new [LiteralText] from the given content and builder function.
 *
 * @param content The content
 * @param fn The builder function
 * @return The constructed text
 */
@JvmName("literalOf")
inline fun LiteralText(content: String, fn: LiteralTextBuilder.() -> Unit): LiteralText =
        Text.builder(content).apply(fn).build()

/**
 * Constructs a new [TranslatableText] from the given [Translation] and arguments.
 *
 * @param translation The translation
 * @param args The translation arguments
 * @return The constructed text
 */
@JvmName("translatableOf")
inline fun TranslatableText(translation: Translation, vararg args: Any): TranslatableText =
        Text.of(translation, *args)

/**
 * Constructs a new [TranslatableText] from the given [Translation] and arguments.
 *
 * @param translation The translation
 * @param args The translation arguments
 * @return The constructed text
 */
@JvmName("of")
inline fun Text(translation: Translation, vararg args: Any): TranslatableText =
        Text.of(translation, *args)

/**
 * Constructs a new [TranslatableText] from the given [Translation], arguments and builder function.
 *
 * @param translation The translation
 * @param args The translation arguments
 * @param fn The builder function
 * @return The constructed text
 */
@JvmName("translatableOf")
inline fun TranslatableText(translation: Translation, vararg args: Any, fn: TranslatableTextBuilder.() -> Unit): TranslatableText =
        Text.builder(translation, *args).apply(fn).build()

/**
 * Constructs a new [TranslatableText] from the given [Translation], arguments and builder function.
 *
 * @param translation The translation
 * @param args The translation arguments
 * @param fn The builder function
 * @return The constructed text
 */
@JvmName("of")
inline fun Text(translation: Translation, vararg args: Any, fn: TranslatableTextBuilder.() -> Unit): TranslatableText =
        Text.builder(translation, *args).apply(fn).build()

/**
 * Constructs a new [TranslatableText] from the given [Translatable].
 *
 * @param translatable The translatable
 * @return The constructed text
 */
@JvmName("translatableOf")
inline fun TranslatableText(translatable: Translatable, vararg args: Any): TranslatableText =
        Text.of(translatable, *args)

/**
 * Constructs a new [TranslatableText] from the given [Translatable].
 *
 * @param translatable The translatable
 * @return The constructed text
 */
@JvmName("of")
inline fun Text(translatable: Translatable, vararg args: Any): TranslatableText =
        Text.of(translatable, *args)
