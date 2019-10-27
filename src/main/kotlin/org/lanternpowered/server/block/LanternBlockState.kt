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

import org.lanternpowered.server.data.property.DirectionRelativePropertyHolderBase
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule
import org.lanternpowered.server.state.AbstractState
import org.lanternpowered.server.state.AbstractStateContainer
import org.lanternpowered.server.state.StateBuilder
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataQuery
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.fluid.FluidState
import org.spongepowered.api.world.Location

class LanternBlockState(
        builder: StateBuilder<BlockState>
) : AbstractState<BlockState, BlockType>(builder), BlockState, DirectionRelativePropertyHolderBase {

    private val serialized: DataContainer = toContainer()

    override fun getType() = this.stateContainer

    override fun snapshotFor(location: Location): BlockSnapshot {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getFluidState(): FluidState {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        private val nameQuery = DataQuery.of("Name")
        private val propertiesQuery = DataQuery.of("Properties")


        /**
         * Serializes the [BlockState] into the format
         * used by [BlockPalette] to store block states.
         *
         * The serialized data view should be copied before
         * modifying.
         *
         * {
         * Name: "minecraft:furnace",
         * Properties: {
         * "lit": "true"
         * }
         * }
         *
         * @param blockState The block state to serialize
         * @return The serialized block state
         */
        fun serialize(blockState: BlockState): DataView
                = (blockState as LanternBlockState).serialized

        /**
         * Deserializes the [BlockState] from the format
         * used by [BlockPalette] to store block states.
         *
         * {
         * Name: "minecraft:furnace",
         * Properties: {
         * "lit": "true"
         * }
         * }
         *
         * @param dataView The data view to deserialize
         * @return The deserialized block state
         */
        fun deserialize(dataView: DataView): BlockState
                = AbstractStateContainer.deserializeState(dataView, BlockRegistryModule.get())
    }
}
