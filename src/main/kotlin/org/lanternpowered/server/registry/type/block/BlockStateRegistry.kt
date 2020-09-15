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
package org.lanternpowered.server.registry.type.block

import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntMaps
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.lanternpowered.api.block.BlockState
import org.lanternpowered.api.block.BlockType
import org.lanternpowered.api.block.BlockTypes
import org.lanternpowered.api.registry.CatalogTypeRegistry
import org.lanternpowered.server.game.registry.InternalRegistries.readJsonArray
import org.lanternpowered.server.registry.mutableInternalCatalogTypeRegistry
import org.lanternpowered.server.state.IState
import org.lanternpowered.server.util.palette.asPalette

val BlockStateRegistry = mutableInternalCatalogTypeRegistry<BlockState>()

/**
 * The global [BlockState] palette.
 */
val GlobalBlockStatePalette = BlockStateRegistry.asPalette { BlockTypes.AIR.get().defaultState }

/**
 * Loads the block state registry.
 */
fun loadBlockStateRegistry(registry: CatalogTypeRegistry<BlockType>) {
    BlockStateRegistry.load {
        for (type in registry.all) {
            val baseId = InternalBlockStateData.blockStateStartIds.getInt(type.key.formatted)
            for (state in type.validStates) {
                state as IState<*>
                val id = baseId + state.index
                this.register(id, state)
            }
        }
    }
}

/**
 * Internal data related to block states.
 */
object InternalBlockStateData {

    /**
     * A map with all the network ids for the [BlockType]s. Multiple
     * ids may be reserved depending on the amount of states of the block type.
     */
    var blockStateStartIds: Object2IntMap<String>
    var blockTypeIds: Object2IntMap<String>
    var blockStatesAssigned: Int

    init {
        val blockStateStartIds = Object2IntOpenHashMap<String>()
        blockStateStartIds.defaultReturnValue(-1)
        val blockTypeIds = Object2IntOpenHashMap<String>()
        blockTypeIds.defaultReturnValue(-1)
        var blockStates = 0
        val jsonArray = readJsonArray("block")
        for ((blockTypes, index) in (0 until jsonArray.size()).withIndex()) {
            val element = jsonArray[index]
            var id: String
            var states = 1
            if (element.isJsonPrimitive) {
                id = element.asString
            } else {
                val obj = element.asJsonObject
                id = obj["id"].asString
                if (obj.has("states")) {
                    states = obj["states"].asInt
                }
            }
            blockStateStartIds[id] = blockStates
            blockStates += states
            blockTypeIds[id] = blockTypes
        }
        this.blockStatesAssigned = blockStates
        this.blockStateStartIds = Object2IntMaps.unmodifiable(blockStateStartIds)
        this.blockTypeIds = Object2IntMaps.unmodifiable(blockTypeIds)
    }
}
