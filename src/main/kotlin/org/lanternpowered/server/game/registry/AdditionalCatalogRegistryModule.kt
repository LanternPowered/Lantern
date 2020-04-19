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
package org.lanternpowered.server.game.registry

import org.spongepowered.api.CatalogType


/**
 * A [CatalogRegistryModule] that allows additional registration
 * after the initial registration of the default [CatalogType]s.
 * The event [org.spongepowered.api.event.game.GameRegistryEvent.Register] will
 * be called for every [AdditionalCatalogRegistryModule] that is
 * registered for a specific [CatalogType].
 *
 * @param T The catalog type
 */
@Deprecated(message = "TODO")
interface AdditionalCatalogRegistryModule<T : CatalogType> : CatalogRegistryModule<T> {

    /**
     * Performs additional registration after initial registration of
     * the pertaining [CatalogType].
     *
     * @param extraCatalog The extra catalog to register
     */
    fun registerAdditionalCatalog(extraCatalog: T)
}
