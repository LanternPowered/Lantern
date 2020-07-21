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

import org.lanternpowered.api.effect.sound.SoundCategory
import org.lanternpowered.server.network.message.Packet

data class EntitySoundEffectPacket(
        val type: Int,
        val entityId: Int,
        val category: SoundCategory,
        val volume: Float,
        val pitch: Float
) : Packet
