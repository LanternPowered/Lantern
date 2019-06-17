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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.server.data.property

import com.google.common.collect.LinkedHashMultimap
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.util.Direction
import org.lanternpowered.api.util.TypeToken
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.data.property.DirectionRelativePropertyHolder
import org.spongepowered.api.data.property.PropertyHolder
import org.spongepowered.api.data.property.provider.PropertyProvider
import org.spongepowered.api.fluid.FluidState
import org.spongepowered.api.fluid.FluidType
import org.spongepowered.api.world.Location
import java.util.Optional

class LanternPropertyProviderBaseBuilder<V : Any, H : PropertyHolder> : PropertyProviderBuilder<V, H>() {

    private val functions = linkedMapOf<Class<*>, PropertyHolder.() -> Optional<V>>()
    private val directionBasedFunctions = linkedMapOf<Class<*>, DirectionRelativePropertyHolder.(Direction) -> Optional<V>>()

    private var priority = 100

    override fun priority(priority: Int) = apply {
        this.priority = priority
    }

    override fun <N : H> forHolder(holderType: TypeToken<N>) =
            LanternPropertyProviderBuilder(holderType, this)

    override fun <H> PropertyProviderBuilder<V, H>.getOptional(fn: H.(direction: Direction) -> Optional<V>):
            PropertyProviderBuilder<V, H> where H : PropertyHolder, H : DirectionRelativePropertyHolder {
        this@LanternPropertyProviderBaseBuilder.getDirectionRelativeOptional(
                typeTokenOf<PropertyHolder>().uncheckedCast(), fn)
        return this
    }

    override fun getOptional(fn: H.() -> Optional<V>) = apply {
        getOptional(typeTokenOf<PropertyHolder>().uncheckedCast(), fn)
    }

    internal fun <H> getDirectionRelativeOptional(holderType: TypeToken<H>, fn: H.(direction: Direction) -> Optional<V>) {
        this.directionBasedFunctions[holderType.rawType] = fn.uncheckedCast()
    }

    internal fun <H> getOptional(holderType: TypeToken<H>, fn: H.() -> Optional<V>) {
        this.functions[holderType.rawType] = fn.uncheckedCast()
    }

    private fun createDirectionBasedFunction(fn: PropertyHolder.() -> Optional<V>): DirectionRelativePropertyHolder.(Direction) -> Optional<V> {
        return { fn(this as PropertyHolder) }
    }

    internal fun build(): PropertyProvider<V> {
        var fn = this.functions[PropertyHolder::class.java]
        var dirFn = this.directionBasedFunctions[PropertyHolder::class.java]

        // Simple stores, no need for any conversion, etc.
        if (fn != null && (dirFn != null || this.directionBasedFunctions.isEmpty())) {
            return LanternPropertyProvider(fn, dirFn ?: createDirectionBasedFunction(fn), this.priority)
        }

        val transformedFunctions = mutableListOf<PropertyHolder.() -> Optional<V>>()
        for ((target, function) in this.functions) {
            val transformedFunction: PropertyHolder.() -> Optional<V>
            val targetConverters = mutableMapOf<Class<*>, (PropertyHolder) -> PropertyHolder?>()
            for ((originalType, pair) in converters.entries()) {
                if (pair.first == target) {
                    // Found a matching result type
                    targetConverters[originalType] = pair.second
                }
            }
            if (targetConverters.isEmpty()) {
                transformedFunction = {
                    var result: Optional<V>? = null
                    if (target.isInstance(this)) {
                        result = function(this)
                    }
                    if (result == null) {
                        for ((originalType, converter) in targetConverters) {
                            if (originalType.isInstance(this)) {
                                val transformed = converter(this)
                                if (transformed != null) {
                                    result = function(transformed)
                                    break
                                }
                            }
                        }
                    }
                    result ?: emptyOptional()
                }
            } else {
                transformedFunction = { if (target.isInstance(this)) function(this) else emptyOptional() }
            }
            transformedFunctions.add(transformedFunction)
        }

        fn = {
            var result: Optional<V>? = null
            for (transformedFunction in transformedFunctions) {
                result = transformedFunction(this)
                if (result!!.isPresent) {
                    break
                }
            }
            result ?: emptyOptional()
        }

        val transformedDirectionBasedFunctions = mutableListOf<PropertyHolder.(Direction) -> Optional<V>>()
        for ((target, function) in this.directionBasedFunctions) {
            val transformedFunction: PropertyHolder.(Direction) -> Optional<V>
            val targetConverters = mutableMapOf<Class<*>, (PropertyHolder) -> PropertyHolder?>()
            for ((originalType, pair) in converters.entries()) {
                if (pair.first == target) {
                    // Found a matching result type
                    targetConverters[originalType] = pair.second
                }
            }
            if (targetConverters.isEmpty()) {
                transformedFunction = { direction ->
                    var result: Optional<V>? = null
                    if (target.isInstance(this)) {
                        result = function(this as DirectionRelativePropertyHolder, direction)
                    }
                    if (result == null) {
                        for ((originalType, converter) in targetConverters) {
                            if (originalType.isInstance(this)) {
                                val transformed = converter(this)
                                if (transformed != null) {
                                    result = function(transformed as DirectionRelativePropertyHolder, direction)
                                    break
                                }
                            }
                        }
                    }
                    result ?: emptyOptional()
                }
            } else {
                transformedFunction = { direction ->
                    if (target.isInstance(this)) function(this as DirectionRelativePropertyHolder, direction) else emptyOptional()
                }
            }
            transformedDirectionBasedFunctions.add(transformedFunction)
        }

        dirFn = { direction ->
            var result: Optional<V>? = null
            for (transformedFunction in transformedDirectionBasedFunctions) {
                result = transformedFunction(this as PropertyHolder, direction)
                if (result!!.isPresent) {
                    break
                }
            }
            result ?: fn(this as PropertyHolder)
        }

        return LanternPropertyProvider(fn, dirFn, this.priority)
    }

    companion object {

        init {
            registerConverter<ItemStack, ItemType> { original -> original.type }
            registerConverter<ItemStack, BlockType?> { original -> original.type.block.orNull() }
            registerConverter<ItemStack, BlockState?> { original -> original.type.block.orNull()?.defaultState }
            registerConverter<ItemStack, FluidState?> { original -> original.type.block.orNull()?.defaultState?.fluidState }
            registerConverter<ItemStack, FluidType?> { original -> original.type.block.orNull()?.defaultState?.fluidState?.type }
            registerConverter<ItemType, ItemStack> { original -> itemStackOf(original) }
            registerConverter<ItemType, BlockType?> { original -> original.block.orNull() }
            registerConverter<ItemType, BlockState?> { original -> original.block.orNull()?.defaultState }
            registerConverter<ItemType, FluidState?> { original -> original.block.orNull()?.defaultState?.fluidState }
            registerConverter<ItemType, FluidType?> { original -> original.block.orNull()?.defaultState?.fluidState?.type }
            registerConverter<BlockType, BlockState> { original -> original.defaultState }
            registerConverter<BlockType, FluidState> { original -> original.defaultState.fluidState }
            registerConverter<BlockType, FluidType> { original -> original.defaultState.fluidState.type }
            registerConverter<BlockState, BlockType> { original -> original.type }
            registerConverter<BlockState, FluidState> { original -> original.fluidState }
            registerConverter<BlockState, FluidType> { original -> original.fluidState.type }
            registerConverter<Location, BlockState> { original -> original.block }
            registerConverter<Location, BlockType> { original -> original.block.type }
            registerConverter<Location, BlockEntity?> { original -> original.blockEntity.orNull() }
            registerConverter<Location, FluidState> { original -> original.fluid }
            registerConverter<Location, FluidType> { original -> original.fluid.type }
        }

        private val converters = LinkedHashMultimap.create<Class<*>, Pair<Class<*>, (PropertyHolder) -> PropertyHolder?>>()

        private inline fun <reified O : PropertyHolder, reified N : PropertyHolder?> registerConverter(noinline fn: (original: O) -> N) {
            registerConverter(typeTokenOf(), typeTokenOf(), fn)
        }

        private fun <O, N> registerConverter(originalType: TypeToken<O>, newType: TypeToken<N>, fn: (original: O) -> N) {
            this.converters.put(originalType.rawType, newType.rawType to fn.uncheckedCast())
        }
    }
}
