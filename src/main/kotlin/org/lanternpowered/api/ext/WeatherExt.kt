/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.ext

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.util.TypeToken
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
val WeatherUniverse.darkness: Double get() = (this as XWeatherUniverse).darkness

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
inline fun <reified V> weatherOptionOf(key: CatalogKey, defaultValue: V): WeatherOption<V> =
        Option(key, object: TypeToken<V>() {}, defaultValue, WeatherOptionMapType::class)

/**
 * Constructs a new [WeatherOption].
 */
inline fun <V> weatherOptionOf(key: CatalogKey, type: TypeToken<V>, defaultValue: V): WeatherOption<V> =
        Option(key, type, defaultValue, WeatherOptionMapType::class)
