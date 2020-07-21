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

import org.lanternpowered.server.network.message.Packet

sealed class ChangeAdvancementTreePacket : Packet {

    /**
     * When a client switches between advancement trees (tabs).
     *
     * @param id The id of the opened advancement tree.
     */
    data class Open(val id: String) : ChangeAdvancementTreePacket()

    /**
     * When a client closes the advancements.
     */
    object Close : ChangeAdvancementTreePacket()
}
