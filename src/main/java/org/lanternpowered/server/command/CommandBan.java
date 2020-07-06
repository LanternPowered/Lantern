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
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanTypes;

public final class CommandBan extends CommandProvider {

    public CommandBan() {
        super(3, "ban");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.string(Text.of("player")),
                        GenericArguments.optional(GenericArguments2.remainingString(Text.of("reason")))
                )
                .executor((src, args) -> {
                    final String target = args.<String>getOne("player").get();
                    final String reason = args.<String>getOne("reason").orElse(null);

                    Lantern.getGame().getGameProfileManager().get(target).whenComplete(((gameProfile, throwable) -> {
                        if (throwable == null) {
                            final BanService banService = Sponge.getServiceManager().provideUnchecked(BanService.class);
                            final Ban ban = Ban.builder()
                                    .type(BanTypes.PROFILE)
                                    .profile(gameProfile)
                                    .reason(reason == null ? null : Text.of(reason))
                                    .source(src)
                                    .build();
                            banService.addBan(ban);
                            Lantern.getServer().getPlayer(gameProfile.getUniqueId()).ifPresent(
                                    player -> player.kick(t("multiplayer.disconnect.banned")));
                            src.sendMessage(t("commands.ban.success", target));
                        } else {
                            src.sendMessage(t("commands.ban.failed", target));
                            Lantern.getLogger().warn("Failed to ban the player: {}", target, throwable);
                        }
                    }));
                    return CommandResult.success();
                });
    }
}
