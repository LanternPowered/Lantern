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
package org.lanternpowered.server.effect.entity.particle.item

import org.lanternpowered.server.effect.entity.EntityEffect
import org.lanternpowered.server.entity.LanternEntity
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.particle.ParticleTypes
import org.spongepowered.math.vector.Vector3d

object ItemDeathParticleEffect : EntityEffect {

    internal object EffectHolder {
        
        val DEATH_EFFECT: ParticleEffect = ParticleEffect.builder()
                .type(ParticleTypes.CLOUD).quantity(3).offset(Vector3d.ONE.mul(0.1)).build()
    }

    override fun play(entity: LanternEntity) {
        /*
        entity.world.spawnParticles(EffectHolder.DEATH_EFFECT,
                entity.boundingBox.map { obj: AABB -> obj.center }.orElseGet { entity.position })*/
        // TODO
    }
}
