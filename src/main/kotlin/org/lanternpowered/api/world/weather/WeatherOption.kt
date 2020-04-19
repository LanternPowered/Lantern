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

import org.lanternpowered.api.util.option.MutableOptionMap
import org.lanternpowered.api.util.option.Option
import org.lanternpowered.api.util.option.OptionHashMap
import org.lanternpowered.api.util.option.OptionMap
import org.lanternpowered.api.util.option.OptionMapType

/**
 * A class which represents the weather option map type.
 */
class WeatherOptionMapType : OptionMapType()

/**
 * A [OptionMap] for [WeatherOption]s.
 */
typealias WeatherOptionMap = OptionMap<WeatherOptionMapType>

/**
 * A [MutableOptionMap] for [WeatherOption]s.
 */
typealias MutableWeatherOptionMap = MutableOptionMap<WeatherOptionMapType>

/**
 * A [OptionHashMap] for [WeatherOption]s.
 */
typealias WeatherOptionHashMap = OptionHashMap<WeatherOptionMapType>

/**
 * Represents a weather option.
 */
typealias WeatherOption<V> = Option<WeatherOptionMapType, V>
