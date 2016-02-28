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
package org.lanternpowered.server.command;

import static org.lanternpowered.server.command.CommandHelper.getWorld;
import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.world.LanternWorldProperties;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.WeatherUniverse;

import java.util.Collection;

public final class CommandWeather {

    public static final String PERMISSION = "minecraft.command.weather";

    public static CommandSpec create() {
        return CommandSpec.builder()
                .arguments(
                        new PatternMatchingCommandElement(Text.of("type")) {
                            @Override
                            protected Iterable<String> getChoices(CommandSource source) {
                                Collection<Weather> weathers = Sponge.getRegistry().getAllOf(Weather.class);
                                ImmutableList.Builder<String> builder = ImmutableList.builder();
                                for (Weather weather : weathers) {
                                    builder.add(weather.getId());
                                    builder.add(weather.getName());
                                }
                                return builder.build();
                            }

                            @Override
                            protected Object getValue(String choice) throws IllegalArgumentException {
                                return Sponge.getRegistry().getType(Weather.class, choice).orElseThrow(
                                        () -> new IllegalArgumentException("Invalid input " + choice + " was found"));
                            }
                        },
                        GenericArguments.optional(GenericArguments.integer(Text.of("duration"))),
                        GenericArguments.optional(GenericArguments.world(Text.of("world"))))
                .permission(PERMISSION)
                .executor((src, args) -> {
                    LanternWorldProperties world = getWorld(src, args);
                    WeatherUniverse weatherUniverse = world.getWorld().get().getWeatherUniverse().orElse(null);
                    Weather type = args.<Weather>getOne("type").get();
                    if (weatherUniverse != null) {
                        if (args.hasAny("duration")) {
                            weatherUniverse.setWeather(type, args.<Integer>getOne("duration").get() * 20);
                        } else {
                            weatherUniverse.setWeather(type);
                        }
                    }
                    src.sendMessage(t("Changing to " + type.getName() + " weather"));
                    return CommandResult.success();
                })
                .build();
    }

    private CommandWeather() {
    }
}
