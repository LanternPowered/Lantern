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

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;

public final class CommandPardon extends CommandProvider {

    public CommandPardon() {
        super(3, "pardon");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.string(Text.of("player"))
                )
                .executor((src, args) -> {
                    final String target = args.<String>getOne("player").get();

                    Lantern.getGame().getGameProfileManager().get(target).whenComplete(((gameProfile, throwable) -> {
                        if (throwable == null) {
                            final BanService banService = Sponge.getServiceManager().provideUnchecked(BanService.class);
                            banService.pardon(gameProfile);
                            src.sendMessage(t("commands.unban.success", target));
                        } else {
                            src.sendMessage(t("commands.unban.failed", target));
                            Lantern.getLogger().warn("Failed to unban the player: {}", target, throwable);
                        }
                    }));
                    return CommandResult.success();
                });
    }
}
