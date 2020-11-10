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
import org.lanternpowered.server.network.value.PackedAngle

data class EntityLookAndRelativeMovePacket(
        val entityId: Int,
        val deltaX: Int,
        val deltaY: Int,
        val deltaZ: Int,
        val yaw: PackedAngle,
        val pitch: PackedAngle,
        val isOnGround: Boolean
) : Packet
