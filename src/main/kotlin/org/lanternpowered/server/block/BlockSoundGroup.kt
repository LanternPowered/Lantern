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
package org.lanternpowered.server.block

import org.spongepowered.api.block.BlockSoundGroup
import org.spongepowered.api.effect.sound.SoundType
import java.util.function.Supplier

/**
 * Constructs a new [BlockSoundGroup].
 */
fun blockSoundGroupOf(
        breakSound: SoundType,
        stepSound: SoundType,
        placeSound: SoundType,
        hitSound: SoundType,
        fallSound: SoundType,
        volume: Double = 1.0,
        pitch: Double = 1.0
): BlockSoundGroup = LanternBlockSoundGroup(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound)

/**
 * Constructs a new [BlockSoundGroup].
 */
fun blockSoundGroupOf(
        breakSound: Supplier<out SoundType>,
        stepSound: Supplier<out SoundType>,
        placeSound: Supplier<out SoundType>,
        hitSound: Supplier<out SoundType>,
        fallSound: Supplier<out SoundType>,
        volume: Double = 1.0,
        pitch: Double = 1.0
): BlockSoundGroup = LanternBlockSoundGroup(volume, pitch, breakSound.get(), stepSound.get(), placeSound.get(), hitSound.get(), fallSound.get())

private data class LanternBlockSoundGroup(
        private val volume: Double,
        private val pitch: Double,
        private val breakSound: SoundType,
        private val stepSound: SoundType,
        private val placeSound: SoundType,
        private val hitSound: SoundType,
        private val fallSound: SoundType
) : BlockSoundGroup {

    override fun getVolume(): Double = this.volume
    override fun getPitch(): Double = this.pitch
    override fun getBreakSound(): SoundType = this.breakSound
    override fun getStepSound(): SoundType = this.stepSound
    override fun getPlaceSound(): SoundType = this.placeSound
    override fun getHitSound(): SoundType = this.hitSound
    override fun getFallSound(): SoundType = this.fallSound
}
