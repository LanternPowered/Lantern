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
package org.lanternpowered.server.network.vanilla.packet.type.play

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.api.util.VariableValueArray
import org.spongepowered.api.data.persistence.DataView

sealed class ChunkPacket(
        val x: Int,
        val z: Int
) : Packet {

    class Init(x: Int, z: Int, val sections: Array<Section?>, val biomes: IntArray) : ChunkPacket(x, z)

    class Update(x: Int, z: Int, val retainLighting: Boolean, val sections: Array<Section?>) : ChunkPacket(x, z)

    class Unload(x: Int, z: Int) : ChunkPacket(x, z)

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
