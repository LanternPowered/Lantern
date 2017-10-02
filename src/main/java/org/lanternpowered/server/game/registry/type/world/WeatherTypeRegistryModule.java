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
package org.lanternpowered.server.game.registry.type.world;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.sound.SoundCategoryRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.sound.SoundTypeRegistryModule;
import org.lanternpowered.server.script.LanternScriptGameRegistry;
import org.lanternpowered.server.script.function.action.ActionTypeRegistryModule;
import org.lanternpowered.server.script.function.condition.ConditionTypeRegistryModule;
import org.lanternpowered.server.script.function.value.DoubleValueProviderTypeRegistryModule;
import org.lanternpowered.server.script.function.value.FloatValueProviderTypeRegistryModule;
import org.lanternpowered.server.script.function.value.IntValueProviderTypeRegistryModule;
import org.lanternpowered.server.world.weather.WeatherOptions;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

// TODO: Move the script based registry modules to a different phase too
// avoid all these dependencies, and growing depending on which actions are added.
@RegistrationDependency({ ActionTypeRegistryModule.class, ConditionTypeRegistryModule.class, DoubleValueProviderTypeRegistryModule.class,
        FloatValueProviderTypeRegistryModule.class, IntValueProviderTypeRegistryModule.class, SoundTypeRegistryModule.class,
        SoundCategoryRegistryModule.class })
public final class WeatherTypeRegistryModule extends AdditionalPluginCatalogRegistryModule<Weather> {

    public WeatherTypeRegistryModule() {
        super(Weathers.class);
    }

    @Override
    public void registerDefaults() {
        WeatherOptions.init();
        // Construct all the weathers and register them,
        // they can't be reloaded after runtime, TODO?
        LanternScriptGameRegistry.get()
                .constructAll("*:weather", Weather.class)
                .forEach(weather -> {
                    Lantern.getLogger().debug("Registered a Weather: " + weather.getId());
                    register(weather);
                });
    }
}
