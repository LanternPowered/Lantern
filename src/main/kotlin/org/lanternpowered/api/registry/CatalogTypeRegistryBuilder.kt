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
     * Registers a new [CatalogType].
     */
    fun <R : T> register(type: R): R
}
