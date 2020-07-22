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
package org.lanternpowered.server.effect.entity.sound

import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.effect.sound.SoundType
import org.lanternpowered.server.effect.entity.AbstractEntityEffect
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.event.LanternEventContextKeys
import org.spongepowered.math.vector.Vector3d
import java.util.function.Supplier
import kotlin.random.Random

class DefaultLivingFallSoundEffect @JvmOverloads constructor(
        private val fallSoundType: SoundType,
        private val bigFallSoundType: SoundType? = null
) : AbstractEntityEffect() {

    constructor(fallSoundType: Supplier<out SoundType>, bigFallSoundType: Supplier<out SoundType>? = null) :
            this(fallSoundType.get(), bigFallSoundType?.get())

    constructor(fallSoundType: Supplier<out SoundType>, bigFallSoundType: SoundType? = null) :
            this(fallSoundType.get(), bigFallSoundType)

    constructor(fallSoundType: SoundType, bigFallSoundType: Supplier<out SoundType>? = null) :
            this(fallSoundType, bigFallSoundType?.get())

    override fun play(entity: LanternEntity, relativePosition: Vector3d, random: Random) {
        var soundType: SoundType? = this.fallSoundType
        // A big fall sound, if the distance (damage) was high enough
        if (this.bigFallSoundType != null) {
            val baseDamage = CauseStack.current().getContext(LanternEventContextKeys.BASE_DAMAGE_VALUE).orElse(0.0)
            if (baseDamage > 4.0) {
                soundType = this.bigFallSoundType
            }
        }
        entity.playSound(soundType, 1.0, 1.0)

        // Play a sound for hitting the ground
        val blockPos = entity.position.add(0.0, -0.2, 0.0).toInt()
        /* TODO
        entity.world.get(blockPos, Keys.BLOCK_SOUND_GROUP).ifPresent { soundGroup ->
            entity.playSound(soundGroup.getFallSound(), soundGroup.getVolume() * 0.5, soundGroup.getPitch() * 0.75)
        }
        */
    }
}