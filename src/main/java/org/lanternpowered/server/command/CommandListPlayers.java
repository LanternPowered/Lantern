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

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.google.common.base.Joiner;
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.stream.Collectors;

public final class CommandListPlayers extends CommandProvider {

    public CommandListPlayers() {
        super(0, "list", "list-players");
    }

    @Override
    public void completeSpec(CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("uuids")))
                )
                .executor((src, args) -> {
                    final boolean includeUUIDs = "uuids".equalsIgnoreCase(args.<String>getOne("uuids").orElse(null));
                    final LanternServer server = Lantern.getServer();
                    src.sendMessage(t("commands.players.list", server.getRawOnlinePlayers().size(), server.getMaxPlayers()));
                    src.sendMessage(Text.of(Joiner.on(", ").join(server.getRawOnlinePlayers().stream()
                            .map(player -> {
                                String name = player.getName();
                                if (includeUUIDs) {
                                    name = String.format("%s (%s)", name, player.getUniqueId().toString());
                                }
                                return name;
                            })
                            .collect(Collectors.toList()))));
                    return CommandResult.builder().queryResult(server.getRawOnlinePlayers().size()).build();
                });
    }
}
