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

import org.lanternpowered.api.boss.BossBarColor
import org.lanternpowered.api.boss.BossBarFlag
import org.lanternpowered.api.boss.BossBarOverlay
import org.lanternpowered.api.text.Text
import org.lanternpowered.server.network.packet.Packet
import java.util.UUID

sealed class BossBarPacket : Packet {

    abstract val uniqueId: UUID

    data class Add(
            override val uniqueId: UUID,
            val title: Text,
            val color: BossBarColor,
            val overlay: BossBarOverlay,
            val percent: Float,
            val flags: Set<BossBarFlag>
    ) : BossBarPacket()

    data class Remove(
            override val uniqueId: UUID
    ) : BossBarPacket()

    data class UpdatePercent(
            override val uniqueId: UUID,
            val percent: Float
    ) : BossBarPacket()

    data class UpdateName(
            override val uniqueId: UUID,
            val title: Text
    ) : BossBarPacket()

    data class UpdateStyle(
            override val uniqueId: UUID,
            val color: BossBarColor,
            val overlay: BossBarOverlay
    ) : BossBarPacket()

    data class UpdateFlags(
            override val uniqueId: UUID,
            val flags: Set<BossBarFlag>
    ) : BossBarPacket()
}
