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
package org.lanternpowered.server.effect.entity

import org.lanternpowered.server.entity.EntityBodyPosition
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.api.data.Keys
import org.spongepowered.math.vector.Vector3d
import kotlin.random.Random

abstract class AbstractEntityEffect protected constructor(
        private val position: EntityBodyPosition = EntityBodyPosition.BOTTOM
) : EntityEffect {

    override fun play(entity: LanternEntity) {
        var relativePosition = Vector3d.ZERO
        if (this.position == EntityBodyPosition.HEAD) {
            val eyeHeight = entity.get(Keys.EYE_HEIGHT).orElse(null)
            if (eyeHeight != null) {
                relativePosition = Vector3d(0.0, eyeHeight, 0.0)
            }
        }
        this.play(entity, relativePosition, Random)
    }

    protected abstract fun play(entity: LanternEntity, relativePosition: Vector3d, random: Random)

}