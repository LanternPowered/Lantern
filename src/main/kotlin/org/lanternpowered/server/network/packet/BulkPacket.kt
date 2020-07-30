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
package org.lanternpowered.server.network.packet

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.collections.contentToString

/**
 * Represents a message that holds a bunch of other messages, this can be
 * used to return multiple messages from a codec.
 */
class BulkPacket(val packets: List<Packet>) : Packet {

    override fun toString(): String = ToStringHelper(this)
            .add("messages", this.packets.contentToString())
            .toString()
}
