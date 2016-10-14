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
package org.lanternpowered.server.cause.entity.spawn;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.event.cause.entity.spawn.WeatherSpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.common.AbstractSpawnCauseBuilder;
import org.spongepowered.api.world.weather.Weather;

public class LanternWeatherSpawnCauseBuilder extends AbstractSpawnCauseBuilder<WeatherSpawnCause, WeatherSpawnCause.Builder>
        implements WeatherSpawnCause.Builder {

    protected Weather weather;

    @Override
    public WeatherSpawnCause.Builder weather(Weather weather) {
        this.weather = checkNotNull(weather, "weather");
        return this;
    }

    @Override
    public WeatherSpawnCause.Builder from(WeatherSpawnCause value) {
        this.weather = value.getWeather();
        return super.from(value);
    }

    @Override
    public WeatherSpawnCause.Builder reset() {
        this.weather = null;
        return super.reset();
    }

    @Override
    public WeatherSpawnCause build() {
        checkState(this.spawnType != null, "The spawn type must be set");
        checkState(this.weather != null, "The weather must be set");
        return new LanternWeatherSpawnCause(this);
    }
}
