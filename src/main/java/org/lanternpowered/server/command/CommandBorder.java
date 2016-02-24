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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.lanternpowered.server.world.LanternWorldProperties;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.List;

public final class CommandBorder {

    public static final String PERMISSION = "minecraft.command.worldborder";

    public static CommandSpec create() {
        return CommandSpec.builder().children(ImmutableMap.<List<String>, CommandSpec>builder()
                .put(Lists.newArrayList("add"), CommandSpec.builder()
                        .arguments(
                                GenericArguments.doubleNum(Text.of("distance")),
                                GenericArguments.optional(GenericArguments.integer(Text.of("time"))),
                                GenericArguments.optional(GenericArguments.world(Text.of("world"))))
                        .executor((src, args) -> {
                            WorldProperties world = getWorld(src, args);
                            WorldBorder border = ((LanternWorldProperties) world).getWorld().get().getWorldBorder();
                            double oldDiameter = border.getDiameter();
                            double diameter = oldDiameter + args.<Double>getOne("distance").get();
                            int time = args.<Integer>getOne("value").orElse(0);
                            border.setDiameter(diameter, time);
                            if (time > 0) {
                                if (diameter < oldDiameter) {
                                    src.sendMessage(t("commands.worldborder.setSlowly.shrink.success",
                                            String.format("%.1f", diameter), String.format("%.1f", oldDiameter), time / 1000L));
                                } else {
                                    src.sendMessage(t("commands.worldborder.setSlowly.grow.success",
                                            String.format("%.1f", diameter), String.format("%.1f", oldDiameter), time / 1000L));
                                }
                            } else {
                                src.sendMessage(t("commands.worldborder.set.success",
                                        String.format("%.1f", diameter), String.format("%.1f", oldDiameter)));
                            }
                            return CommandResult.success();
                        })
                        .build())
                .put(Lists.newArrayList("set"), CommandSpec.builder()
                        .arguments(
                                GenericArguments.doubleNum(Text.of("distance")),
                                GenericArguments.optional(GenericArguments.integer(Text.of("time"))),
                                GenericArguments.optional(GenericArguments.world(Text.of("world"))))
                        .executor((src, args) -> {
                            WorldProperties world = getWorld(src, args);
                            WorldBorder border = ((LanternWorldProperties) world).getWorld().get().getWorldBorder();
                            double oldDiameter = border.getDiameter();
                            double diameter = args.<Double>getOne("distance").get();
                            int time = args.<Integer>getOne("value").orElse(0);
                            border.setDiameter(diameter, time);
                            if (time > 0) {
                                if (diameter < oldDiameter) {
                                    src.sendMessage(t("commands.worldborder.setSlowly.shrink.success",
                                            String.format("%.1f", diameter), String.format("%.1f", oldDiameter), time / 1000L));
                                } else {
                                    src.sendMessage(t("commands.worldborder.setSlowly.grow.success",
                                            String.format("%.1f", diameter), String.format("%.1f", oldDiameter), time / 1000L));
                                }
                            } else {
                                src.sendMessage(t("commands.worldborder.set.success",
                                        String.format("%.1f", diameter), String.format("%.1f", oldDiameter)));
                            }
                            return CommandResult.success();
                        })
                        .build())
                .put(Lists.newArrayList("center"), CommandSpec.builder()
                        .arguments(
                                GenericArguments.doubleNum(Text.of("x")),
                                GenericArguments.doubleNum(Text.of("z")),
                                GenericArguments.optional(GenericArguments.world(Text.of("world"))))
                        .executor((src, args) -> {
                            WorldProperties world = getWorld(src, args);
                            WorldBorder border = ((LanternWorldProperties) world).getWorld().get().getWorldBorder();
                            double x = args.<Double>getOne("x").get();
                            double z = args.<Double>getOne("z").get();
                            border.setCenter(x, z);
                            src.sendMessage(t("commands.worldborder.center.success", x, z));
                            return CommandResult.success();
                        })
                        .build())
                .put(Lists.newArrayList("damage"), CommandSpec.builder()
                        .children(ImmutableMap.of(
                                Lists.newArrayList("amount"), CommandSpec.builder()
                                        .arguments(
                                                GenericArguments.doubleNum(Text.of("damagePerBlock")),
                                                GenericArguments.optional(GenericArguments.world(Text.of("world"))))
                                        .executor((src, args) -> {
                                            WorldProperties world = getWorld(src, args);
                                            WorldBorder border = ((LanternWorldProperties) world).getWorld().get().getWorldBorder();
                                            double oldDamage = border.getDamageAmount();
                                            double damage = args.<Double>getOne("damagePerBlock").get();
                                            border.setDamageAmount(damage);
                                            src.sendMessage(t("commands.worldborder.damage.buffer.success",
                                                    String.format("%.1f", damage), String.format("%.1f", oldDamage)));
                                            return CommandResult.success();
                                        })
                                        .build(),
                                Lists.newArrayList("buffer"), CommandSpec.builder()
                                        .arguments(
                                                GenericArguments.doubleNum(Text.of("distance")),
                                                GenericArguments.optional(GenericArguments.world(Text.of("world"))))
                                        .executor((src, args) -> {
                                            WorldProperties world = getWorld(src, args);
                                            WorldBorder border = ((LanternWorldProperties) world).getWorld().get().getWorldBorder();
                                            double oldDistance = border.getDamageThreshold();
                                            double distance = args.<Double>getOne("distance").get();
                                            border.setDamageThreshold(args.<Double>getOne("distance").get());
                                            src.sendMessage(t("commands.worldborder.damage.amount.success",
                                                    String.format("%.1f", distance), String.format("%.1f", oldDistance)));
                                            return CommandResult.success();
                                        })
                                        .build()
                        ))
                        .build())
                .put(Lists.newArrayList("warning"), CommandSpec.builder()
                        .children(ImmutableMap.of(
                                Lists.newArrayList("distance"), CommandSpec.builder()
                                        .arguments(
                                                GenericArguments.integer(Text.of("distance")),
                                                GenericArguments.optional(GenericArguments.world(Text.of("world"))))
                                        .executor((src, args) -> {
                                            WorldProperties world = getWorld(src, args);
                                            WorldBorder border = ((LanternWorldProperties) world).getWorld().get().getWorldBorder();
                                            int oldDistance = border.getWarningDistance();
                                            int distance = args.<Integer>getOne("distance").get();
                                            border.setWarningDistance(distance);
                                            src.sendMessage(t("commands.worldborder.warning.distance.success", distance, oldDistance));
                                            return CommandResult.success();
                                        })
                                        .build(),
                                Lists.newArrayList("time"), CommandSpec.builder()
                                        .arguments(
                                                GenericArguments.integer(Text.of("time")),
                                                GenericArguments.optional(GenericArguments.world(Text.of("world"))))
                                        .executor((src, args) -> {
                                            WorldProperties world = getWorld(src, args);
                                            WorldBorder border = ((LanternWorldProperties) world).getWorld().get().getWorldBorder();
                                            int oldTime = border.getWarningTime();
                                            int time = args.<Integer>getOne("time").get();
                                            border.setWarningTime(time);
                                            src.sendMessage(t("commands.worldborder.warning.time.success", time, oldTime));
                                            return CommandResult.success();
                                        })
                                        .build()
                        ))
                        .build())
                .put(Lists.newArrayList("get"), CommandSpec.builder()
                        .arguments(
                                GenericArguments.optional(GenericArguments.world(Text.of("world"))))
                        .executor((src, args) -> {
                            WorldProperties world = getWorld(src, args);
                            WorldBorder border = ((LanternWorldProperties) world).getWorld().get().getWorldBorder();
                            double diameter = border.getDiameter();
                            src.sendMessage(t("commands.worldborder.get.success",
                                    String.format("%.1f", diameter), String.format("%.1f", diameter)));
                            return CommandResult.builder().queryResult((int) (diameter + 0.5)).build();
                        })
                        .build()).build())
                .permission(PERMISSION)
                .build();
    }

    private CommandBorder() {
    }

}
