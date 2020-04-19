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
package org.lanternpowered.api.x.world.weather

import org.lanternpowered.api.world.weather.WeatherUniverse

/**
 * An extended [WeatherUniverse].
 */
interface XWeatherUniverse : WeatherUniverse {

    /**
     * The current darkness of the sky.
     */
    val skyDarkness: Double

    /**
     * The current rain strength.
     */
    val rainStrength: Double
}
