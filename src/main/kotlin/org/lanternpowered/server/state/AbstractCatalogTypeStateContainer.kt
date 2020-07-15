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
package org.lanternpowered.server.state

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.catalog.CatalogType
import org.spongepowered.api.state.State
import org.spongepowered.api.state.StateProperty

abstract class AbstractCatalogTypeStateContainer<S : State<S>>(
        private val key: ResourceKey, stateProperties: Iterable<StateProperty<*>>, constructor: (StateBuilder<S>) -> S
) : AbstractStateContainer<S>(key, stateProperties, constructor), CatalogType {

    override fun getKey() = this.key
}
