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
package org.lanternpowered.api.registry

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.util.type.TypeToken
import kotlin.reflect.KClass

/**
 * A builder for [CatalogTypeRegistry]s.
 */
interface CatalogTypeRegistryBuilder<T : CatalogType> {

    /**
     * Allows registrations by plugins. This will trigger an event
     * after the initial registration and each reload.
     */
    fun allowPluginRegistrations()

    /**
     * Checks whether the suggested id is applicable for the target
     * catalog type. The suggested id is always lowercase.
     */
    fun matchSuggestedId(matcher: (suggestedId: String, type: T) -> Boolean)

    /**
     * Processes suggested ids and type information from catalog fields
     * in the target catalog classes.
     *
     * Only public static final fields will be processed. Or if it's a public
     * not open property in a kotlin object.
     */
    fun processSuggestions(catalogs: Iterable<KClass<*>>, function: (suggestedId: String, type: TypeToken<out T>) -> Unit)

    /**
     * Processes suggested ids and type information from catalog fields
     * in the target catalog classes.
     *
     * Only public static final fields will be processed. Or if it's a public
     * not open property in a kotlin object.
     */
    fun processSuggestions(firstCatalog: KClass<*>, vararg moreCatalogs: KClass<*>, function: (suggestedId: String, type: TypeToken<out T>) -> Unit) =
            processSuggestions(listOf(firstCatalog) + moreCatalogs.asList(), function)

    /**
     * Registers a new [CatalogType].
     */
    fun <R : T> register(type: R): R
}
