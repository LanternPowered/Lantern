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

import org.lanternpowered.server.command.element.GenericArguments2;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

public final class CommandSetSpawn extends CommandProvider {

    public CommandSetSpawn() {
        super(2, "setworldspawn", "setspawn");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.flags()
                                .valueFlag(GenericArguments.world(CommandHelper.WORLD_KEY), "-world", "w")
                                .buildWith(GenericArguments.none()),
                        GenericArguments.optional(GenericArguments2.targetedVector3d(Text.of("coordinates")))
                )
                .executor((src, args) -> {
                    WorldProperties world = CommandHelper.getWorldProperties(src, args);
                    Vector3d position;
                    if (args.hasAny("coordinates")) {
                        position = args.<Vector3d>getOne("coordinates").get();
                    } else if (src instanceof Locatable) {
                        position = ((Locatable) src).getLocation().getPosition();
                    } else {
                        throw new CommandException(t("Non-located sources must specify coordinates."));
                    }
                    Vector3i position0 = position.toInt();
                    world.setSpawnPosition(position0);
                    src.sendMessage(t("commands.setworldspawn.success", position0.getX(), position0.getY(),
                            position0.getZ()));
                    return CommandResult.success();
                });
    }
}
