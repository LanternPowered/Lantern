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
package org.lanternpowered.api.data.structure.chunk

import org.lanternpowered.api.block.BlockState
import org.lanternpowered.api.data.persistence.dataQueryOf
import org.lanternpowered.api.util.VariableValueArray
import org.lanternpowered.api.util.palette.Palette
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataSerializable

class ChunkBlocksData(
        val states: VariableValueArray,
        val palette: Palette<BlockState>
) : DataSerializable {

    override fun getContentVersion(): Int = 1

    override fun toContainer(): DataContainer = DataContainer.createNew()
            .set(Queries.States, this.states.backing)
            .set(Queries.Palette, this.palette.entries.map { it.key.formatted })

    object Queries {

        val States = dataQueryOf("States")
        val Palette = dataQueryOf("Palette")
    }
}
