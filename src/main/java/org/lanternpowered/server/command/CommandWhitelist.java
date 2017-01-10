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

import com.google.common.base.Joiner;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.util.Reloadable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.whitelist.WhitelistService;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.stream.Collectors;

public final class CommandWhitelist extends CommandProvider {

    public CommandWhitelist() {
        super(3, "whitelist");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .child(CommandSpec.builder()
                        .arguments(GenericArguments.string(Text.of("player")))
                        .executor((src, args) -> {
                            final String playerName = args.<String>getOne("player").get();
                            final WhitelistService service = Sponge.getServiceManager().provideUnchecked(WhitelistService.class);
                            Lantern.getGame().getGameProfileManager().get(playerName).whenComplete((profile, error) -> {
                                if (error != null || service.isWhitelisted(profile = ((LanternGameProfile) profile).withoutProperties())) {
                                    src.sendMessage(t("commands.whitelist.add.failed", playerName));
                                } else {
                                    src.sendMessage(t("commands.whitelist.add.success", playerName));
                                    service.addProfile(profile);
                                }
                            });
                            return CommandResult.success();
                        })
                        .build(), "add")
                .child(CommandSpec.builder()
                        .arguments(GenericArguments.string(Text.of("player")))
                        .executor((src, args) -> {
                            final String playerName = args.<String>getOne("player").get();
                            final WhitelistService service = Sponge.getServiceManager().provideUnchecked(WhitelistService.class);
                            Lantern.getGame().getGameProfileManager().get(playerName).whenComplete((profile, error) -> {
                                if (error != null) {
                                    src.sendMessage(t("commands.whitelist.remove.failed", playerName));
                                } else {
                                    src.sendMessage(t("commands.whitelist.remove.success", playerName));
                                    service.removeProfile(((LanternGameProfile) profile).withoutProperties());
                                }
                            });
                            return CommandResult.success();
                        })
                        .build(), "remove")
                .child(CommandSpec.builder()
                        .executor((src, args) -> {
                            final WhitelistService service = Sponge.getServiceManager().provideUnchecked(WhitelistService.class);
                            final List<String> whitelisted = service.getWhitelistedProfiles().stream()
                                    .map(p -> p.getName().get()).collect(Collectors.toList());

                            src.sendMessage(t("commands.whitelist.list", whitelisted.size(), Sponge.getServer().getOnlinePlayers().size()));
                            src.sendMessage(Text.of(Joiner.on(", ").join(whitelisted)));
                            return CommandResult.success();
                        })
                        .build(), "list")
                .child(CommandSpec.builder()
                        .executor((src, args) -> {
                            Lantern.getGame().getGlobalConfig().setWhitelistEnabled(true);
                            src.sendMessage(t("commands.whitelist.enabled"));
                            return CommandResult.success();
                        })
                        .build(), "on")
                .child(CommandSpec.builder()
                        .executor((src, args) -> {
                            Lantern.getGame().getGlobalConfig().setWhitelistEnabled(false);
                            src.sendMessage(t("commands.whitelist.disabled"));
                            return CommandResult.success();
                        })
                        .build(), "off")
                .child(CommandSpec.builder()
                        .executor((src, args) -> {
                            final WhitelistService service = Sponge.getServiceManager().provideUnchecked(WhitelistService.class);
                            if (service instanceof Reloadable) {
                                try {
                                    ((Reloadable) service).reload();
                                } catch (Exception e) {
                                    throw new CommandException(t("commands.whitelist.reload.failed", e.getMessage()), e);
                                }
                            } else {
                                src.sendMessage(t("commands.whitelist.reload.notSupported"));
                                return CommandResult.empty();
                            }
                            return CommandResult.success();
                        })
                        .build(), "reload");
    }
}
