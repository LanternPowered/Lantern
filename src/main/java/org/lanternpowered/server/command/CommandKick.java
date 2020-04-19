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

import org.lanternpowered.server.command.element.GenericArguments2;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class CommandKick extends CommandProvider {

    public CommandKick() {
        super(3, "kick");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.optional(GenericArguments2.remainingString(Text.of("reason")))
                )
                .description(t("commands.kick.description"))
                .executor((src, args) -> {
                    final Player player = args.<Player>getOne("player").get();
                    final Optional<String> optReason = args.<String>getOne("reason");
                    player.kick(optReason.<Text>map(Text::of).orElse(t("multiplayer.disconnect.kicked")));
                    if (optReason.isPresent()) {
                        src.sendMessage(t("commands.kick.success.reason", player.getName(), optReason.get()));
                    } else {
                        src.sendMessage(t("commands.kick.success", player.getName()));
                    }
                    return CommandResult.success();
                });
    }
}
