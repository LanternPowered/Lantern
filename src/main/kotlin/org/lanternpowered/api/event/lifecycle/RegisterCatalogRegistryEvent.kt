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
package org.lanternpowered.api.event.lifecycle

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.registry.CatalogTypeRegistry
import org.lanternpowered.api.registry.DuplicateRegistrationException

interface RegisterCatalogRegistryEvent : org.spongepowered.api.event.lifecycle.RegisterCatalogRegistryEvent {

    /**
     * Registers the [CatalogTypeRegistry] with the given [key].
     *
     * @param registry The registry to register
     * @param key The key the registry should be bound to
     * @throws DuplicateRegistrationException If the type or key is already registered
     */
    fun <T : CatalogType> register(registry: CatalogTypeRegistry<T>, key: NamespacedKey)
}
