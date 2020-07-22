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
package org.lanternpowered.server.fluid

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.server.data.LocalImmutableDataHolder
import org.lanternpowered.server.data.LocalKeyRegistry
import org.lanternpowered.server.state.AbstractCatalogTypeStateContainer
import org.spongepowered.api.fluid.FluidState
import org.spongepowered.api.fluid.FluidType
import org.spongepowered.api.state.StateProperty

class LanternFluidType(
        key: NamespacedKey, stateProperties: Iterable<StateProperty<*>>,
        override val keyRegistry: LocalKeyRegistry<out LocalImmutableDataHolder<FluidType>>
) : AbstractCatalogTypeStateContainer<FluidState>(key, stateProperties, ::LanternFluidState), FluidType, LocalImmutableDataHolder<FluidType>
