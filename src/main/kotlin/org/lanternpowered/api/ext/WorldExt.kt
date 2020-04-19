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
package org.lanternpowered.api.ext

import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.weather.WeatherUniverse
import org.lanternpowered.api.x.world.XWorld

/**
 * The weather universe of the world, if supported.
 */
val World.weatherUniverse: WeatherUniverse? get() = (this as XWorld).weatherUniverse
