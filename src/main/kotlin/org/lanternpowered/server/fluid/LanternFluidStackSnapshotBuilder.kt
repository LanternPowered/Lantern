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

import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.fluid.FluidStackSnapshot

class LanternFluidStackSnapshotBuilder : AbstractFluidStackBuilder<FluidStackSnapshot, FluidStackSnapshot.Builder>(FluidStackSnapshot::class.java),
        FluidStackSnapshot.Builder {

    override fun <V : Any> add(key: Key<out Value<V>>, value: V) = apply {
        fluidStack(null).offerFastNoEvents(key, value)
    }

    override fun build() = LanternFluidStackSnapshot(buildStack())
}
