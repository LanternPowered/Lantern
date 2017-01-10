/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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

import org.lanternpowered.server.command.element.GenericArguments2;
import org.lanternpowered.server.config.user.ban.BanConfig;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.plugin.PluginContainer;
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
                            // Try to ban the player with a custom cause builder
                            // to append the command source, only possible for our BanService
                            if (banService instanceof BanConfig) {
                                ((BanConfig) banService).addBan(ban, () -> Cause.source(src).build());
                            } else {
                                banService.addBan(ban);
                            }
                            Lantern.getServer().getPlayer(gameProfile.getUniqueId()).ifPresent(player -> player.kick(t("disconnect.banned")));
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
