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
                    Lantern.getLogger().debug("Registered a Weather: " + weather.getKey());
                    register(weather);
                });
    }
}
