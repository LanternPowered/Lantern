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
import com.google.common.collect.Lists;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder;

public final class CommandBorder extends CommandProvider {

    public CommandBorder() {
        super(2, "worldborder", "border");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .child(CommandSpec.builder()
                        .arguments(
                                GenericArguments.flags()
                                        .valueFlag(GenericArguments.world(CommandHelper.WORLD_KEY), "-world", "w")
                                        .buildWith(GenericArguments.none()),
                                GenericArguments.doubleNum(Text.of("distance")),
                                GenericArguments.optional(GenericArguments.integer(Text.of("time")))
                        )
                        .executor((src, args) -> {
                            World world = CommandHelper.getWorld(src, args);
                            WorldBorder border = world.getWorldBorder();
                            double oldDiameter = border.getDiameter();
                            double diameter = oldDiameter + args.<Double>getOne("distance").get();
                            int time = args.<Integer>getOne("time").orElse(0);
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
                        .build(), "add")
                .child(CommandSpec.builder()
                        .arguments(
                                GenericArguments.doubleNum(Text.of("distance")),
                                GenericArguments.optional(GenericArguments.integer(Text.of("time"))),
                                GenericArguments.optional(GenericArguments.world(Text.of("world")))
                        )
                        .executor((src, args) -> {
                            World world = CommandHelper.getWorld(src, args);
                            WorldBorder border = world.getWorldBorder();
                            double oldDiameter = border.getDiameter();
                            double diameter = args.<Double>getOne("distance").get();
                            int time = args.<Integer>getOne("time").orElse(0);
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
                        .build(), "set")
                .child(CommandSpec.builder()
                        .arguments(
                                GenericArguments.doubleNum(Text.of("x")),
                                GenericArguments.doubleNum(Text.of("z")),
                                GenericArguments.optional(GenericArguments.world(Text.of("world")))
                        )
                        .executor((src, args) -> {
                            World world = CommandHelper.getWorld(src, args);
                            WorldBorder border = world.getWorldBorder();
                            double x = args.<Double>getOne("x").get();
                            double z = args.<Double>getOne("z").get();
                            border.setCenter(x, z);
                            src.sendMessage(t("commands.worldborder.center.success", x, z));
                            return CommandResult.success();
                        })
                        .build(), "center")
                .child(CommandSpec.builder()
                        .child(CommandSpec.builder()
                                .arguments(
                                        GenericArguments.doubleNum(Text.of("damagePerBlock")),
                                        GenericArguments.optional(GenericArguments.world(Text.of("world")))
                                )
                                .executor((src, args) -> {
                                    World world = CommandHelper.getWorld(src, args);
                                    WorldBorder border = world.getWorldBorder();
                                    double oldDamage = border.getDamageAmount();
                                    double damage = args.<Double>getOne("damagePerBlock").get();
                                    border.setDamageAmount(damage);
                                    src.sendMessage(t("commands.worldborder.damage.buffer.success",
                                            String.format("%.1f", damage), String.format("%.1f", oldDamage)));
                                    return CommandResult.success();
                                })
                                .build(), "amount")
                        .child(CommandSpec.builder()
                                .arguments(
                                        GenericArguments.doubleNum(Text.of("distance")),
                                        GenericArguments.optional(GenericArguments.world(Text.of("world")))
                                )
                                .executor((src, args) -> {
                                    World world = CommandHelper.getWorld(src, args);
                                    WorldBorder border = world.getWorldBorder();
                                    double oldDistance = border.getDamageThreshold();
                                    double distance = args.<Double>getOne("distance").get();
                                    border.setDamageThreshold(args.<Double>getOne("distance").get());
                                    src.sendMessage(t("commands.worldborder.damage.amount.success",
                                            String.format("%.1f", distance), String.format("%.1f", oldDistance)));
                                    return CommandResult.success();
                                })
                                .build(), "buffer")
                        .build(), "damage")
                .child(CommandSpec.builder()
                        .children(ImmutableMap.of(
                                Lists.newArrayList("distance"), CommandSpec.builder()
                                        .arguments(
                                                GenericArguments.integer(Text.of("distance")),
                                                GenericArguments.optional(GenericArguments.world(Text.of("world"))))
                                        .executor((src, args) -> {
                                            World world = CommandHelper.getWorld(src, args);
                                            WorldBorder border = world.getWorldBorder();
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
                                            World world = CommandHelper.getWorld(src, args);
                                            WorldBorder border = world.getWorldBorder();
                                            int oldTime = border.getWarningTime();
                                            int time = args.<Integer>getOne("time").get();
                                            border.setWarningTime(time);
                                            src.sendMessage(t("commands.worldborder.warning.time.success", time, oldTime));
                                            return CommandResult.success();
                                        })
                                        .build()
                        ))
                        .build(), "warning")
                .child(CommandSpec.builder()
                        .arguments(
                                GenericArguments.optional(GenericArguments.world(Text.of("world")))
                        )
                        .executor((src, args) -> {
                            World world = CommandHelper.getWorld(src, args);
                            WorldBorder border = world.getWorldBorder();
                            double diameter = border.getDiameter();
                            src.sendMessage(t("commands.worldborder.get.success",
                                    String.format("%.1f", diameter), String.format("%.1f", diameter)));
                            return CommandResult.builder().queryResult((int) (diameter + 0.5)).build();
                        })
                        .build(), "get");
    }
}
