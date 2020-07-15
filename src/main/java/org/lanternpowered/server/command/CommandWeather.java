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

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.world.LanternWorldPropertiesOld;
import org.lanternpowered.server.world.weather.LanternWeather;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.WeatherUniverse;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

public final class CommandWeather extends CommandProvider {

    public CommandWeather() {
        super(2, "weather");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.flags()
                                .valueFlag(GenericArguments.world(CommandHelper.WORLD_KEY), "-world", "w")
                                .buildWith(GenericArguments.none()),
                        new PatternMatchingCommandElement(Text.of("type")) {
                            @Override
                            protected Iterable<String> getChoices(CommandSource source) {
                                Collection<Weather> weathers = Sponge.getRegistry().getAllOf(Weather.class);
                                ImmutableList.Builder<String> builder = ImmutableList.builder();
                                for (Weather weather : weathers) {
                                    builder.add(weather.getKey().toString());
                                    builder.addAll(((LanternWeather) weather).getAliases());
                                }
                                return builder.build();
                            }

                            @Override
                            protected Object getValue(String choice) throws IllegalArgumentException {
                                final Optional<Weather> optWeather = Sponge.getRegistry().getType(Weather.class, ResourceKey.resolve(choice));
                                if (!optWeather.isPresent()) {
                                    return Sponge.getRegistry().getAllOf(Weather.class).stream()
                                            .filter(weather -> {
                                                for (String alias : ((LanternWeather) weather).getAliases()) {
                                                    if (alias.equalsIgnoreCase(choice)) {
                                                        return true;
                                                    }
                                                }
                                                return false;
                                            })
                                            .findAny()
                                            .orElseThrow(() -> new IllegalArgumentException("Invalid input " + choice + " was found"));
                                }
                                return optWeather.get();
                            }
                        },
                        GenericArguments.optional(GenericArguments.integer(Text.of("duration")))
                )
                .executor((src, args) -> {
                    LanternWorldPropertiesOld world = CommandHelper.getWorldProperties(src, args);
                    WeatherUniverse weatherUniverse = world.getWorld().get().getWeatherUniverse().orElse(null);
                    Weather type = args.<Weather>getOne("type").get();
                    if (weatherUniverse != null) {
                        if (args.hasAny("duration")) {
                            weatherUniverse.setWeather(type, Duration.ofSeconds(args.<Integer>getOne("duration").get()));
                        } else {
                            weatherUniverse.setWeather(type);
                        }
                    }
                    src.sendMessage(t("Changing to " + type.getName() + " weather"));
                    return CommandResult.success();
                });
    }
}
