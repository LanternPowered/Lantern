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

sealed class WorldBorderPacket : Packet {

    data class Initialize(
            val centerX: Double,
            val centerZ: Double,
            val oldDiameter: Double,
            val newDiameter: Double,
            val lerpTime: Long,
            val worldSize: Int,
            val warningDistance: Int,
            val warningTime: Int
    ) : WorldBorderPacket()

    data class UpdateDiameter(
            val diameter: Double
    ) : WorldBorderPacket()

    data class UpdateLerpedDiameter(
            val oldDiameter: Double,
            val newDiameter: Double,
            val lerpTime: Long
    ) : WorldBorderPacket()

    data class UpdateCenter(
            val x: Double,
            val z: Double
    ) : WorldBorderPacket()

    data class UpdateWarningTime(
            val time: Int
    ) : WorldBorderPacket()

    data class UpdateWarningDistance(
            val distance: Int
    ) : WorldBorderPacket()
}
