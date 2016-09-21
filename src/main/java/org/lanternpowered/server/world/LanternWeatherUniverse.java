/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.world;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.api.world.weather.WeatherUniverse;
import org.lanternpowered.server.component.AttachableTo;
import org.lanternpowered.server.component.Component;
import org.lanternpowered.server.component.Locked;
import org.lanternpowered.server.component.OnAttach;
import org.lanternpowered.server.inject.Inject;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldSky;
import org.lanternpowered.server.world.weather.LanternWeather;
import org.lanternpowered.server.world.weather.WeatherOptions;
import org.spongepowered.api.util.weighted.WeightedTable;
import org.spongepowered.api.world.weather.Weather;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Locked
@AttachableTo(LanternWorld.class)
public final class LanternWeatherUniverse implements Component, WeatherUniverse {

    private static final float FADE_SPEED = 0.01f;

    // The random used for the random weather times
    private final Random random = new Random();

    // The world this weather universe is attached to
    @Inject private LanternWorld world;
    private WeatherData weatherData;

    private float rainStrengthTarget;
    private float rainStrength;

    private float darknessTarget;
    private float darkness;

    @OnAttach
    private void onAttach() {
        this.weatherData = this.world.getProperties().getWeatherData();
        this.rainStrengthTarget = this.weatherData.getWeather().getOptions().getOrDefault(WeatherOptions.RAIN_STRENGTH).get().floatValue();
        this.rainStrength = this.rainStrengthTarget;
        this.darknessTarget = this.weatherData.getWeather().getOptions().getOrDefault(WeatherOptions.SKY_DARKNESS).get().floatValue();
        this.darkness = this.darknessTarget;
    }

    /**
     * Pulses the weather.
     */
    void pulse() {
        this.weatherData.setRunningDuration(this.weatherData.getRunningDuration() + 1);
        final long remaining = this.weatherData.getRemainingDuration() - 1;
        if (remaining <= 0) {
            this.setWeather(this.nextWeather());
        } else {
            this.weatherData.setRemainingDuration(remaining);
        }
        boolean updateSky = false;
        if (this.darkness != this.darknessTarget) {
            if (Math.abs(this.darkness - this.darknessTarget) < FADE_SPEED) {
                this.darkness = this.darknessTarget;
            } else if (this.darkness > this.darknessTarget) {
                this.darkness -= FADE_SPEED;
            } else {
                this.darkness += FADE_SPEED;
            }
            updateSky = true;
        }
        if (this.rainStrength != this.rainStrengthTarget) {
            if (Math.abs(this.rainStrength - this.rainStrengthTarget) < FADE_SPEED) {
                this.rainStrength = this.rainStrengthTarget;
            } else if (this.rainStrength > this.rainStrengthTarget) {
                this.rainStrength -= FADE_SPEED;
            } else {
                this.rainStrength += FADE_SPEED;
            }
            updateSky = true;
        }
        if (updateSky) {
            this.world.broadcast(this::createSkyUpdateMessage);
        }
    }

    /**
     * Creates a new {@link MessagePlayOutWorldSky} for the
     * current state of the sky.
     *
     * @return The message
     */
    public MessagePlayOutWorldSky createSkyUpdateMessage() {
        return new MessagePlayOutWorldSky(this.rainStrength, this.darkness);
    }

    /**
     * Gets the next possible {@link LanternWeather}, ignoring
     * the last weather type.
     *
     * @return The next weather type
     */
    private LanternWeather nextWeather() {
        //noinspection unchecked
        final List<LanternWeather> weathers = new ArrayList(this.world.game.getRegistry().getAllOf(Weather.class));
        final LanternWeather current = this.weatherData.getWeather();
        weathers.remove(current);
        if (weathers.isEmpty()) {
            return current;
        }

        final WeightedTable<LanternWeather> table = new WeightedTable<>();
        weathers.forEach(weather -> table.add(weather, weather.getWeight()));
        return table.get(this.random).get(0);
    }

    @Override
    public LanternWeather getWeather() {
        return this.weatherData.getWeather();
    }

    @Override
    public long getRemainingDuration() {
        return this.weatherData.getRemainingDuration();
    }

    @Override
    public long getRunningDuration() {
        return this.weatherData.getRunningDuration();
    }

    @Override
    public void setWeather(Weather weather) {
        this.setWeather(weather, (300 + this.random.nextInt(600)) * 20);
    }

    @Override
    public void setWeather(Weather weather, long duration) {
        final LanternWeather weather0 = (LanternWeather) checkNotNull(weather, "weather");
        final LanternWeather current = this.weatherData.getWeather();
        this.weatherData.setRemainingDuration(duration);
        if (current != weather) {
            this.weatherData.setRunningDuration(0);
            this.weatherData.setWeather(weather0);
        }
        this.darknessTarget = weather0.getOptions().getOrDefault(WeatherOptions.SKY_DARKNESS).get().floatValue();
        this.rainStrengthTarget = weather0.getOptions().getOrDefault(WeatherOptions.RAIN_STRENGTH).get().floatValue();
    }

    @Override
    public double getDarkness() {
        return this.darkness;
    }

}
