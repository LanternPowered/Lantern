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

import org.lanternpowered.api.locale.Locale
import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.api.text.format.TextDecoration
import org.lanternpowered.api.text.serializer.PlainTextSerializer
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.adventure.SpongeComponents
import org.spongepowered.api.command.CommandCause

typealias LiteralText = net.kyori.adventure.text.TextComponent
typealias ScoreText = net.kyori.adventure.text.ScoreComponent
typealias SelectorText = net.kyori.adventure.text.SelectorComponent
typealias Text = net.kyori.adventure.text.Component
typealias TextBuilder<T, B> = net.kyori.adventure.text.ComponentBuilder<T, B>
typealias TextRepresentable = net.kyori.adventure.text.ComponentLike
typealias TranslatableText = net.kyori.adventure.text.TranslatableComponent

/**
 * Converts the [Text] to a plain string format.
 */
fun Text.toPlain(locale: Locale): String = PlainTextSerializer.serialize(this, locale)

/**
 * Gets an empty [Text].
 */
inline fun emptyText(): LiteralText = LiteralText.empty()

/**
 * Creates a new [LiteralText].
 */
inline fun textOf(text: String): LiteralText = LiteralText.of(text)

/**
 * Gets this [String] as a [Text] representation.
 */
inline fun String.text(): LiteralText = LiteralText.of(this)

/**
 * Gets this [TextRepresentable] as a [Text] representation.
 */
inline fun TextRepresentable.text(): Text = asComponent()

fun String.color(color: TextColor): LiteralText = text().color(color)
fun String.italic(): LiteralText = text().italic()
fun String.bold(): LiteralText = text().bold()
fun String.obfuscated(): LiteralText = text().obfuscated()
fun String.strikethrough(): LiteralText = text().strikethrough()
fun String.underlined(): LiteralText = text().underlined()

fun <T : Text> T.italic(): T = decoration(TextDecoration.ITALIC, true).uncheckedCast()
fun <T : Text> T.bold(): T = decoration(TextDecoration.BOLD, true).uncheckedCast()
fun <T : Text> T.obfuscated(): T = decoration(TextDecoration.OBFUSCATED, true).uncheckedCast()
fun <T : Text> T.strikethrough(): T = decoration(TextDecoration.STRIKETHROUGH, true).uncheckedCast()
fun <T : Text> T.underlined(): T = decoration(TextDecoration.UNDERLINED, true).uncheckedCast()

fun TextRepresentable.color(color: TextColor): Text = text().color(color)
fun TextRepresentable.italic(): Text = text().italic()
fun TextRepresentable.bold(): Text = text().bold()
fun TextRepresentable.obfuscated(): Text = text().obfuscated()
fun TextRepresentable.strikethrough(): Text = text().strikethrough()
fun TextRepresentable.underlined(): Text = text().underlined()

/**
 * Applies a callback as click action.
 */
fun Text.onClick(callback: (CommandCause) -> Unit): Text = clickEvent(SpongeComponents.executeCallback(callback))

/**
 * Appends a text component to this text component.
 */
inline operator fun Text.plus(other: Text): Text = append(other)

/**
 * Appends literal text to this text component.
 */
inline operator fun Text.plus(other: String): Text = append(other.text())

/**
 * Constructs a new [TranslatableText] from the given [Translation] and arguments.
 *
 * @param translation The translation
 * @param args The translation arguments
 * @return The constructed text
 *//*
inline fun translatableTextOf(translation: Translation, vararg args: Any): TranslatableText =
        Text.of(translation, *args)

/**
 * Constructs a new [TranslatableText] from the given [Translation], arguments and builder function.
 *
 * @param translation The translation
 * @param args The translation arguments
 * @param fn The builder function
 * @return The constructed text
 */
inline fun translatableTextOf(translation: Translation, vararg args: Any, fn: TranslatableTextBuilder.() -> Unit): TranslatableText =
        Text.builder(translation, *args).apply(fn).build()

/**
 * Constructs a new [TranslatableText] from the given [Translatable].
 *
 * @param translatable The translatable
 * @return The constructed text
 */
inline fun translatableTextOf(translatable: Translatable, vararg args: Any): TranslatableText =
        Text.of(translatable, *args)
*/