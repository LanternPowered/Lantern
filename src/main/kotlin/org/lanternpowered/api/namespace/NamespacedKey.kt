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

package org.lanternpowered.api.namespace

import net.kyori.adventure.key.Key as AdventureKey

typealias NamespacedKey = org.spongepowered.api.ResourceKey

/**
 * Constructs a new [NamespacedKey].
 */
inline fun namespacedKey(namespace: String, value: String): NamespacedKey = NamespacedKey.of(namespace, value)

/**
 * Constructs a new [NamespacedKey].
 */
fun namespacedKey(namespace: Namespace, value: String): NamespacedKey = NamespacedKey.of(namespace.name, value)

/**
 * Constructs a new [NamespacedKey].
 */
fun lanternKey(value: String): NamespacedKey = namespacedKey(Namespace.LANTERN, value)

/**
 * Constructs a new [NamespacedKey].
 */
fun minecraftKey(value: String): NamespacedKey = namespacedKey(Namespace.MINECRAFT, value)

/**
 * Constructs a new [NamespacedKey].
 */
fun spongeKey(value: String): NamespacedKey = namespacedKey(Namespace.SPONGE, value)

/**
 * Resolves the [NamespacedKey] from the given value.
 */
fun resolveNamespacedKey(value: String): NamespacedKey = NamespacedKey.resolve(value)

/**
 * Converts the [AdventureKey] into a [NamespacedKey].
 */
fun AdventureKey.asNamespacedKey(): NamespacedKey =
        if (this is NamespacedKey) this else NamespacedKey.of(namespace(), value())

/**
 * Converts the [AdventureKey] into a [NamespacedKey].
 */
@Deprecated(message = "Redundant conversion.", replaceWith = ReplaceWith(""), level = DeprecationLevel.WARNING)
fun NamespacedKey.asNamespacedKey(): NamespacedKey = this
