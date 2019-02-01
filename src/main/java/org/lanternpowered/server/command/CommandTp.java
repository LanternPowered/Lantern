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

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.command.element.GenericArguments2;
import org.lanternpowered.server.command.element.RelativeDouble;
import org.lanternpowered.server.command.element.RelativeVector3d;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

public final class CommandTp extends CommandProvider {

    public CommandTp() {
        super(2, "tp");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.optional(GenericArguments.player(Text.of("target"))),
                        GenericArguments.firstParsing(
                                GenericArguments.player(Text.of("destination")),
                                GenericArguments.seq(
                                        /*
                                        GenericArguments.flags()
                                                .valueFlag(GenericArguments.world(CommandHelper.WORLD_KEY),
                                                        "-world", "w")
                                                .buildWith(GenericArguments.none()),
                                        */
                                        GenericArguments.optional(GenericArguments.world(CommandHelper.WORLD_KEY)),
                                        GenericArguments2.targetedRelativeVector3d(Text.of("coordinates")),
                                        GenericArguments.optional(GenericArguments.seq(
                                                GenericArguments2.relativeDoubleNum(Text.of("y-rot")),
                                                GenericArguments2.relativeDoubleNum(Text.of("x-rot"))
                                        ))
                                )
                        )
                )
                .executor((src, args) -> {
                    Player target = args.<Player>getOne("target").orElse(null);
                    if (target == null) {
                        if (!(src instanceof Player)) {
                            throw new CommandException(t("The target parameter is only optional for players."));
                        }
                        target = (Player) src;
                    }
                    final Optional<Player> optDestination = args.getOne("destination");
                    if (optDestination.isPresent()) {
                        final Player destination = optDestination.get();
                        target.setTransform(destination.getTransform());
                        src.sendMessage(t("commands.tp.success", target.getName(), destination.getName()));
                    } else {
                        final RelativeVector3d coords = args.<RelativeVector3d>getOne("coordinates").get();
                        final Transform transform = target.getTransform();
                        World world = args.<WorldProperties>getOne(CommandHelper.WORLD_KEY)
                                .flatMap(p -> Lantern.getServer().getWorld(p.getUniqueId())).orElse(transform.getWorld());
                        Vector3d position = coords.applyToValue(transform.getPosition());

                        final Optional<RelativeDouble> optYRot = args.getOne("y-rot");
                        if (optYRot.isPresent()) {
                            final Vector3d rot = transform.getRotation();
                            double xRot = args.<RelativeDouble>getOne("x-rot").get().applyToValue(rot.getX());
                            double yRot = args.<RelativeDouble>getOne("y-rot").get().applyToValue(rot.getY());
                            double zRot = rot.getZ();
                            target.setLocationAndRotation(new Location<>(world, position),
                                    new Vector3d(xRot, yRot, zRot));
                        } else {
                            target.setLocation(new Location<>(world, position));
                        }
                        src.sendMessage(t("commands.tp.success.position", target.getName(),
                                formatDouble(position.getX()),
                                formatDouble(position.getY()),
                                formatDouble(position.getZ()),
                                world.getName()));
                    }
                    return CommandResult.success();
                });
    }

    private static String formatDouble(double value) {
        return Double.toString(Math.round(value * 1000.0) / 1000.0);
    }
}
