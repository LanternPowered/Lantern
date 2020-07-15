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

typealias ResourceKey = org.spongepowered.api.ResourceKey

/**
 * Constructs a new [ResourceKey].
 */
@JvmName("resourceKeyOf")
inline fun ResourceKey(namespace: String, value: String): ResourceKey = ResourceKey.of(namespace, value)
