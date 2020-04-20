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
package org.lanternpowered.server.registry

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.registry.MutableCatalogTypeRegistry
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.type.typeTokenOf

/**
 * Constructs a new [MutableInternalCatalogTypeRegistry].
 */
inline fun <reified T : CatalogType> mutableInternalCatalogTypeRegistry():
        MutableInternalCatalogTypeRegistry<T> = mutableInternalCatalogTypeRegistry(typeTokenOf())

/**
 * Constructs a new [MutableInternalCatalogTypeRegistry].
 */
fun <T : CatalogType> mutableInternalCatalogTypeRegistry(typeToken: TypeToken<T>):
        MutableInternalCatalogTypeRegistry<T> = LanternCatalogTypeRegistryFactory.buildMutable(typeToken)

interface MutableInternalCatalogTypeRegistry<T : CatalogType> : InternalCatalogTypeRegistry<T>, MutableCatalogTypeRegistry<T>
