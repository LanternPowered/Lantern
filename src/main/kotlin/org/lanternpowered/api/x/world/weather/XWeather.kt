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

import org.lanternpowered.api.text.translation.Translatable
import org.lanternpowered.api.world.weather.Weather
import org.lanternpowered.api.world.weather.WeatherOptionMap

/**
 * An extended version of [Weather].
 */
interface XWeather : Weather, Translatable {

    /**
     * The options of this weather.
     */
    val options: WeatherOptionMap
}
