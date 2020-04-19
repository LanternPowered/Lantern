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
package org.lanternpowered.server.world;

import org.lanternpowered.server.world.weather.LanternWeather;
import org.spongepowered.api.world.weather.Weathers;

public final class WeatherData {

    private LanternWeather weather = (LanternWeather) Weathers.CLEAR;

    private long duration = 0;
    private long remaining = 600 * 20;

    public LanternWeather getWeather() {
        return this.weather;
    }

    public void setWeather(LanternWeather weather) {
        this.weather = weather;
    }

    public long getRunningDuration() {
        return this.duration;
    }

    public void setRunningDuration(long duration) {
        this.duration = duration;
    }

    public long getRemainingDuration() {
        return this.remaining;
    }

    public void setRemainingDuration(long remaining) {
        this.remaining = remaining;
    }
}
