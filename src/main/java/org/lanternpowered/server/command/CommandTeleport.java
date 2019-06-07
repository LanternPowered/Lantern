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

import static org.spongepowered.api.util.SpongeApiTranslationHelper.t;

import org.lanternpowered.server.command.element.GenericArguments2;
import org.lanternpowered.server.command.element.RelativeDouble;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.math.vector.Vector3d;

public final class CommandTeleport extends CommandProvider {

    public CommandTeleport() {
        super(2, "teleport");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        // TODO: Replace with entity selector
                        GenericArguments.player(Text.of("target")),
                        GenericArguments.flags()
                                .valueFlag(GenericArguments.world(CommandHelper.WORLD_KEY), "-world", "w")
                                .buildWith(GenericArguments.none()),
                        GenericArguments.vector3d(Text.of("position")),
                        GenericArguments.optional(GenericArguments.seq(
                                        GenericArguments2.relativeDoubleNum(Text.of("y-rot")),
                                        GenericArguments2.relativeDoubleNum(Text.of("x-rot"))
                                )
                        )
                )
                .executor((src, args) -> {
                    // TODO: Replace with selected entities
                    final Entity target = args.<Entity>getOne("target").get();
                    final World world = CommandHelper.getWorld(src, args);
                    final Location location = new Location<>(world, args.<Vector3d>getOne("position").get());

                    if (args.hasAny("y-rot")) {
                        RelativeDouble yRot = args.<RelativeDouble>getOne("y-rot").get();
                        RelativeDouble xRot = args.<RelativeDouble>getOne("x-rot").get();

                        boolean rel = yRot.isRelative() || xRot.isRelative();
                        if (rel && !(src instanceof Locatable)) {
                            throw new CommandException(
                                    t("Relative rotation specified but source does not have a rotation."));
                        }

                        double xRot0 = xRot.getValue();
                        double yRot0 = yRot.getValue();
                        double zRot0 = 0;

                        // Only entities have a rotation, but don't throw errors if the src
                        // is locatable, just handle it then as absolute
                        if (src instanceof Entity) {
                            final Vector3d rot = ((Entity) src).getRotation();
                            xRot0 = xRot.applyToValue(rot.getX());
                            yRot0 = yRot.applyToValue(rot.getY());
                            zRot0 = rot.getZ();
                        }

                        target.setLocationAndRotation(location, new Vector3d(xRot0, yRot0, zRot0));
                    } else {
                        target.setLocation(location);
                    }

                    src.sendMessage(t("commands.teleport.success.coordinates",
                            location.getX(), location.getY(), location.getZ()));
                    return CommandResult.success();
                });
    }
}
