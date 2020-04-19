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
package org.lanternpowered.api.x.world

import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.weather.WeatherUniverse

/**
 * An extended [World].
 */
interface XWorld : World {

    /**
     * The weather universe of this world, or null if this
     * world doesn't support weathers. E.g. The End.
     */
    val weatherUniverse: WeatherUniverse?
}
