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

import org.lanternpowered.api.script.function.action.Action;
import org.lanternpowered.api.script.function.value.IntValueProvider;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.script.context.EmptyScriptContext;
import org.lanternpowered.server.util.option.OptionValueMap;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.world.weather.Weather;

import java.util.Set;

public final class LanternWeather extends DefaultCatalogType implements Weather {

    public static WeatherBuilder builder() {
        return new WeatherBuilder();
    }

    private final Action action;
    private final Set<String> aliases;
    private final OptionValueMap options;
    private final double weight;
    private final IntValueProvider duration;

    LanternWeather(CatalogKey key, Action action, Set<String> aliases,
            OptionValueMap options, double weight, IntValueProvider duration) {
        super(key);
        this.action = action;
        this.aliases = aliases;
        this.options = options;
        this.weight = weight;
        this.duration = duration;
    }

    public Action getAction() {
        return this.action;
    }

    public Set<String> getAliases() {
        return this.aliases;
    }

    public OptionValueMap getOptions() {
        return this.options;
    }

    public double getWeight() {
        return this.weight;
    }

    public IntValueProvider getDuration() {
        return this.duration;
    }

    public long getRandomTicksDuration() {
        return this.duration.get(EmptyScriptContext.INSTANCE) * LanternGame.TICKS_PER_SECOND;
    }
}
