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
package org.lanternpowered.server.world.weather;

import org.lanternpowered.server.util.option.Option;

public final class WeatherOptions {

    /**
     * Affects the darkness of the sky.
     */
    public static final Option<Double> SKY_DARKNESS = Option.of("sky_darkness", Double.class, 0.0);

    /**
     * Affects how hard it is raining. A value of {@code 0} means no rain.
     */
    public static final Option<Double> RAIN_STRENGTH = Option.of("rain_strength", Double.class, 0.0);

    public static void init() {
        // Empty method, it's just used to load the options
    }
}
