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
@file:JvmName("TextFactory")
@file:Suppress("NOTHING_TO_INLINE", "FunctionName", "UNUSED_PARAMETER")

package org.lanternpowered.api.text

import org.lanternpowered.api.text.serializer.TextSerializers
import org.lanternpowered.api.text.translation.Translatable
import org.lanternpowered.api.text.translation.Translation
import org.spongepowered.api.command.CommandCause
import org.spongepowered.api.text.action.ClickAction

typealias LiteralText = org.spongepowered.api.text.LiteralText
typealias LiteralTextBuilder = org.spongepowered.api.text.LiteralText.Builder
typealias ScoreText = org.spongepowered.api.text.ScoreText
typealias SelectorText = org.spongepowered.api.text.SelectorText
typealias Text = org.spongepowered.api.text.Text
typealias TextBuilder = org.spongepowered.api.text.Text.Builder
typealias TextElement = org.spongepowered.api.text.TextElement
typealias TextRepresentable = org.spongepowered.api.text.TextRepresentable
typealias TranslatableText = org.spongepowered.api.text.TranslatableText
typealias TranslatableTextBuilder = org.spongepowered.api.text.TranslatableText.Builder

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
inline fun LiteralText(content: String): LiteralText = Text.of(content)

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

/**
 * Applies a [ClickAction.ExecuteCallback] as click action.
 */
fun TextBuilder.onClick(callback: (CommandCause) -> Unit): TextBuilder = onClick(ClickAction.ExecuteCallback.builder().callback(callback).build())
