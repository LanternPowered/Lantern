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

import static org.lanternpowered.server.command.CommandHelper.getWorld;
import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.LocatedSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.storage.WorldProperties;

public final class CommandSetSpawn {

    public static final String PERMISSION = "minecraft.command.setworldspawn";

    public static CommandSpec create() {
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.optional(GenericArguments.vector3d(Text.of("coordinates"))),
                        GenericArguments.optional(GenericArguments.world(Text.of("world"))))
                .permission(PERMISSION)
                .executor((src, args) -> {
                    WorldProperties world = getWorld(src, args);
                    Vector3d position;
                    if (args.hasAny("coordinates")) {
                        position = args.<Vector3d>getOne("coordinates").get();
                    } else if (src instanceof LocatedSource) {
                        position = ((LocatedSource) src).getLocation().getPosition();
                    } else {
                        throw new CommandException(t("Non-located sources must specify coordinates."));
                    }
                    Vector3i position0 = position.toInt();
                    world.setSpawnPosition(position0);
                    src.sendMessage(t("commands.setworldspawn.success", position0.getX(), position0.getY(),
                            position0.getZ()));
                    return CommandResult.success();
                })
                .build();
    }

    private CommandSetSpawn() {
    }

}
