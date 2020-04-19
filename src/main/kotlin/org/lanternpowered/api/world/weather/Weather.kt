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
