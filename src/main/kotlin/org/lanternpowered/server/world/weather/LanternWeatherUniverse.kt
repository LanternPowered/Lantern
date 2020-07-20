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
package org.lanternpowered.server.world.weather

import org.lanternpowered.api.world.weather.WeatherUniverse
import org.spongepowered.api.world.weather.Weather
import java.time.Duration

class LanternWeatherUniverse : WeatherUniverse {

    override fun setWeather(weather: Weather) {
        TODO("Not yet implemented")
    }

    override fun setWeather(weather: Weather, duration: Duration) {
        TODO("Not yet implemented")
    }

    override fun getRemainingWeatherDuration(): Duration {
        TODO("Not yet implemented")
    }

    override fun getWeather(): Weather {
        TODO("Not yet implemented")
    }

    override fun getRunningWeatherDuration(): Duration {
        TODO("Not yet implemented")
    }
}
