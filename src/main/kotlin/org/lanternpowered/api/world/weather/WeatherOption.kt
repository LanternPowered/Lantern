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
