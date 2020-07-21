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
import org.spongepowered.math.vector.Vector3i

data class UpdateJigsawBlockPacket(
        val position: Vector3i,
        val name: String,
        val target: String,
        val pool: String,
        val finalState: String,
        val jointType: String
) : Packet
