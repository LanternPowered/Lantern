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
package org.lanternpowered.server.effect.entity.animation

import org.lanternpowered.server.effect.entity.EntityEffect
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.entity.event.DamagedEntityEvent

/**
 * Plays a entity hurt animation. This will make a
 * living entity become red for a few ticks.
 */
object DefaultLivingHurtAnimation : EntityEffect {

    override fun play(entity: LanternEntity) {
        entity.triggerEvent(DamagedEntityEvent.of())
    }
}
