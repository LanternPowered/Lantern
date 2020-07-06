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
import org.lanternpowered.server.profile.LanternGameProfile;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.StartsWithPredicate;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class CommandOp extends CommandProvider {

    public CommandOp() {
        super(3, "op");
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
                                return Lantern.getGame().getGameProfileManager().getCache().getProfiles().stream()
                                        .filter(p -> p.getName().isPresent() && !config.getEntryByUUID(p.getUniqueId()).isPresent())
                                        .map(p -> p.getName().get())
                                        .filter(new StartsWithPredicate(prefix))
                                        .collect(ImmutableList.toImmutableList());
                            }
                        },
                        GenericArguments.optional(GenericArguments.integer(Text.of("level")))
                )
                .executor((src, args) -> {
                    String playerName = args.<String>getOne("player").get();
                    UserConfig<OpsEntry> config = Lantern.getGame().getOpsConfig();
                    if (!(src instanceof ConsoleSource) && args.hasAny("level")) {
                        throw new CommandException(Text.of("Only the console may specify the op level."));
                    }
                    int opLevel = args.<Integer>getOne("level").orElse(Lantern.getGame().getGlobalConfig().getDefaultOpPermissionLevel());
                    Lantern.getGame().getGameProfileManager().get(playerName).whenComplete((profile, error) -> {
                        if (error != null) {
                            src.sendMessage(t("commands.op.failed", playerName));
                        } else {
                            src.sendMessage(t("commands.op.success", playerName));
                            config.addEntry(new OpsEntry(((LanternGameProfile) profile).withoutProperties(), opLevel));
                        }
                    });
                    return CommandResult.success();
                });
    }
}
