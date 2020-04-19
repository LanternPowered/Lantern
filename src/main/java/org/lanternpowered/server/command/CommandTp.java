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
import org.spongepowered.math.vector.Vector3d;

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
