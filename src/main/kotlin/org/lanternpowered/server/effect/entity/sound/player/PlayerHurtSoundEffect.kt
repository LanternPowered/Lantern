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
package org.lanternpowered.server.effect.entity.sound.player

import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.first
import org.lanternpowered.server.effect.entity.sound.AbstractLivingSoundEffect
import org.lanternpowered.server.entity.EntityBodyPosition
import org.lanternpowered.server.entity.LanternEntity
import org.spongepowered.api.effect.sound.SoundTypes
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources
import org.spongepowered.math.vector.Vector3d
import kotlin.random.Random

class PlayerHurtSoundEffect(position: EntityBodyPosition) : AbstractLivingSoundEffect(position) {

    override fun play(entity: LanternEntity, relativePosition: Vector3d, random: Random) {
        val soundType = when (CauseStack.currentOrEmpty().first<DamageSource>()) {
            DamageSources.FIRE_TICK.get() -> SoundTypes.ENTITY_PLAYER_HURT_ON_FIRE
            DamageSources.DROWNING.get() -> SoundTypes.ENTITY_PLAYER_HURT_DROWN
            else -> SoundTypes.ENTITY_PLAYER_HURT
        }
        entity.makeSound(soundType, relativePosition, getVolume(entity, random), getPitch(entity, random))
    }
}
