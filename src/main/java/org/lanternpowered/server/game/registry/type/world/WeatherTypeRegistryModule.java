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
package org.lanternpowered.server.game.registry.type.world;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.lanternpowered.server.world.LanternWeather;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public final class WeatherTypeRegistryModule implements CatalogRegistryModule<Weather> {

    @RegisterCatalog(Weathers.class) private final Map<String, Weather> weathers = Maps.newHashMap();

    @Override
    public void registerDefaults() {
        Map<String, LanternWeather> mappings = Maps.newHashMap();
        mappings.put("clear", new LanternWeather("minecraft", "clear", 0f, 0f, 0f, 0f));
        mappings.put("rain", new LanternWeather("minecraft", "rain", 1f, 0f, 0f, 0f));
        mappings.put("thunder_storm", new LanternWeather("minecraft", "thunderStorm", 1f, 1f, 0.00001f, 0.00001f));
        mappings.forEach((key, value) -> {
            this.weathers.put(key, value);
            this.weathers.put(value.getId(), value);
            this.weathers.put(value.getName(), value);
        });
    }

    @Override
    public Optional<Weather> getById(String id) {
        return Optional.ofNullable(this.weathers.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<Weather> getAll() {
        return ImmutableList.copyOf(this.weathers.values());
    }

}
