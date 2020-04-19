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

object DefaultLivingDeathAnimation : EntityEffect {

    override fun play(entity: LanternEntity) {
        // TODO: Override default client behavior (by tricking the client) and make this effect server controlled
    }
}
