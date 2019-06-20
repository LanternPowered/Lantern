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
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.persistence.DataBuilder
import org.spongepowered.api.data.persistence.DataSerializable
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.fluid.FluidStack
import org.spongepowered.api.fluid.FluidStackSnapshot
import org.spongepowered.api.fluid.FluidType
import org.spongepowered.api.fluid.FluidTypes
import java.util.Optional

abstract class AbstractFluidStackBuilder<T : DataSerializable, B : DataBuilder<T>> internal constructor(type: Class<T>) :
        AbstractDataBuilder<T>(type, 1) {

    private var fluidStack: LanternFluidStack? = null
    private var fluidTypeSet: Boolean = false

    fun fluidStack(fluidType: FluidType?): LanternFluidStack {
        var fluidStack = this.fluidStack
        if (fluidType != null) {
            if (fluidStack == null) {
                fluidStack = LanternFluidStack(fluidType, 0)
            } else if (fluidStack.fluid != fluidType) {
                val oldFluidStack = fluidStack
                fluidStack = LanternFluidStack(fluidType, 0)
                fluidStack.volume = oldFluidStack.volume
                fluidStack.copyFromFastNoEvents(oldFluidStack)
            }
            this.fluidTypeSet = true
        } else if (fluidStack == null) {
            fluidStack = LanternFluidStack(FluidTypes.WATER, 0)
        }
        this.fluidStack = fluidStack
        return fluidStack
    }

    fun fluid(fluidType: FluidType): B = apply {
        fluidStack(fluidType)
    }.uncheckedCast()

    fun volume(volume: Int): B = apply {
        fluidStack(null).volume = volume
    }.uncheckedCast()

    fun buildStack(): LanternFluidStack {
        check(this.fluidTypeSet) { "The fluid type must be set" }
        return fluidStack(null).copy()
    }

    fun from(fluidStackSnapshot: FluidStackSnapshot): B = apply {
        this.fluidStack = fluidStackSnapshot.createStack() as LanternFluidStack
        this.fluidTypeSet = true
    }.uncheckedCast()

    fun from(value: FluidStack): B = apply {
        this.fluidStack = value.copy() as LanternFluidStack
        this.fluidTypeSet = true
    }.uncheckedCast()

    override fun reset(): B = apply {
        this.fluidStack = null
        this.fluidTypeSet = false
    }.uncheckedCast()

    override fun buildContent(container: DataView): Optional<T> {
        throw UnsupportedOperationException("TODO")
    }
}
