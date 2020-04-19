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

import org.lanternpowered.api.registry.CatalogBuilder
import org.lanternpowered.api.x.world.weather.XWeather

/**
 * A builder to construct [Weather]s.
 */
interface WeatherBuilder : CatalogBuilder<XWeather, WeatherBuilder> {

    /**
     * Adds the option with the specified value.
     *
     * @param option The option to assign the value to
     * @param value The value to assign
     * @return This builder, for chaining
     */
    fun <V> option(option: WeatherOption<V>, value: V): WeatherBuilder
}
