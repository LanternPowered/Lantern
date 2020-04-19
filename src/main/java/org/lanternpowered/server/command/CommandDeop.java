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

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.config.user.OpsEntry;
import org.lanternpowered.server.config.user.UserConfig;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.StartsWithPredicate;

import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class CommandDeop extends CommandProvider {

    public CommandDeop() {
        super(3, "deop");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        new CommandElement(Text.of("player")) {
                            @Nullable
                            @Override
                            protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
                                return args.next();
                            }

                            @Override
                            public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
                                final String prefix = args.nextIfPresent().orElse("");
                                final UserConfig<OpsEntry> config = Lantern.getGame().getOpsConfig();
                                return config.getEntries().stream()
                                        .filter(e -> e.getProfile().getName().isPresent())
                                        .map(e -> e.getProfile().getName().get())
                                        .filter(new StartsWithPredicate(prefix))
                                        .collect(ImmutableList.toImmutableList());
                            }
                        }
                )
                .executor((src, args) -> {
                    String playerName = args.<String>getOne("player").get();
                    UserConfig<OpsEntry> config = Lantern.getGame().getOpsConfig();
                    Optional<OpsEntry> entry = config.getEntryByName(playerName);
                    if (entry.isPresent()) {
                        config.removeEntry(entry.get().getProfile().getUniqueId());
                        src.sendMessage(t("commands.deop.success", playerName));
                    } else {
                        src.sendMessage(t("commands.deop.failed", playerName));
                    }
                    return CommandResult.success();
                });
    }
}
