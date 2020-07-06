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
