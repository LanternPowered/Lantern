/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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

import org.lanternpowered.server.command.element.WorldPropertiesChoicesElement;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.world.storage.WorldProperties;

public final class CommandSeed {

    public static CommandSpec create() {
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.optional(WorldPropertiesChoicesElement.of(Texts.of("world"))))
                .permission("minecraft.command.seed")
                .executor(new CommandExecutor() {
                    @Override
                    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                        WorldProperties world;
                        if (args.hasAny("world")) {
                            world = args.<WorldProperties>getOne("world").get();
                        } else {
                            world = LanternGame.get().getServer().getDefaultWorld().orElse(null);
                            if (world == null) {
                                // Shouldn't happen
                                throw new CommandException(t("Unable to find the default world."));
                            }
                        }
                        src.sendMessage(t("commands.seed.success", world.getSeed()));
                        return CommandResult.success();
                    }
                })
                .build();
    }
}
