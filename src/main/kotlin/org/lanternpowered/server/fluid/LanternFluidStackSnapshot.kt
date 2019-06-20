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

import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.data.LocalImmutableDataHolder
import org.lanternpowered.server.data.value.ValueFactory
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.fluid.FluidStackSnapshot
import java.util.Optional

class LanternFluidStackSnapshot internal constructor(private val fluidStack: LanternFluidStack) : FluidStackSnapshot,
        LocalImmutableDataHolder<FluidStackSnapshot> {

    override val keyRegistry get() = this.fluidStack.keyRegistry.forHolder<LanternFluidStackSnapshot>()

    override fun getFluid() = this.fluidStack.fluid
    override fun getVolume() = this.fluidStack.volume

    override fun createStack() = this.fluidStack.copy()

    override fun with(value: Value<*>): Optional<FluidStackSnapshot> {
        val copy = this.fluidStack.copy()
        return if (copy.offerFast(value)) {
            LanternFluidStackSnapshot(copy).optional()
        } else super.with(value)
    }

    override fun <E : Any> with(key: Key<out Value<E>>, value: E): Optional<FluidStackSnapshot> {
        val copy = this.fluidStack.copy()
        return if (copy.offerFast(key, value)) {
            LanternFluidStackSnapshot(copy).optional()
        } else super.with(key, value)
    }

    override fun without(key: Key<*>): Optional<FluidStackSnapshot> {
        val copy = this.fluidStack.copy()
        return if (copy.removeFast(key)) {
            LanternFluidStackSnapshot(copy).optional()
        } else super<LocalImmutableDataHolder>.without(key)
    }

    override fun merge(that: FluidStackSnapshot, function: MergeFunction): FluidStackSnapshot {
        val copy = this.fluidStack.copy()
        copy.copyFromNoEvents((that as LanternFluidStackSnapshot).fluidStack, function)
        return LanternFluidStackSnapshot(copy)
    }

    override fun toString() = ToStringHelper(this)
            .add("fluid", this.fluid.key)
            .add("volume", this.volume)
            .add("data", ValueFactory.toString(this.fluidStack))
            .toString()
}
