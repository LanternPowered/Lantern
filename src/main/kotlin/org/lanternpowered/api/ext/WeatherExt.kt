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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.ext

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.util.option.Option
import org.lanternpowered.api.world.weather.Weather
import org.lanternpowered.api.world.weather.WeatherOption
import org.lanternpowered.api.world.weather.WeatherOptionMap
import org.lanternpowered.api.world.weather.WeatherOptionMapType
import org.lanternpowered.api.world.weather.WeatherUniverse
import org.lanternpowered.api.x.world.weather.XWeather
import org.lanternpowered.api.x.world.weather.XWeatherUniverse

/**
 * The options of the [Weather].
 */
val Weather.options: WeatherOptionMap get() = (this as XWeather).options

/**
 * The current rain strength.
 */
val WeatherUniverse.rainStrength: Double get() = (this as XWeatherUniverse).rainStrength

/**
 * The current darkness of the sky.
 */
val WeatherUniverse.skyDarkness: Double get() = (this as XWeatherUniverse).skyDarkness

// We cannot call typeTokenOf here, chaining reified calls and using
// anonymous classes can end up in weird errors. But only in specific
// packages? The 'world' package is cursed?
// E.g.:
// error: cannot access Test$weatherOptionOf$$inlined$typeTokenOf$1
//  bad class file: D:\Projects\LanternServer\build\classes\kotlin\main\org\lanternpowered\api\world\Test$weatherOptionOf$$inlined$typeTokenOf$1.class
//    bad enclosing class for Test$weatherOptionOf$$inlined$typeTokenOf$1: WeatherExtKt

/**
 * Constructs a new [WeatherOption].
 */
inline fun <reified V> weatherOptionOf(key: NamespacedKey, defaultValue: V): WeatherOption<V> =
        Option(key, object : TypeToken<V>() {}, defaultValue, WeatherOptionMapType::class)

/**
 * Constructs a new [WeatherOption].
 */
inline fun <V> weatherOptionOf(key: NamespacedKey, type: TypeToken<V>, defaultValue: V): WeatherOption<V> =
        Option(key, type, defaultValue, WeatherOptionMapType::class)
