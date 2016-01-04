/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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

import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.command.element.ChoicesElement;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.LocatedSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.storage.WorldProperties;

public final class CommandDifficulty {

    public static CommandSpec create() {
        final ImmutableMap.Builder<String, Object> baseBuilder = ImmutableMap.builder();
        final ImmutableMap.Builder<String, Object> aliasesBuilder = ImmutableMap.builder();

        for (Difficulty difficulty : Sponge.getRegistry().getAllOf(Difficulty.class)) {
            baseBuilder.put(difficulty.getName(), difficulty);
            aliasesBuilder.put(difficulty.getName().substring(0, 1), difficulty);
            aliasesBuilder.put(((LanternDifficulty) difficulty).getInternalId() + "", difficulty);
        }

        return CommandSpec.builder()
                .arguments(
                        ChoicesElement.of(Text.of("difficulty"), baseBuilder.build(),
                                aliasesBuilder.build(), false, true),
                        GenericArguments.optional(GenericArguments.world(Text.of("world"))))
                .permission("minecraft.command.difficulty")
                .executor((src, args) -> {
                    WorldProperties world;
                    if (args.hasAny("world")) {
                        world = args.<WorldProperties>getOne("world").get();
                    } else if (src instanceof LocatedSource) {
                        world = ((LocatedSource) src).getWorld().getProperties();
                    } else {
                        throw new CommandException(Text.of("Non-located sources must specify a world."));
                    }
                    Difficulty difficulty = args.<Difficulty>getOne("difficulty").get();
                    world.setDifficulty(difficulty);
                    src.sendMessage(t("commands.difficulty.success", difficulty.getName()));
                    return CommandResult.success();
                })
                .build();
    }

    private CommandDifficulty() {
    }

}
