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

import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataHolderBuilder
import org.spongepowered.api.data.DataManipulator
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.fluid.FluidStack
import org.spongepowered.api.fluid.FluidStackSnapshot
import org.spongepowered.api.fluid.FluidType
import org.spongepowered.api.fluid.FluidTypes
import java.util.Optional

abstract class AbstractFluidStackBuilder<T : DataHolder, B : DataHolderBuilder<T, B>> internal constructor(type: Class<T>) :
        AbstractDataBuilder<T>(type, 1), DataHolderBuilder<T, B> {

    private var fluidStack: LanternFluidStack? = null
    private var fluidTypeSet: Boolean = false

    @Suppress("UNCHECKED_CAST")
    private inline fun apply(fn: () -> Unit): B {
        fn()
        return this as B
    }

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

    fun fluid(fluidType: FluidType) = apply {
        fluidStack(fluidType)
    }

    fun volume(volume: Int) = apply {
        fluidStack(null).volume = volume
    }

    override fun add(itemData: DataManipulator) = apply {
        val itemStack = fluidStack(null)
        itemData.values.forEach { itemStack.offerFastNoEvents(it) }
    }

    override fun add(value: Value<*>) = apply {
        fluidStack(null).offerFastNoEvents(value)
    }

    override fun <V : Any> add(key: Key<out Value<V>>, value: V) = apply {
        fluidStack(null).offerFastNoEvents(key, value)
    }

    fun buildStack(): LanternFluidStack {
        check(this.fluidTypeSet) { "The fluid type must be set" }
        return fluidStack(null).copy()
    }

    fun from(fluidStackSnapshot: FluidStackSnapshot) = apply {
        this.fluidStack = fluidStackSnapshot.createStack() as LanternFluidStack
        this.fluidTypeSet = true
    }

    fun from(value: FluidStack) = apply {
        this.fluidStack = value.copy() as LanternFluidStack
        this.fluidTypeSet = true
    }

    override fun reset() = apply {
        this.fluidStack = null
        this.fluidTypeSet = false
    }

    override fun buildContent(container: DataView): Optional<T> {
        TODO()
    }
}
