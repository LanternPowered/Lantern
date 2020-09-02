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

typealias Audience = net.kyori.adventure.audience.Audience
typealias Audiences = org.spongepowered.api.adventure.Audiences
typealias ForwardingAudience = net.kyori.adventure.audience.ForwardingAudience
typealias MessageType = net.kyori.adventure.audience.MessageType

inline fun emptyAudience(): Audience = Audience.empty()

fun audienceOf(): Audience = Audience.empty()

fun audienceOf(vararg audiences: Audience): Audience =
        if (audiences.isEmpty()) Audience.empty() else Audience.of(audiences.asList())

fun audienceOf(audiences: Iterable<Audience>): Audience = Audience.of(audiences)
