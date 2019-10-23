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
@file:JvmName("WeatherFactory")
@file:Suppress("NOTHING_TO_INLINE", "FunctionName")

package org.lanternpowered.api.world.weather

import org.lanternpowered.api.ext.*
import org.lanternpowered.api.x.world.weather.XWeather
import org.spongepowered.api.CatalogKey
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

typealias Weather = org.spongepowered.api.world.weather.Weather
typealias Weathers = org.spongepowered.api.world.weather.Weathers
typealias WeatherUniverse = org.spongepowered.api.world.weather.WeatherUniverse

@JvmName("of")
inline fun Weather(key: CatalogKey, fn: WeatherBuilder.() -> Unit): XWeather {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return WeatherBuilder().key(key).apply(fn).build()
}

/**
 * Constructs a new [WeatherBuilder].
 */
@JvmName("builder")
inline fun WeatherBuilder(): WeatherBuilder = builderOf()
