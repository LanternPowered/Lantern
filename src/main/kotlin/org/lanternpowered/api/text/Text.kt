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
import org.spongepowered.api.adventure.SpongeComponents
import org.spongepowered.api.command.CommandCause

typealias LiteralText = net.kyori.adventure.text.TextComponent
typealias ScoreText = net.kyori.adventure.text.ScoreComponent
typealias SelectorText = net.kyori.adventure.text.SelectorComponent
typealias KeybindText = net.kyori.adventure.text.KeybindComponent
typealias BlockDataText = net.kyori.adventure.text.BlockNBTComponent
typealias EntityDataText = net.kyori.adventure.text.EntityNBTComponent
typealias StorageDataText = net.kyori.adventure.text.StorageNBTComponent
typealias DataText<C, B> = net.kyori.adventure.text.NBTComponent<C, B>
typealias Text = net.kyori.adventure.text.Component
typealias TextBuilder<T, B> = net.kyori.adventure.text.ComponentBuilder<T, B>
typealias TextRepresentable = net.kyori.adventure.text.ComponentLike
typealias TranslatableText = net.kyori.adventure.text.TranslatableComponent

/**
 * Converts the [Text] to a plain string format.
 */
fun Text.toPlain(locale: Locale): String = PlainTextSerializer.serialize(this, locale)

/**
 * Converts the [Text] to a plain string format.
 */
fun Text.toPlain(): String = PlainTextSerializer.serialize(this)

/**
 * Gets an empty [Text].
 */
inline fun emptyText(): LiteralText =
        LiteralText.empty()

/**
 * Creates a new [LiteralText].
 */
inline fun textOf(text: String): LiteralText =
        LiteralText.of(text)

/**
 * Creates a new [TranslatableText].
 */
inline fun translatableTextOf(key: String): TranslatableText =
        TranslatableText.of(key)

/**
 * Creates a new [TranslatableText].
 */
fun translatableTextOf(key: String, vararg args: TextRepresentable): TranslatableText =
        TranslatableText.of(key, args.map { it.toText() })

/**
 * Creates a new [TranslatableText].
 */
fun translatableTextOf(key: String, args: Iterable<TextRepresentable>): TranslatableText =
        TranslatableText.of(key, args.map { it.toText() })

/**
 * Gets this [String] as a [Text] representation.
 */
inline fun String.toText(): LiteralText = LiteralText.of(this)

/**
 * Gets this [TextRepresentable] as a [Text] representation.
 */
inline fun TextRepresentable.toText(): Text = asComponent()

fun Text.italic(): Text = decorate(TextDecoration.ITALIC)
fun Text.bold(): Text = decorate(TextDecoration.BOLD)
fun Text.obfuscated(): Text = decorate(TextDecoration.OBFUSCATED)
fun Text.strikethrough(): Text = decorate(TextDecoration.STRIKETHROUGH)
fun Text.underlined(): Text = decorate(TextDecoration.UNDERLINED)

fun TextRepresentable.color(color: TextColor): Text = toText().color(color)
fun TextRepresentable.italic(): Text = toText().italic()
fun TextRepresentable.bold(): Text = toText().bold()
fun TextRepresentable.obfuscated(): Text = toText().obfuscated()
fun TextRepresentable.strikethrough(): Text = toText().strikethrough()
fun TextRepresentable.underlined(): Text = toText().underlined()

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
inline operator fun Text.plus(other: String): Text = append(other.toText())
