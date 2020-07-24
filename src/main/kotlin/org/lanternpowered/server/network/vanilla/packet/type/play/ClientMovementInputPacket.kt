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

/**
 * This is the only message that we will use to modify the controls
 * of the player. More info will come as I write the implementation.
 *
 * @property forwards The forwards value. (Positive is forwards, negative is backwards.)
 * @property sideways The sideways value. (Positive is left, negative is right.)
 */
data class ClientMovementInputPacket(
        val forwards: Float,
        val sideways: Float
) : Packet
