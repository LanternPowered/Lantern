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
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.StartsWithPredicate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class CommandListBans extends CommandProvider {

    public CommandListBans() {
        super(3, "banlist");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.optional(new CommandElement(Text.of("ips")) {
                            private final List<String> choices = Arrays.asList("ips", "players");

                            @Nullable
                            @Override
                            protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
                                return args.next();
                            }

                            @Override
                            public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
                                final String start = args.nextIfPresent().orElse("").toLowerCase();
                                return this.choices.stream().filter(new StartsWithPredicate(start)).collect(Collectors.toList());
                            }
                        })
                )
                .executor((src, args) -> {
                    final boolean showIpBans = "ips".equalsIgnoreCase(args.<String>getOne("ips").orElse(null));
                    final BanService banService = Lantern.getGame().getServiceManager().provideUnchecked(BanService.class);
                    List<String> entries;
                    if (showIpBans) {
                        entries = banService.getIpBans().stream()
                                .map(ban -> ban.getAddress().getHostAddress())
                                .collect(Collectors.toList());
                        src.sendMessage(t("commands.banlist.ips", entries.size()));
                    } else {
                        entries = banService.getProfileBans().stream()
                                .map(ban -> ban.getProfile().getName().orElse(ban.getProfile().getUniqueId().toString()))
                                .collect(Collectors.toList());
                        src.sendMessage(t("commands.banlist.players", entries.size()));
                    }
                    src.sendMessage(Text.of(Joiner.on(", ").join(entries)));
                    return CommandResult.success();
                });
    }
}
