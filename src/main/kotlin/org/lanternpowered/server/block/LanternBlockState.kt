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
package org.lanternpowered.server.block

import org.lanternpowered.server.registry.type.block.BlockRegistry
import org.lanternpowered.server.state.AbstractState
import org.lanternpowered.server.state.AbstractStateContainer
import org.lanternpowered.server.state.StateBuilder
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataQuery
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.fluid.FluidState
import org.spongepowered.api.util.Direction
import org.spongepowered.api.world.Location
import java.util.Optional

class LanternBlockState(
        builder: StateBuilder<BlockState>
) : AbstractState<BlockState, BlockType>(builder), BlockState {

    private val serialized: DataContainer = toContainer()

    override fun getType() = this.stateContainer

    override fun snapshotFor(location: Location): BlockSnapshot {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun <E : Any?> get(direction: Direction?, key: Key<out Value<E>>?): Optional<E> {
        TODO("Not yet implemented")
    }

    override fun validateRawData(container: DataView?): Boolean {
        TODO("Not yet implemented")
    }

    override fun withRawData(container: DataView): BlockState {
        TODO("Not yet implemented")
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
                = AbstractStateContainer.deserializeState(dataView, BlockRegistry)
    }
}
