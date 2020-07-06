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

import org.lanternpowered.api.world.weather.WeatherOption
import org.lanternpowered.api.world.weather.WeatherUniverse

/**
 * An extended [WeatherUniverse].
 */
interface XWeatherUniverse : WeatherUniverse {

    /**
     * Gets the current value for the given [WeatherOption].
     */
    fun <V> get(option: WeatherOption<V>): V
}
