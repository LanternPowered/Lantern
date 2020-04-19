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
package org.lanternpowered.api.world.weather;

public interface WeatherUniverse extends org.spongepowered.api.world.weather.WeatherUniverse {

    /**
     * Gets the current darkness level of the sky.
     *
     * @return The current darkness level
     */
    double getDarkness();
}
