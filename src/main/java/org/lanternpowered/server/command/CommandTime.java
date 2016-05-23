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

import org.lanternpowered.server.command.element.GenericArguments2;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.util.StartsWithPredicate;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public final class CommandTime extends CommandProvider {

    public CommandTime() {
        super(2, "time");
    }

    @Override
    public void completeSpec(CommandSpec.Builder specBuilder) {
        final Map<String, Integer> presets = new HashMap<>();
        presets.put("day", 1000);
        presets.put("night", 13000);

        specBuilder
                .arguments(
                        GenericArguments.flags()
                                .valueFlag(GenericArguments.world(Text.of("world")), "-world", "w")
                                .buildWith(GenericArguments.none())
                )
                .child(CommandSpec.builder()
                        .arguments(
                                new CommandElement(Text.of("value")) {
                                    @Nullable
                                    @Override
                                    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
                                        final String input = args.next().toLowerCase();
                                        // Try to use one of the presets first
                                        if (presets.containsKey(input)) {
                                            return presets.get(input);
                                        }
                                        try {
                                            return Integer.parseInt(input);
                                        } catch (NumberFormatException ex) {
                                            throw args.createError(t("Expected an integer or a valid preset, but input '%s' was not", input));
                                        }
                                    }

                                    @Override
                                    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
                                        final String prefix = args.nextIfPresent().orElse("");
                                        return presets.keySet().stream().filter(new StartsWithPredicate(prefix)).collect(
                                                GuavaCollectors.toImmutableList());
                                    }
                                }
                        )
                        .executor((src, args) -> {
                            WorldProperties world = getWorld(src, args);
                            int time = args.<Integer>getOne("value").get();
                            world.setWorldTime(time);
                            src.sendMessage(t("commands.time.set", time));
                            return CommandResult.success();
                        })
                        .build(), "set")
                .child(CommandSpec.builder()
                        .arguments(
                                GenericArguments.integer(Text.of("value"))
                        )
                        .executor((src, args) -> {
                            WorldProperties world = getWorld(src, args);
                            int time = args.<Integer>getOne("value").get();
                            world.setWorldTime(world.getWorldTime() + time);
                            src.sendMessage(t("commands.time.added", time));
                            return CommandResult.success();
                        })
                        .build(), "add")
                .child(CommandSpec.builder()
                        .arguments(
                                GenericArguments2.enumValue(Text.of("value"), QueryType.class)
                        )
                        .executor((src, args) -> {
                            WorldProperties world = getWorld(src, args);
                            QueryType queryType = args.<QueryType>getOne("value").get();
                            int result;
                            switch (queryType) {
                                case DAYTIME:
                                    result = (int) (world.getWorldTime() % Integer.MAX_VALUE);
                                    break;
                                case GAMETIME:
                                    result = (int) (world.getTotalTime() % Integer.MAX_VALUE);
                                    break;
                                case DAY:
                                    result = (int) (world.getTotalTime() / 24000);
                                    break;
                                default:
                                    throw new IllegalStateException("Unknown query type: " + queryType);
                            }
                            src.sendMessage(t("commands.time.query", result));
                            return CommandResult.builder().queryResult(result).build();
                        })
                        .build(), "query");
    }

    private enum QueryType {
        DAYTIME,
        GAMETIME,
        DAY,
        ;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

}
