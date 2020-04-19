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

package org.lanternpowered.api.catalog

typealias CatalogKey = org.spongepowered.api.CatalogKey
typealias CatalogKeyBuilder = org.spongepowered.api.CatalogKey.Builder

/**
 * Constructs a new [CatalogKey].
 */
@JvmName("catalogKeyOf")
inline fun CatalogKey(namespace: String, value: String): CatalogKey {
    return CatalogKeyBuilder().namespace(namespace).value(value).build()
}

/**
 * Constructs a new [CatalogKeyBuilder].
 */
@JvmName("builder")
inline fun CatalogKeyBuilder(): CatalogKeyBuilder = CatalogKey.builder()
