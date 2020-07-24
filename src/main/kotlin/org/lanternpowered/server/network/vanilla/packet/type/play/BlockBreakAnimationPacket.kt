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

/**
 * A new block break animation packet. The id must be unique for
 * every break animation and the state must be between 0-9 in order to
 * create/update the animation, and any other value will remove it.
 *
 * @property position The position
 * @property id The id
 * @property state The state
 */
data class BlockBreakAnimationPacket(
        val position: Vector3i,
        val id: Int,
        val state: Int
) : Packet
