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
package org.lanternpowered.server.block

import com.google.common.base.Objects
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.data.DataQueries
import org.lanternpowered.server.data.ForwardingDataHolder
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.Queries
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.world.LocatableBlock
import org.spongepowered.api.world.Location
import java.util.Optional
import java.util.function.Function

class LanternLocatableBlock internal constructor(
        internal val location: Location, internal val blockState: BlockState
) : LocatableBlock, ForwardingDataHolder {

    override fun getBlockState() = this.blockState
    override fun getLocation() = this.location

    override val delegateDataHolder: DataHolder get() = this.blockState

    override fun toContainer(): DataContainer {
        val blockPos = this.location.position.toInt()
        return super.toContainer()
                .set(Queries.WORLD_ID, this.location.worldUniqueId)
                .set(Queries.POSITION_X, blockPos.x)
                .set(Queries.POSITION_Y, blockPos.y)
                .set(Queries.POSITION_Z, blockPos.z)
                .set(DataQueries.BLOCK_STATE, this.blockState)
    }

    override fun <E> transform(key: Key<out Value<E>>, function: Function<E, E>): Optional<LocatableBlock> {
        return this.blockState.transform(key, function)
                .map { state -> LanternLocatableBlock(this.location, state) }
    }

    override fun <E> with(key: Key<out Value<E>>, value: E): Optional<LocatableBlock> {
        return this.blockState.with(key, value)
                .map { state -> LanternLocatableBlock(this.location, state) }
    }

    override fun with(value: Value<*>): Optional<LocatableBlock> {
        return this.blockState.with(value)
                .map { state -> LanternLocatableBlock(this.location, state) }
    }

    override fun without(key: Key<*>): Optional<LocatableBlock> {
        return this.blockState.without(key)
                .map { state -> LanternLocatableBlock(this.location, state) }
    }

    override fun merge(that: LocatableBlock, function: MergeFunction): LocatableBlock {
        val state = this.blockState.merge(that.blockState, function)
        return if (state === this.blockState) this else LanternLocatableBlock(this.location, state)
    }

    override fun copy() = this

    override fun toString() = ToStringHelper(this)
            .add("blockState", this.blockState)
            .add("location", this.location)
            .toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is LanternLocatableBlock) {
            return false
        }
        return this.blockState === other.blockState && this.location == other.location
    }

    override fun hashCode() = Objects.hashCode(this.blockState, this.location)
}
