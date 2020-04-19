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
package org.lanternpowered.api.world;

import org.lanternpowered.api.world.weather.WeatherUniverse;
import org.spongepowered.api.world.Dimension;

import java.util.Optional;

public interface World extends org.spongepowered.api.world.World {

    /**
     * Gets the {@link WeatherUniverse} of this world if the
     * {@link Dimension} supports weathers.
     *
     * @return The weather universe
     */
    Optional<WeatherUniverse> getWeatherUniverse();
}
