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

import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.server.data.DataHelper.checkDataExists
import org.lanternpowered.server.data.DataQueries
import org.lanternpowered.server.state.AbstractStateBuilder
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.persistence.InvalidDataException
import java.util.Optional

class LanternBlockStateBuilder : AbstractStateBuilder<BlockState, BlockState.Builder>(
        BlockState::class, 1), BlockState.Builder {

    override fun blockType(blockType: BlockType) = from(blockType.defaultState)

    override fun buildContent(container: DataView): Optional<BlockState> {
        if (!container.contains(DataQueries.BLOCK_STATE)) {
            return emptyOptional()
        }
        checkDataExists(container, DataQueries.BLOCK_STATE)
        try {
            return container.getCatalogType(DataQueries.BLOCK_STATE, BlockState::class.java)
        } catch (e: Exception) {
            throw InvalidDataException("Could not retrieve a block state!", e)
        }
    }
}
