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
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanTypes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CommandBanIp extends CommandProvider {

    static final Pattern IP_PATTERN = Pattern.compile(
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public CommandBanIp() {
        super(3, "ban-ip");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.string(Text.of("address")),
                        GenericArguments.optional(GenericArguments2.remainingString(Text.of("reason")))
                )
                .executor((src, args) -> {
                    final String target = args.<String>getOne("address").get();
                    final String reason = args.<String>getOne("reason").orElse(null);
                    InetAddress address;
                    if (IP_PATTERN.matcher(target).matches()) {
                        try {
                            address = InetAddress.getByName(target);
                        } catch (UnknownHostException e) {
                            throw new IllegalStateException("Unable to parse a valid InetAddress: " + target, e);
                        }
                    } else {
                        // Ip address failed, try to find a player
                        Optional<Player> player = Sponge.getGame().getServer().getPlayer(target);
                        if (!player.isPresent()) {
                            throw new CommandException(t("commands.banip.invalid"));
                        }
                        address = player.get().getConnection().getAddress().getAddress();
                    }
                    final BanService banService = Sponge.getServiceManager().provideUnchecked(BanService.class);
                    final Ban ban = Ban.builder()
                            .type(BanTypes.IP)
                            .address(address)
                            .reason(reason == null ? null : Text.of(reason))
                            .source(src)
                            .build();
                    banService.addBan(ban);
                    final List<LanternPlayer> playersToKick = Lantern.getServer().getRawOnlinePlayers().stream()
                            .filter(player -> player.getConnection().getAddress().getAddress().equals(address))
                            .collect(Collectors.toList());
                    if (!playersToKick.isEmpty()) {
                        final Text kickReason = t("multiplayer.disconnect.ip_banned");
                        for (LanternPlayer player : playersToKick) {
                            player.kick(kickReason);
                        }
                    }
                    src.sendMessage(t("commands.banip.success", address.toString()));
                    return CommandResult.success();
                });
    }
}
