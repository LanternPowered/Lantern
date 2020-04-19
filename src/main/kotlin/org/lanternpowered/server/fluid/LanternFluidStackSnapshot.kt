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

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.data.MutableBackedSerializableLocalImmutableDataHolder
import org.lanternpowered.server.data.value.ValueFactory
import org.spongepowered.api.fluid.FluidStackSnapshot

class LanternFluidStackSnapshot internal constructor(fluidStack: LanternFluidStack) : FluidStackSnapshot,
        MutableBackedSerializableLocalImmutableDataHolder<FluidStackSnapshot, LanternFluidStack>(fluidStack) {

    override fun getFluid() = this.backingDataHolder.fluid
    override fun getVolume() = this.backingDataHolder.volume

    override fun createStack() = this.backingDataHolder.copy()

    override fun withBacking(backingDataHolder: LanternFluidStack) = LanternFluidStackSnapshot(backingDataHolder)

    override fun toString() = ToStringHelper(this)
            .add("fluid", this.fluid.key)
            .add("volume", this.volume)
            .add("data", ValueFactory.toString(this.backingDataHolder))
            .toString()
}
