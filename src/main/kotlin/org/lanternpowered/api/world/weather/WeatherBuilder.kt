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

import org.lanternpowered.api.util.builder.TranslatableCatalogBuilder
import org.lanternpowered.api.world.World
import org.lanternpowered.api.x.world.weather.XWeather

/**
 * A builder to construct [Weather]s.
 */
interface WeatherBuilder : TranslatableCatalogBuilder<XWeather, WeatherBuilder> {

    /**
     * Adds the option with the specified value.
     *
     * @param option The option to assign the value to
     * @param value The value to assign
     * @return This builder, for chaining
     */
    fun <V> option(option: WeatherOption<V>, value: V): WeatherBuilder

    /**
     * Adds a action that will be executed every tick that the weather is active
     *
     * @param fn The action function
     */
    fun action(fn: (World) -> Unit)
}
