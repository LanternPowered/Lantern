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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.audience

import net.kyori.adventure.identity.Identity
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextRepresentable

typealias Audience = net.kyori.adventure.audience.Audience
typealias Audiences = org.spongepowered.api.adventure.Audiences
typealias ForwardingAudience = net.kyori.adventure.audience.ForwardingAudience
typealias MessageType = net.kyori.adventure.audience.MessageType

inline fun emptyAudience(): Audience = Audience.empty()

fun audienceOf(): Audience = Audience.empty()

fun audienceOf(vararg audiences: Audience): Audience =
        if (audiences.isEmpty()) Audience.empty() else Audience.audience(audiences.asList())

fun audienceOf(audiences: Iterable<Audience>): Audience = Audience.audience(audiences)

/**
 * Sends a chat message.
 *
 * @param message The message to send
 */
fun Audience.sendMessage(message: TextRepresentable) =
        this.sendMessage(Identity.nil(), message.asComponent())

/**
 * Sends a chat message.
 *
 * @param message The message to send
 */
fun Audience.sendMessage(message: Text) =
        this.sendMessage(Identity.nil(), message, MessageType.SYSTEM)

/**
 * Sends a chat message.
 *
 * @param message The message to send
 * @param type The message type
 */
fun Audience.sendMessage(message: TextRepresentable, type: MessageType) =
        this.sendMessage(Identity.nil(), message.asComponent(), type)

/**
 * Sends a chat message.
 *
 * @param message The message to send
 * @param type The message type
 */
fun Audience.sendMessage(message: Text, type: MessageType) =
        this.sendMessage(Identity.nil(), message, type)
