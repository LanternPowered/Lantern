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
package org.lanternpowered.server.command;

import org.lanternpowered.api.world.weather.WeatherUniverse;
import org.lanternpowered.server.world.LanternWorldProperties;
import org.lanternpowered.server.world.weather.LanternWeather;
import org.lanternpowered.server.world.weather.WeatherOptions;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.weather.Weathers;

public class CommandToggleDownfall extends CommandProvider {

    public CommandToggleDownfall() {
        super(2, "toggledownfall");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.flags()
                                .valueFlag(GenericArguments.world(CommandHelper.WORLD_KEY), "-world", "w")
                                .buildWith(GenericArguments.none())
                )
                .executor((src, args) -> {
                    final LanternWorldProperties world = CommandHelper.getWorldProperties(src, args);
                    final WeatherUniverse weatherUniverse = world.getWorld().get().getWeatherUniverse().orElse(null);
                    final LanternWeather weather = (LanternWeather) weatherUniverse.getWeather();
                    if (weather.getOptions().getOrDefault(WeatherOptions.RAIN_STRENGTH).get() > 0) {
                        weatherUniverse.setWeather(Weathers.CLEAR);
                    } else {
                        weatherUniverse.setWeather(Weathers.RAIN);
                    }
                    return CommandResult.success();
                });
    }
}
