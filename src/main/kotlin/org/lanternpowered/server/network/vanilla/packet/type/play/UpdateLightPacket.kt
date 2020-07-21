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

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.network.message.Packet

class UpdateLightPacket(
        val chunkX: Int,
        val chunkZ: Int,
        val skyLight: Array<ByteArray?>,
        val blockLight: Array<ByteArray?>
) : Packet {

    override fun toString() = ToStringHelper(this)
            .add("chunkX", this.chunkX)
            .add("chunkZ", this.chunkZ)
            .toString()

    companion object {

        /**
         * Every section has 2048 to store light values.
         */
        const val SECTION_BYTES = 2048

        /**
         * Represents a light array that provides a zero light value
         * for every position within the section.
         */
        val EMPTY_SECTION = ByteArray(0)
    }
}
