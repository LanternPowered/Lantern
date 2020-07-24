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

import org.lanternpowered.server.block.action.BlockActionData
import org.lanternpowered.server.network.packet.Packet
import org.spongepowered.math.vector.Vector3i

class BlockActionPacket(
        val position: Vector3i,
        val blockType: Int
) : Packet, BlockActionData {

    // TODO: Make immutable

    val parameters = IntArray(2)

    override fun set(index: Int, data: Int) {
        if (index >= 0 && index < this.parameters.size) {
            this.parameters[index] = data
        }
    }
}
