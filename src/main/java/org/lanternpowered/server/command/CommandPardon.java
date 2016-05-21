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

import org.lanternpowered.server.config.user.ban.BanConfig;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;

public final class CommandPardon extends CommandProvider {

    public CommandPardon() {
        super(3, "pardon");
    }

    @Override
    public void completeSpec(CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.string(Text.of("player"))
                )
                .executor((src, args) -> {
                    final String target = args.<String>getOne("player").get();

                    Lantern.getGame().getGameProfileManager().get(target).whenComplete(((gameProfile, throwable) -> {
                        if (throwable == null) {
                            final BanService banService = Sponge.getServiceManager().provideUnchecked(BanService.class);
                            // Try to pardon the player with a custom cause builder
                            // to append the command source, only possible for our BanService
                            if (banService instanceof BanConfig) {
                                banService.getBanFor(gameProfile).ifPresent(ban -> ((BanConfig) banService).removeBan(ban,
                                        () -> Cause.source(src).build()));
                            } else {
                                banService.pardon(gameProfile);
                            }
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
