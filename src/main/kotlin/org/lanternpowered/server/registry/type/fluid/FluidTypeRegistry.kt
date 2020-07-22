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
package org.lanternpowered.server.registry.type.fluid

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.data.LocalKeyRegistry
import org.lanternpowered.server.fluid.LanternFluidType
import org.spongepowered.api.fluid.FluidType

val FluidTypeRegistry = catalogTypeRegistry<FluidType> {
    fun register(id: String) =
            register(LanternFluidType(NamespacedKey.minecraft(id), emptyList(), LocalKeyRegistry.of()))

    register("water")
    register("lava")
}
