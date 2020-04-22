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
import org.lanternpowered.api.registry.MutableCatalogTypeRegistryBase
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.type.typeTokenOf

/**
 * Constructs a new [GenericInternalCatalogTypeRegistry].
 */
inline fun <reified T : CatalogType, I> mutableCustomInternalCatalogTypeRegistry():
        MutableGenericInternalCatalogTypeRegistry<T, I> = mutableCustomInternalCatalogTypeRegistry(typeTokenOf())

/**
 * Constructs a new [GenericInternalCatalogTypeRegistry].
 */
fun <T : CatalogType, I> mutableCustomInternalCatalogTypeRegistry(typeToken: TypeToken<T>):
        MutableGenericInternalCatalogTypeRegistry<T, I> = LanternCatalogTypeRegistryFactory.buildMutableInternalGeneric(typeToken)

interface MutableGenericInternalCatalogTypeRegistry<T : CatalogType, I> : GenericInternalCatalogTypeRegistry<T, I>,
        MutableCatalogTypeRegistryBase<T, InternalCatalogTypeRegistryBuilder<T, I>, MutableGenericInternalCatalogTypeRegistry<T, I>>
