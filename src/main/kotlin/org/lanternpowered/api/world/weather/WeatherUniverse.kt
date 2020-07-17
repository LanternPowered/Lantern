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

import org.lanternpowered.api.x.world.weather.XWeatherUniverse
import kotlin.contracts.contract

typealias WeatherUniverse = org.spongepowered.api.world.weather.WeatherUniverse

/**
 * Gets the current value for the given [WeatherOption].
 */
fun <V> WeatherUniverse.get(option: WeatherOption<V>): V {
  contract { returns() implies (this@get is XWeatherUniverse) }
  return (this as XWeatherUniverse).get(option)
}
