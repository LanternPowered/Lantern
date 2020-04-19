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

import org.lanternpowered.server.data.SerializableImmutableDataHolder
import org.lanternpowered.server.state.AbstractState
import org.lanternpowered.server.state.StateBuilder
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.fluid.FluidState
import org.spongepowered.api.fluid.FluidType

class LanternFluidState(
        builder: StateBuilder<FluidState>
) : AbstractState<FluidState, FluidType>(builder), FluidState, SerializableImmutableDataHolder<FluidState> {

    override fun getType() = this.stateContainer

    override fun isEmpty(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBlock(): BlockState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validateRawData(container: DataView?): Boolean {
        TODO("Not yet implemented")
    }

    override fun withRawData(container: DataView): FluidState {
        TODO("Not yet implemented")
    }
}
