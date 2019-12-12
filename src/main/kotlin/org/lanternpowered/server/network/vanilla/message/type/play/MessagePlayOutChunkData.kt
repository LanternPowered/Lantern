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
package org.lanternpowered.server.network.vanilla.message.type.play

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import org.lanternpowered.server.network.message.Message
import org.lanternpowered.server.util.collect.array.VariableValueArray
import org.spongepowered.api.data.persistence.DataView

sealed class MessagePlayOutChunkData(
        val x: Int,
        val z: Int,
        val sections: Array<Section?>
) : Message {

    class Init(x: Int, z: Int, sections: Array<Section?>, val biomes: IntArray) : MessagePlayOutChunkData(x, z, sections)

    class Update(x: Int, z: Int, sections: Array<Section?>) : MessagePlayOutChunkData(x, z, sections)

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
