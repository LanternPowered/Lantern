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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class CommandPardonIp extends CommandProvider {

    public CommandPardonIp() {
        super(3, "pardon-ip");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.string(Text.of("address"))
                )
                .executor((src, args) -> {
                    final String target = args.<String>getOne("address").get();
                    InetAddress address;
                    if (CommandBanIp.IP_PATTERN.matcher(target).matches()) {
                        try {
                            address = InetAddress.getByName(target);
                        } catch (UnknownHostException e) {
                            throw new IllegalStateException("Unable to parse a valid InetAddress: " + target, e);
                        }
                    } else {
                        throw new CommandException(t("commands.unbanip.invalid"));
                    }
                    final BanService banService = Sponge.getServiceManager().provideUnchecked(BanService.class);
                    banService.pardon(address);
                    src.sendMessage(t("commands.banip.success", address.toString()));
                    return CommandResult.success();
                });
    }
}
