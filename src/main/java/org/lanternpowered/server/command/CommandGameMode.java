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

import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.command.element.ChoicesElement;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.gamerule.GameRules;

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
                        if (player.getWorld().getGameRule(GameRules.SEND_COMMAND_FEEDBACK)) {
                            player.sendMessage(t("gameMode.changed", gameModeText));
                        }
                        src.sendMessage(t("commands.gamemode.success.other", player.getName(), gameModeText));
                    }
                    return CommandResult.success();
                });
    }
}
