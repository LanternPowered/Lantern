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

import org.spongepowered.api.CatalogKey
import org.spongepowered.api.CatalogType
import java.util.Optional

@Deprecated(message = "TODO")
interface CatalogRegistryModule<T : CatalogType> : RegistryModule {

    /**
     * Gets the desired [CatalogType] from the provided [String] id.
     * If a [catalog type][CatalogType] is not registered for the given `id`,
     * [Optional.empty] is returned.
     *
     * @param id The id of the catalog type requested
     * @return The catalog type, if available
     */
    fun get(key: CatalogKey): Optional<T>

    /**
     * Gets all registered [CatalogType]s registered in this
     * [RegistryModule].
     *
     * @return All catalog types registered in this module
     */
    val all: Collection<T>
}
