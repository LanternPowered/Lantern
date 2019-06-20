/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.fluid

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.data.DataQueries
import org.lanternpowered.server.data.LocalKeyRegistry
import org.lanternpowered.server.data.LocalMutableDataHolder
import org.lanternpowered.server.data.property.PropertyHolderBase
import org.lanternpowered.server.data.value.ValueFactory
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.fluid.FluidStack
import org.spongepowered.api.fluid.FluidType

class LanternFluidStack private constructor(
        private val fluidType: FluidType,
        private var volume: Int,
        override val keyRegistry: LocalKeyRegistry<LanternFluidStack>
) : FluidStack, PropertyHolderBase, LocalMutableDataHolder {

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
