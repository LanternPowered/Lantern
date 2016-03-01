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

import org.lanternpowered.server.config.user.OpsEntry;
import org.lanternpowered.server.config.user.UserConfig;
import org.lanternpowered.server.game.LanternGame;
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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.util.StartsWithPredicate;

import java.util.List;

import javax.annotation.Nullable;

public final class CommandOp {

    public static final String PERMISSION = "minecraft.command.op";

    public static CommandSpec create() {
        return CommandSpec.builder()
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
                                final UserConfig<OpsEntry> config = LanternGame.get().getOpsConfig();
                                return LanternGame.get().getGameProfileManager().getCachedProfiles().stream()
                                        .filter(p -> p.getName().isPresent() && !config.getEntryByUUID(p.getUniqueId()).isPresent())
                                        .map(p -> p.getName().get())
                                        .filter(new StartsWithPredicate(prefix))
                                        .collect(GuavaCollectors.toImmutableList());
                            }
                        },
                        GenericArguments.optional(GenericArguments.integer(Text.of("level"))))
                .executor((src, args) -> {
                    String playerName = args.<String>getOne("player").get();
                    UserConfig<OpsEntry> config = LanternGame.get().getOpsConfig();
                    if (!(src instanceof ConsoleSource) && args.hasAny("level")) {
                        throw new CommandException(Text.of("Only the console may specify the op level."));
                    }
                    int opLevel = args.<Integer>getOne("level").orElse(LanternGame.get().getGlobalConfig().getDefaultOpPermissionLevel());
                    LanternGame.get().getGameProfileManager().get(playerName).whenComplete((profile, error) -> {
                        if (error != null) {
                            src.sendMessage(t("commands.op.failed", playerName));
                        } else {
                            src.sendMessage(t("commands.op.success", playerName));
                            config.addEntry(new OpsEntry(((LanternGameProfile) profile).withoutProperties(), opLevel));
                        }
                    });
                    return CommandResult.success();
                })
                .permission(PERMISSION)
                .build();
    }

    private CommandOp() {
    }

}
