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
package org.lanternpowered.server.world;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import org.lanternpowered.api.script.ScriptContext;
import org.lanternpowered.api.script.context.Parameters;
import org.lanternpowered.api.world.weather.WeatherUniverse;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldSky;
import org.lanternpowered.server.script.context.ContextImpl;
import org.lanternpowered.server.world.rules.RuleTypes;
import org.lanternpowered.server.world.weather.LanternWeather;
import org.lanternpowered.server.world.weather.WeatherOptions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.world.ChangeWorldWeatherEvent;
import org.spongepowered.api.util.weighted.WeightedTable;
import org.spongepowered.api.world.weather.Weather;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class LanternWeatherUniverse implements WeatherUniverse {

    private static final float FADE_SPEED = 0.01f;

    // The random used for the random weather times
    private final Random random = new Random();
    // The world this weather universe is attached to
    private final LanternWorld world;

    private WeatherData weatherData;

    private float rainStrengthTarget;
    private float rainStrength;

    private float darknessTarget;
    private float darkness;

    private ScriptContext context;

    public LanternWeatherUniverse(LanternWorld world) {
        this.world = world;
        this.weatherData = this.world.getProperties().getWeatherData();
        this.rainStrengthTarget = this.weatherData.getWeather().getOptions().getOrDefault(WeatherOptions.RAIN_STRENGTH).get().floatValue();
        this.rainStrength = this.rainStrengthTarget;
        this.darknessTarget = this.weatherData.getWeather().getOptions().getOrDefault(WeatherOptions.SKY_DARKNESS).get().floatValue();
        this.darkness = this.darknessTarget;
        this.context = new ContextImpl(ImmutableMap.of(Parameters.WORLD, this.world));
    }

    /**
     * Pulses the weather.
     */
    void pulse() {
        pulseWeather();
        pulseSky();
    }

    private void pulseWeather() {
        if (!this.world.getOrCreateRule(RuleTypes.DO_WEATHER_CYCLE).getValue()) {
            return;
        }
        this.weatherData.setRunningDuration(this.weatherData.getRunningDuration() + 1);
        final long remaining = this.weatherData.getRemainingDuration() - 1;
        if (remaining <= 0) {
            final LanternWeather next = this.nextWeather();
            if (this.setWeather(next, next.getRandomTicksDuration(), true)) {
                // If the event is cancelled, continue the current weather for
                // a random amount of time, maybe more luck next time
                final LanternWeather current = this.weatherData.getWeather();
                this.weatherData.setRemainingDuration(current.getRandomTicksDuration());
            }
        } else {
            this.weatherData.setRemainingDuration(remaining);
        }
        this.weatherData.getWeather().getAction().run(this.context);
    }

    private void pulseSky() {
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
        this.setWeather(weather, ((LanternWeather) weather).getRandomTicksDuration());
    }

    @Override
    public void setWeather(Weather weather, long duration) {
        this.setWeather(weather, duration, false);
    }

    private boolean setWeather(Weather weather, long duration, boolean doEvent) {
        checkNotNull(weather, "weather");
        final Cause cause = Cause.source(this.world).build();
        final LanternWeather current = this.weatherData.getWeather();
        if (doEvent) {
            final ChangeWorldWeatherEvent event = SpongeEventFactory.createChangeWorldWeatherEvent(
                    cause, (int) duration, (int) duration, current, weather, weather, this.world);
            Sponge.getEventManager().post(event);
            if (event.isCancelled()) {
                return true;
            }
            weather = event.getWeather();
            duration = event.getDuration();
        }
        final LanternWeather weather0 = (LanternWeather) weather;
        this.weatherData.setRemainingDuration(duration);
        if (current != weather) {
            this.weatherData.setRunningDuration(0);
            this.weatherData.setWeather(weather0);
        }
        this.darknessTarget = weather0.getOptions().getOrDefault(WeatherOptions.SKY_DARKNESS).get().floatValue();
        this.rainStrengthTarget = weather0.getOptions().getOrDefault(WeatherOptions.RAIN_STRENGTH).get().floatValue();
        return false;
    }

    @Override
    public double getDarkness() {
        return this.darkness;
    }
}
