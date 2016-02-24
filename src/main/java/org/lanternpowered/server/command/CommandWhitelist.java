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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.lanternpowered.server.config.user.WhitelistConfig;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public final class CommandWhitelist {

    public static final String PERMISSION = "minecraft.command.whitelist";

    public static CommandSpec create() {
        return CommandSpec.builder().children(
                ImmutableMap.of(
                        Lists.newArrayList("add"), CommandSpec.builder()
                                .arguments(GenericArguments.string(Text.of("player")))
                                .executor((src, args) -> {
                                    String playerName = args.<String>getOne("player").get();
                                    WhitelistConfig config = LanternGame.get().getWhitelistConfig();
                                    Futures.addCallback(LanternGame.get().getGameProfileManager().get(playerName),
                                            new FutureCallback<GameProfile>() {
                                        @Override
                                        public void onSuccess(@Nullable GameProfile result) {
                                            if (result != null) {
                                                config.addProfile(((LanternGameProfile) result).withoutProperties());
                                            } else {
                                                config.addProfile(new LanternGameProfile(UUID.randomUUID(), playerName));
                                            }
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                            config.addProfile(new LanternGameProfile(UUID.randomUUID(), playerName));
                                        }
                                    });
                                    src.sendMessage(t("commands.whitelist.add.success", playerName));
                                    return CommandResult.success();
                                })
                                .build(),
                        Lists.newArrayList("list"), CommandSpec.builder()
                                .executor((src, args) -> {
                                    WhitelistConfig config = LanternGame.get().getWhitelistConfig();
                                    List<String> whitelisted = config.getWhitelistedProfiles().stream()
                                            .map(p -> p.getName().get()).collect(Collectors.toList());

                                    src.sendMessage(t("commands.whitelist.list", whitelisted.size(), Sponge.getServer().getOnlinePlayers().size()));
                                    src.sendMessage(Text.of(Joiner.on(", ").join(whitelisted)));
                                    return CommandResult.success();
                                })
                                .build(),
                        Lists.newArrayList("on"), CommandSpec.builder()
                                .executor((src, args) -> {
                                    return CommandResult.success();
                                })
                                .build(),
                        Lists.newArrayList("off"), CommandSpec.builder()
                                .executor((src, args) -> {
                                    return CommandResult.success();
                                })
                                .build(),
                        Lists.newArrayList("reload"), CommandSpec.builder()
                                .executor((src, args) -> {
                                    return CommandResult.success();
                                })
                                .build()))
                .permission(PERMISSION)
                .build();
    }

    private CommandWhitelist() {
    }

}
