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

import com.google.common.base.Joiner;
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.util.stream.Collectors;

public final class CommandListPlayers extends CommandProvider {

    public CommandListPlayers() {
        super(0, "list", "list-players");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
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
