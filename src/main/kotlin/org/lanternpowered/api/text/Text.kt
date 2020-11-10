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

import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.locale.Locale
import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.api.text.format.TextDecoration
import org.lanternpowered.api.text.serializer.PlainTextSerializer
import org.spongepowered.api.adventure.SpongeComponents
import org.spongepowered.api.command.CommandCause

typealias LiteralText = net.kyori.adventure.text.TextComponent
typealias LiteralTextBuilder = net.kyori.adventure.text.TextComponent.Builder
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
        Text.empty()

/**
 * Creates a new [LiteralText].
 */
inline fun textOf(text: String): LiteralText =
        Text.text(text)

/**
 * Creates a new [LiteralTextBuilder].
 */
fun literalTextBuilderOf(): LiteralTextBuilder = Text.text()

/**
 * Creates a new [LiteralTextBuilder] with the given content.
 */
fun literalTextBuilderOf(content: String): LiteralTextBuilder = literalTextBuilderOf().content(content);

/**
 * Creates a new [Text].
 */
inline fun textOf(vararg text: TextRepresentable): Text =
        Text.text().append(text.asList()).build()

/**
 * Creates a new [TranslatableText].
 */
inline fun translatableTextOf(key: String): TranslatableText =
        Text.translatable(key)

/**
 * Creates a new [TranslatableText].
 */
fun translatableTextOf(key: String, vararg args: TextRepresentable): TranslatableText =
        Text.translatable(key, args.map { it.toText() })

/**
 * Creates a new [TranslatableText].
 */
fun translatableTextOf(key: String, vararg args: String): TranslatableText =
        Text.translatable(key, args.map { it.toText() })

/**
 * Creates a new [TranslatableText].
 */
fun translatableTextOf(key: String, args: Iterable<TextRepresentable>): TranslatableText =
        Text.translatable(key, args.map { it.toText() })

/**
 * Gets this [String] as a [Text] representation.
 */
inline fun String.toText(): LiteralText = Text.text(this)

/**
 * Gets this [TextRepresentable] as a [Text] representation.
 */
inline fun TextRepresentable.toText(): Text = this.asComponent()

/**
 * Gets this [ItemStack] as a [Text] representation.
 */
inline fun ItemStack.toText(): Text = (this as TextRepresentable).toText()

/**
 * Gets whether this [Text] is not empty.
 */
inline fun Text.isNotEmpty(): Boolean = !this.isEmpty()

/**
 * Gets whether this [Text] is empty.
 */
fun Text.isEmpty(): Boolean =
        if (this is LiteralText) this.content().isEmpty() && this.children().all { child -> child.isEmpty() } else false

fun Text.italic(): Text = this.decorate(TextDecoration.ITALIC)
fun Text.bold(): Text = this.decorate(TextDecoration.BOLD)
fun Text.obfuscated(): Text = this.decorate(TextDecoration.OBFUSCATED)
fun Text.strikethrough(): Text = this.decorate(TextDecoration.STRIKETHROUGH)
fun Text.underlined(): Text = this.decorate(TextDecoration.UNDERLINED)

fun TextRepresentable.color(color: TextColor): Text = this.toText().color(color)
fun TextRepresentable.italic(): Text = this.toText().italic()
fun TextRepresentable.bold(): Text = this.toText().bold()
fun TextRepresentable.obfuscated(): Text = this.toText().obfuscated()
fun TextRepresentable.strikethrough(): Text = this.toText().strikethrough()
fun TextRepresentable.underlined(): Text = this.toText().underlined()

/**
 * Applies a callback as click action.
 */
fun Text.onClick(callback: (CommandCause) -> Unit): Text = this.clickEvent(SpongeComponents.executeCallback(callback))

/**
 * Appends a text component to this text component.
 */
inline operator fun Text.plus(other: Text): Text = this.append(other)

/**
 * Appends literal text to this text component.
 */
inline operator fun Text.plus(other: String): Text = this.append(other.toText())

fun <B : TextBuilder<*, B>> B.appendNewline(): B =
        this.append(Text.newline())
