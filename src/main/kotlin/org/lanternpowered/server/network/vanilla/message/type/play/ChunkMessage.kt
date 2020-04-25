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
package org.lanternpowered.server.network.vanilla.message.type.play

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import org.lanternpowered.server.network.message.Message
import org.lanternpowered.server.util.VariableValueArray
import org.spongepowered.api.data.persistence.DataView

sealed class ChunkMessage(
        val x: Int,
        val z: Int,
        val sections: Array<Section?>
) : Message {

    class Init(x: Int, z: Int, sections: Array<Section?>, val biomes: IntArray) : ChunkMessage(x, z, sections)

    class Update(x: Int, z: Int, sections: Array<Section?>) : ChunkMessage(x, z, sections)

    /**
     * Represents the data of chunk section.
     *
     * Notes:
     * - If bitsPerValue is smaller then 4 bits, the client will round up to 4
     * - When bitsPerValue is greater then 8 bits, the client will use the global palette
     */
    class Section(
            val types: VariableValueArray,
            val palette: IntArray?,
            val nonAirBlockCount: Int,
            val blockEntities: Short2ObjectMap<DataView>
    )
}
