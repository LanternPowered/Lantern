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
package org.lanternpowered.server.world.weather;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.api.script.function.action.Action;
import org.lanternpowered.server.script.CatalogTypeConstructor;
import org.lanternpowered.server.util.option.Option;
import org.lanternpowered.server.util.option.OptionValueMap;
import org.lanternpowered.server.util.option.SimpleOptionValueMap;
import org.lanternpowered.server.util.option.UnmodifiableOptionValueMap;
import org.spongepowered.api.util.ResettableBuilder;
import org.spongepowered.api.world.weather.Weather;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

public class WeatherBuilder implements ResettableBuilder<LanternWeather, WeatherBuilder>, CatalogTypeConstructor<Weather> {

    Set<String> aliases;
    Action action;
    OptionValueMap options;
    double weight = 100;
    @Nullable private String name;

    WeatherBuilder() {
        this.reset();
    }

    /**
     * Sets the aliases of the {@link LanternWeather}.
     *
     * @param aliases The aliases
     * @return This weather builder
     */
    public WeatherBuilder aliases(String... aliases) {
        this.aliases = ImmutableSet.copyOf(aliases);
        return this;
    }

    /**
     * Sets the aliases of the {@link LanternWeather}.
     *
     * @param aliases The aliases
     * @return This weather builder
     */
    public WeatherBuilder aliases(Iterable<String> aliases) {
        this.aliases = ImmutableSet.copyOf(aliases);
        return this;
    }

    /**
     * Sets the {@link Action} of the {@link LanternWeather}.
     *
     * @param action The action
     * @return This weather builder
     */
    public WeatherBuilder action(Action action) {
        this.action = checkNotNull(action, "action");
        return this;
    }

    /**
     * Sets the {@link OptionValueMap} of the {@link LanternWeather}.
     *
     * @param optionValueMap The option value map
     * @return This weather builder
     */
    public WeatherBuilder options(OptionValueMap optionValueMap) {
        this.options = checkNotNull(optionValueMap, "optionValueMap");
        return this;
    }

    /**
     * Sets a {@link Option} for the {@link LanternWeather}. The option is applied
     * to the current {@link OptionValueMap}.
     *
     * @param option The option
     * @param value The value
     * @return This weather builder
     */
    public <T> WeatherBuilder options(Option<T> option, T value) {
        this.options.put(option, value);
        return this;
    }

    @Override
    public WeatherBuilder from(LanternWeather value) {
        this.aliases = value.getAliases();
        this.action = value.getAction();
        this.options = new SimpleOptionValueMap();
        value.getOptions().copyTo(this.options);
        return this;
    }

    @Override
    public WeatherBuilder reset() {
        this.aliases = Collections.emptySet();
        this.action = Action.empty();
        this.options = new SimpleOptionValueMap();
        return this;
    }

    public WeatherBuilder name(@Nullable String name) {
        this.name = name;
        return this;
    }

    public WeatherBuilder weight(double weight) {
        this.weight = weight;
        return this;
    }

    @Override
    public Weather create(String pluginId, String id) {
        return this.create(pluginId, id, this.name == null ? id : this.name);
    }

    @Override
    public Weather create(String pluginId, String id, String name) {
        checkNotNull(pluginId, "pluginId");
        checkNotNull(name, "name");
        checkNotNull(id, "id");
        return new LanternWeather(pluginId, id, name,
                this.action, this.aliases, new UnmodifiableOptionValueMap(this.options), this.weight);
    }
}
