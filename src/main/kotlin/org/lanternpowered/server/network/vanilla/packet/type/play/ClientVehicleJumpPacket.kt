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
 * This message is send when the player jumps with a vehicle.
 */
data class ClientVehicleJumpPacket(
        /**
         * The jumping state.
         */
        val isJumping: Boolean,
        /**
         * The progress of strength (charge) bar, scales between 0 and 1.
         *
         * This value will only return something greater then 0 if the player
         * is riding a horse and the new jump state ([isJumping] returns `false`).
         * Which means that the player released the jump button.
         */
        val powerProgress: Float
) : Packet
