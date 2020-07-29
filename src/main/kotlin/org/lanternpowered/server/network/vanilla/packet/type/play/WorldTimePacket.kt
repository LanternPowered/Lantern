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

import org.lanternpowered.server.data.type.MoonPhase
import org.lanternpowered.server.network.packet.Packet

/**
 * @property moonPhase The moon phase of the world.
 * @property time The time of the world. Scales between 0 and 24000.
 * @property enabled Whether the time enabled is, this will freeze the sky animation.
 */
data class WorldTimePacket(
        val moonPhase: MoonPhase,
        val age: Long,
        val time: Int,
        val enabled: Boolean
) : Packet
