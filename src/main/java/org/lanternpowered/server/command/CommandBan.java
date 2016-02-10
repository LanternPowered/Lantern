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

import org.lanternpowered.server.command.element.RemainingTextElement;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanTypes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

public final class CommandBan {

    public static final String PERMISSION_BAN = "minecraft.command.ban";
    public static final String PERMISSION_BAN_IP = "minecraft.command.ban-ip";

    /**
     * Creates a new ban command.
     * 
     * @param ban whether the created command spec should have "ban-ip" or "ban"
     *            command behaviour, when set to true it will use "ban"
     * @return the command spec
     */
    public static CommandSpec create(boolean ban) {
        final String targetArg = ban ? "name" : "address|name";
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.string(Text.of(targetArg)),
                        GenericArguments.optional(RemainingTextElement.of(Text.of("reason"))))
                .permission(ban ? PERMISSION_BAN : PERMISSION_BAN_IP)
                .executor((src, args) -> {
                    final String target = args.<String>getOne(targetArg).get();
                    final String reason = args.<String>getOne("reason").orElse(null);
                    BanService banService = Sponge.getServiceManager().provideUnchecked(BanService.class);
                    if (ban) {
                        // TODO
                    } else {
                        // Try as ip address first
                        try {
                            InetAddress address = InetAddress.getByName(target);
                            banService.addBan(LanternGame.get().getRegistry().createBuilder(Ban.Builder.class)
                                    .type(BanTypes.IP)
                                    .address(address)
                                    .reason(reason == null ? null : Text.of(reason))
                                    .build());
                        } catch (UnknownHostException e) {
                            // Ip address failed, try to find a player
                            Optional<Player> player = Sponge.getGame().getServer().getPlayer(target);
                            if (!player.isPresent()) {
                                throw new CommandException(t("commands.banip.invalid"));
                            }
                            banService.addBan(Sponge.getRegistry().createBuilder(Ban.Builder.class)
                                    .type(BanTypes.PROFILE)
                                    .profile(player.get().getProfile())
                                    .reason(reason == null ? null : Text.of(reason))
                                    .build());
                        }
                    }
                    return CommandResult.success();
                })
                .build();
    }

    private CommandBan() {
    }

}
