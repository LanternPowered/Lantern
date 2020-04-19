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

import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.command.element.ChoicesElement;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.storage.WorldProperties;

public final class CommandDifficulty extends CommandProvider {

    public CommandDifficulty() {
        super(2, "difficulty");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        final ImmutableMap.Builder<String, Object> baseBuilder = ImmutableMap.builder();
        final ImmutableMap.Builder<String, Object> aliasesBuilder = ImmutableMap.builder();

        for (Difficulty difficulty : Sponge.getRegistry().getAllOf(Difficulty.class)) {
            baseBuilder.put(difficulty.getName(), difficulty);
            aliasesBuilder.put(difficulty.getName().substring(0, 1), difficulty);
            aliasesBuilder.put(((LanternDifficulty) difficulty).getInternalId() + "", difficulty);
        }

        specBuilder
                .arguments(
                        GenericArguments.flags()
                                .valueFlag(GenericArguments.world(CommandHelper.WORLD_KEY), "-world", "w")
                                .buildWith(GenericArguments.none()),
                        ChoicesElement.of(Text.of("difficulty"), baseBuilder.build(), aliasesBuilder.build(), false, true)
                )
                .executor((src, args) -> {
                    WorldProperties world = CommandHelper.getWorldProperties(src, args);
                    Difficulty difficulty = args.<Difficulty>getOne("difficulty").get();
                    world.setDifficulty(difficulty);
                    src.sendMessage(t("commands.difficulty.success", difficulty.getName()));
                    return CommandResult.success();
                });
    }
}
