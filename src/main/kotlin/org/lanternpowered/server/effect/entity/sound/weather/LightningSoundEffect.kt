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
package org.lanternpowered.server.effect.entity.sound.weather

import org.lanternpowered.server.effect.entity.AbstractEntityEffect
import org.lanternpowered.server.entity.LanternEntity
import org.spongepowered.api.effect.sound.SoundTypes
import org.spongepowered.math.vector.Vector3d
import kotlin.random.Random

object LightningSoundEffect : AbstractEntityEffect() {

    override fun play(entity: LanternEntity, relativePosition: Vector3d, random: Random) {
        entity.makeSound(SoundTypes.ENTITY_LIGHTNING_BOLT_THUNDER,
                10000.0, 0.8 + random.nextDouble() * 0.2)
        entity.makeSound(SoundTypes.ENTITY_LIGHTNING_BOLT_IMPACT,
                2.0, 0.5 + random.nextDouble() * 0.2)
    }
}
