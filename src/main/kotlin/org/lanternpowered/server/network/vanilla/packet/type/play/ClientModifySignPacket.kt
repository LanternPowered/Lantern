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

import org.lanternpowered.server.network.packet.Packet
import org.spongepowered.math.vector.Vector3i

class ClientModifySignPacket(
        val position: Vector3i,
        val lines: Array<String>
) : Packet {

    init {
        check(this.lines.size == EXPECTED_LINES_SIZE) { "lines length must be $EXPECTED_LINES_SIZE" }
    }

    companion object {
        const val EXPECTED_LINES_SIZE = 4
    }
}
