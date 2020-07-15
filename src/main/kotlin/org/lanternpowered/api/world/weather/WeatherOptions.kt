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
package org.lanternpowered.api.world.weather

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.ext.*

/**
 * An iteration with all the default [WeatherOption]s.
 */
object WeatherOptions {

    /**
     * Affects the darkness of the sky.
     */
    @JvmStatic val SKY_DARKNESS = weatherOptionOf(ResourceKey.minecraft("sky_darkness"), 0.0)

    /**
     * Affects how hard it is raining. A value of 0 means no rain.
     */
    @JvmStatic val RAIN_STRENGTH = weatherOptionOf(ResourceKey.minecraft("rain_strength"), 0.0)
}
