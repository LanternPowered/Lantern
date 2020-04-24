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
package org.lanternpowered.server.network.vanilla.message.type.play

import org.lanternpowered.server.network.message.Message
import org.spongepowered.api.boss.BossBarColor
import org.spongepowered.api.boss.BossBarOverlay
import org.spongepowered.api.text.Text
import java.util.UUID

sealed class BossBarMessage : Message {

    abstract val uniqueId: UUID

    data class Add(
            override val uniqueId: UUID,
            val title: Text,
            val color: BossBarColor,
            val overlay: BossBarOverlay,
            val health: Float,
            val isDarkenSky: Boolean,
            val isEndMusic: Boolean,
            val shouldCreateFog: Boolean
    ) : BossBarMessage()

    data class Remove(
            override val uniqueId: UUID
    ) : BossBarMessage()

    data class UpdatePercent(
            override val uniqueId: UUID,
            val percent: Float
    ) : BossBarMessage()

    data class UpdateTitle(
            override val uniqueId: UUID,
            val title: Text
    ) : BossBarMessage()

    data class UpdateStyle(
            override val uniqueId: UUID,
            val color: BossBarColor,
            val overlay: BossBarOverlay
    ) : BossBarMessage()

    data class UpdateMisc(
            override val uniqueId: UUID,
            val isDarkenSky: Boolean,
            val isEndMusic: Boolean,
            val shouldCreateFog: Boolean
    ) : BossBarMessage()
}
