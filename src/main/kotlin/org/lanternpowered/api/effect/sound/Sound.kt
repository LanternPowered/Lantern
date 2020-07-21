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
package org.lanternpowered.api.effect.sound

import java.util.function.Supplier

typealias SoundCategory = net.kyori.adventure.sound.Sound.Source
typealias SoundType = org.spongepowered.api.effect.sound.SoundType
typealias SoundTypes = org.spongepowered.api.effect.sound.SoundTypes
typealias SoundEffect = net.kyori.adventure.sound.Sound

fun soundEffectOf(
        type: SoundType,
        category: SoundCategory = SoundCategory.MASTER,
        volume: Double = 1.0,
        pitch: Double = 1.0
): SoundEffect = SoundEffect.of(type, category, volume.toFloat(), pitch.toFloat())

fun soundEffectOf(
        type: Supplier<out SoundType>,
        category: SoundCategory = SoundCategory.MASTER,
        volume: Double = 1.0,
        pitch: Double = 1.0
): SoundEffect = SoundEffect.of(type, category, volume.toFloat(), pitch.toFloat())
