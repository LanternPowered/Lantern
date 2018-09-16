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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.ext.*

/**
 * An iteration with all the default [WeatherOption]s.
 */
object WeatherOptions {

    /**
     * Affects the darkness of the sky.
     */
    @JvmStatic val SKY_DARKNESS = weatherOptionOf(CatalogKey.minecraft("sky_darkness"), 0.0)

    /**
     * Affects how hard it is raining. A value of 0 means no rain.
     */
    @JvmStatic val RAIN_STRENGTH = weatherOptionOf(CatalogKey.minecraft("rain_strength"), 0.0)

    /**
     * A provider which provides the duration (in seconds) when the weather is naturally switched.
     */
    @JvmStatic val DURATION = weatherOptionOf(CatalogKey.minecraft("duration")) { random.nextDouble(300.0 .. 900.0) }
}
