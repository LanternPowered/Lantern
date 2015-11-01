/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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

import java.util.List;
import java.util.Random;

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldSky;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.WeatherUniverse;
import org.spongepowered.api.world.weather.Weathers;

import com.google.common.collect.Lists;

public final class LanternWeatherUniverse implements WeatherUniverse {

    private static final float FADE_SPEED = 0.01f;

    // The random used for the random weather times
    private final Random random = new Random();

    // The world this weather universe is attached to
    private final LanternWorld world;

    // The current weather
    private LanternWeather weather = (LanternWeather) Weathers.CLEAR;

    private long duration;
    private long remaining;

    private float rainStrengthTarget;
    private float rainStrength;

    private float darknessTarget;
    private float darkness;

    LanternWeatherUniverse(LanternWorld world) {
        this.world = world;
    }

    void setRaining(boolean raining) {
        if (raining && this.world.properties.rainTime > 0) {
            this.forecast(Weathers.RAIN, this.world.properties.rainTime);
        } else {
            this.world.properties.raining = false;
        }
    }

    void setRainTime(int time) {
        if (!this.world.properties.raining) {
            return;
        }
        if (this.weather.getRainStrength() > 0f) {
            this.duration += time;
            this.remaining += time;
        } else {
            this.forecast(Weathers.RAIN, time);
        }
    }

    void setThundering(boolean thundering) {
        if (thundering && this.world.properties.raining && this.world.properties.rainTime > 0
                && this.world.properties.thunderTime > 0) {
            this.forecast(Weathers.THUNDER_STORM, this.world.properties.thunderTime);
        } else {
            this.world.properties.thundering = false;
        }
    }

    void setThunderTime(int time) {
        if (!this.world.properties.raining || !this.world.properties.thundering) {
            return;
        }
        if (this.weather.getThunderRate() > 0f) {
            this.duration += time;
            this.remaining += time;
        } else {
            this.forecast(Weathers.THUNDER_STORM, time);
        }
    }

    void pulse() {
        if (this.world.properties.raining && this.world.properties.rainTime > 0) {
            this.world.properties.rainTime--;
        }
        if (this.world.properties.thundering && this.world.properties.thunderTime > 0) {
            this.world.properties.thunderTime--;
        }
        if (!this.world.properties.thundering && !this.world.properties.raining &&
                this.world.properties.clearWeatherTime > 0) {
            this.world.properties.clearWeatherTime--;
        }
        if (--this.remaining <= 0) {
            this.forecast(this.nextWeather());
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
            List<LanternPlayer> players = this.world.getPlayers();
            if (!players.isEmpty()) {
                MessagePlayOutWorldSky message = this.createSkyUpdateMessage();
                players.forEach(player -> player.getConnection().send(message));
            }
        }
    }

    MessagePlayOutWorldSky createSkyUpdateMessage() {
        return new MessagePlayOutWorldSky(this.rainStrength, this.darkness);
    }

    LanternWeather nextWeather() {
        List<Weather> weathers = Lists.newArrayList(this.world.game.getRegistry().getAllOf(Weather.class));
        while (weathers.size() > 1) {
            LanternWeather next = (LanternWeather) weathers.remove(this.random.nextInt(weathers.size()));
            if (next != this.weather) {
                return next;
            }
        }
        return weathers.isEmpty() ? this.weather : (LanternWeather) weathers.get(0);
    }

    @Override
    public LanternWeather getWeather() {
        return this.weather;
    }

    @Override
    public long getRemainingDuration() {
        return this.remaining;
    }

    @Override
    public long getRunningDuration() {
        return this.duration;
    }

    @Override
    public void forecast(Weather weather) {
        this.forecast(weather, (300 + this.random.nextInt(600)) * 20);
    }

    @Override
    public void forecast(Weather weather, long duration) {
        LanternWeather weather0 = (LanternWeather) checkNotNull(weather, "weather");
        boolean rain = weather0.getRainStrength() > 0f;
        boolean thunder = weather0.getThunderRate() > 0f;
        this.world.properties.raining = rain;
        // Lets just assume for now that it won't throw errors
        this.world.properties.rainTime = rain ? (int) duration : 0;
        this.world.properties.thundering = thunder;
        // Lets just assume for now that it won't throw errors
        this.world.properties.thunderTime = thunder ? (int) duration : 0;
        if (this.weather == weather) {
            this.duration += duration;
            this.remaining += duration;
        } else {
            this.duration = duration;
            this.remaining = duration;
        }
        this.darknessTarget = weather0.getDarkness();
        this.rainStrengthTarget = weather0.getRainStrength();
    }
}
