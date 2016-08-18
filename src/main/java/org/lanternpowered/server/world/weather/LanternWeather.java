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

import org.lanternpowered.api.script.function.action.Action;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.util.option.OptionValueMap;
import org.spongepowered.api.world.weather.Weather;

import java.util.Set;

public final class LanternWeather extends PluginCatalogType.Base implements Weather {

    public static WeatherBuilder builder() {
        return new WeatherBuilder();
    }

    private final Action action;
    private final Set<String> aliases;
    private final OptionValueMap options;

    LanternWeather(String pluginId, String name, Action action, Set<String> aliases, OptionValueMap options) {
        super(pluginId, name);
        this.action = action;
        this.aliases = aliases;
        this.options = options;
    }

    LanternWeather(String pluginId, String id, String name, Action action, Set<String> aliases, OptionValueMap options) {
        super(pluginId, id, name);
        this.action = action;
        this.aliases = aliases;
        this.options = options;
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
}
