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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.api.script.ScriptContext;
import org.lanternpowered.api.script.context.Parameters;
import org.lanternpowered.server.network.vanilla.packet.type.play.UpdateWorldSkyPacket;
import org.lanternpowered.server.script.context.ContextImpl;
import org.lanternpowered.server.world.weather.LanternWeather;
import org.lanternpowered.server.world.weather.WeatherOptions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.world.ChangeWorldWeatherEvent;
import org.spongepowered.api.util.weighted.WeightedTable;
import org.spongepowered.api.world.gamerule.GameRules;
import org.spongepowered.api.world.weather.Weather;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class LanternWeatherUniverse implements WeatherUniverse {

    private static final float FADE_SPEED = 0.01f;

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
    void pulse(CauseStack causeStack) {
        pulseWeather(causeStack);
        pulseSky();
    }

    private void pulseWeather(CauseStack causeStack) {
        if (!this.world.getGameRule(GameRules.DO_WEATHER_CYCLE)) {
            return;
        }
        this.weatherData.setRunningDuration(this.weatherData.getRunningDuration() + 1);
        final long remaining = this.weatherData.getRemainingDuration() - 1;
        if (remaining <= 0) {
            final LanternWeather next = this.nextWeather();
            if (setWeather(causeStack, next, next.getRandomTicksDuration(), true)) {
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
     * Creates a new {@link UpdateWorldSkyPacket} for the
     * current state of the sky.
     *
     * @return The message
     */
    public UpdateWorldSkyPacket createSkyUpdateMessage() {
        return new UpdateWorldSkyPacket(this.rainStrength, this.darkness);
    }

    /**
     * Gets the next possible {@link LanternWeather}, ignoring
     * the last weather type.
     *
     * @return The next weather type
     */
    @SuppressWarnings("unchecked")
    private LanternWeather nextWeather() {
        final List<LanternWeather> weathers = new ArrayList(this.world.game.getRegistry().getAllOf(Weather.class));
        final LanternWeather current = this.weatherData.getWeather();
        weathers.remove(current);
        if (weathers.isEmpty()) {
            return current;
        }

        final WeightedTable<LanternWeather> table = new WeightedTable<>();
        weathers.forEach(weather -> table.add(weather, weather.getWeight()));
        return table.get(ThreadLocalRandom.current()).get(0);
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
        setWeather(weather, ((LanternWeather) weather).getRandomTicksDuration());
    }

    @Override
    public void setWeather(Weather weather, long duration) {
        setWeather(CauseStack.current(), weather, duration, false);
    }

    private boolean setWeather(CauseStack causeStack, Weather weather, long duration, boolean doEvent) {
        checkNotNull(weather, "weather");
        final LanternWeather current = this.weatherData.getWeather();
        if (doEvent) {
            final ChangeWorldWeatherEvent event = SpongeEventFactory.createChangeWorldWeatherEvent(
                    causeStack.getCurrentCause(), (int) duration, (int) duration, current, weather, weather, this.world);
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
