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
import org.spongepowered.api.data.type.HandType

/**
 * A message send when a book is being modified.
 *
 * @property hand The [HandType] of which the book is being modified.
 */
sealed class ClientModifyBookPacket : Packet {

    abstract val hand: HandType
    abstract val pages: List<String>

    /**
     * Is send by the client when a book edit is being confirmed/saved.
     */
    data class Edit(
            override val hand: HandType,
            override val pages: List<String>
    ) : ClientModifyBookPacket()

    /**
     * Is send by the client when a book is being signed/finished editing.
     */
    data class Sign(
            override val hand: HandType,
            override val pages: List<String>,
            val author: String,
            val title: String
    ) : ClientModifyBookPacket()

}
