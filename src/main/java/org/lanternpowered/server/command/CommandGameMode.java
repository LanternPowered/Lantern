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

import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.command.element.ChoicesElement;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.world.rules.RuleHolder;
import org.lanternpowered.server.world.rules.RuleTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

public class CommandGameMode extends CommandProvider {

    public CommandGameMode() {
        super(2, "gamemode");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        final ImmutableMap.Builder<String, Object> baseBuilder = ImmutableMap.builder();
        final ImmutableMap.Builder<String, Object> aliasesBuilder = ImmutableMap.builder();

        for (GameMode gameMode : Sponge.getRegistry().getAllOf(GameMode.class)) {
            // Ignore the not set game mode
            if (gameMode == GameModes.NOT_SET) {
                continue;
            }
            baseBuilder.put(gameMode.getKey().getValue(), gameMode);
            aliasesBuilder.put(((LanternGameMode) gameMode).getInternalId() + "", gameMode);
        }
        aliasesBuilder.put("s", GameModes.SURVIVAL);
        aliasesBuilder.put("c", GameModes.CREATIVE);
        aliasesBuilder.put("a", GameModes.ADVENTURE);
        aliasesBuilder.put("sp", GameModes.SPECTATOR);

        specBuilder
                .arguments(
                        ChoicesElement.of(Text.of("game-mode"), baseBuilder.build(), aliasesBuilder.build(), false, true),
                        GenericArguments.playerOrSource(Text.of("player"))
                )
                .executor((src, args) -> {
                    final GameMode gameMode = args.<GameMode>getOne("game-mode").get();
                    final Player player = args.<Player>getOne("player").get();
                    player.offer(Keys.GAME_MODE, gameMode);
                    final Text gameModeText = Text.of(gameMode.getTranslation());
                    if (player == src) {
                        src.sendMessage(t("commands.gamemode.success.self", gameModeText));
                    } else {
                        if (((RuleHolder) player.getWorld()).getOrCreateRule(RuleTypes.SEND_COMMAND_FEEDBACK).getValue()) {
                            player.sendMessage(t("gameMode.changed", gameModeText));
                        }
                        src.sendMessage(t("commands.gamemode.success.other", player.getName(), gameModeText));
                    }
                    return CommandResult.success();
                });
    }
}
