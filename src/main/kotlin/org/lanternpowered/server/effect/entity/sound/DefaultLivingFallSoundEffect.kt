/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.effect.entity.sound

import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.server.effect.entity.AbstractEntityEffect
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.event.LanternEventContextKeys
import org.spongepowered.api.effect.sound.SoundType
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