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
@file:Suppress("UNUSED_PARAMETER", "FunctionName", "NOTHING_TO_INLINE")

package org.lanternpowered.api

import net.kyori.adventure.key.Key as AdventureKey

typealias ResourceKey = org.spongepowered.api.ResourceKey

/**
 * Constructs a new [ResourceKey].
 */
inline fun resourceKeyOf(namespace: String, value: String): ResourceKey =
        ResourceKey.of(namespace, value)

/**
 * Converts the [AdventureKey] into a [ResourceKey].
 */
fun AdventureKey.asResourceKey(): ResourceKey =
        if (this is ResourceKey) this else ResourceKey.of(namespace(), value())

/**
 * Converts the [AdventureKey] into a [ResourceKey].
 */
@Deprecated(message = "Redundant conversion.", replaceWith = ReplaceWith(""), level = DeprecationLevel.WARNING)
fun ResourceKey.asResourceKey(): ResourceKey = this
