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
import org.lanternpowered.server.data.DataQueries
import org.lanternpowered.server.data.LocalKeyRegistry
import org.lanternpowered.server.data.SerializableLocalMutableDataHolder
import org.lanternpowered.server.data.value.ValueFactory
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.fluid.FluidStack
import org.spongepowered.api.fluid.FluidType

class LanternFluidStack private constructor(
        private val fluidType: FluidType,
        private var volume: Int,
        override val keyRegistry: LocalKeyRegistry<LanternFluidStack>
) : FluidStack, SerializableLocalMutableDataHolder {

    constructor(fluidType: FluidType, volume: Int) : this(fluidType, volume, LocalKeyRegistry.of())

    override fun getFluid() = this.fluidType
    override fun getVolume() = this.volume

    override fun setVolume(volume: Int) = apply {
        check(volume >= 0) { "volume cannot be negative" }
        check(volume <= 1000) { "volume cannot be greater then 1000" }
        this.volume = volume
    }

    override fun createSnapshot() = LanternFluidStackSnapshot(copy())
    override fun copy() = LanternFluidStack(this.fluid, this.volume, this.keyRegistry.copy())

    override fun validateRawData(dataView: DataView): Boolean {
        return super.validateRawData(dataView) && dataView.contains(DataQueries.FLUID_TYPE)
    }

    override fun setRawData(dataView: DataView) {
        dataView.remove(DataQueries.FLUID_TYPE)
        this.volume = dataView.getInt(DataQueries.VOLUME).orElse(0)
        super.setRawData(dataView)
    }

    override fun toContainer(): DataContainer = super.toContainer()
            .set(DataQueries.FLUID_TYPE, this.fluid)
            .set(DataQueries.VOLUME, this.volume)

    override fun toString() = ToStringHelper(this)
            .add("fluid", this.fluid.key)
            .add("volume", this.volume)
            .add("data", ValueFactory.toString(this))
            .toString()
}
