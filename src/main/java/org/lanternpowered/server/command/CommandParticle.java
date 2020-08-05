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
import org.lanternpowered.server.effect.particle.LanternParticleType;
import org.lanternpowered.server.entity.player.LanternPlayer;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnParticlePacket;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.StartsWithPredicate;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class CommandParticle extends CommandProvider {

    public CommandParticle() {
        super(2, "particle");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        new PatternMatchingCommandElement(Text.of("type")) {
                            @Override
                            protected Iterable<String> getChoices(CommandSource source) {
                                return Sponge.getGame().getRegistry().getAllOf(ParticleType.class).stream()
                                        .filter(type -> ((LanternParticleType) type).getInternalType() != null)
                                        .map(CatalogType::getKey)
                                        .map(ResourceKey::toString)
                                        .collect(Collectors.toList());
                            }

                            @Override
                            protected Object getValue(String choice) throws IllegalArgumentException {
                                final Optional<ParticleType> ret = Sponge.getGame().getRegistry()
                                        .getType(ParticleType.class, ResourceKey.resolve(choice));
                                if (!ret.isPresent() || ((LanternParticleType) ret.get()).getInternalType() == null) {
                                    throw new IllegalArgumentException("Invalid input " + choice + " was found");
                                }
                                return ret.get();
                            }
                        },
                        GenericArguments2.targetedVector3d(Text.of("position")),
                        // The default value should be 0 for x, y and z
                        GenericArguments2.vector3d(Text.of("offset"), Vector3d.ZERO),
                        GenericArguments2.doubleNum(Text.of("speed"), 1.0),
                        GenericArguments.optional(GenericArguments2.integer(Text.of("count"), 1)),
                        GenericArguments.optional(new CommandElement(Text.of("mode")) {
                            @Nullable
                            @Override
                            protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
                                return args.next();
                            }

                            @Override
                            public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
                                Optional<String> arg = args.nextIfPresent();
                                if (arg.isPresent()) {
                                    return Stream.of("normal", "force")
                                            .filter(new StartsWithPredicate(arg.get()))
                                            .collect(Collectors.toList());
                                }
                                return Collections.emptyList();
                            }
                        }),
                        GenericArguments.optional(GenericArguments.player(Text.of("player"))),
                        GenericArguments.optional(new CommandElement(Text.of("params")) {

                            @Nullable
                            @Override
                            protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
                                List<Integer> params = new ArrayList<>();
                                while (args.hasNext()) {
                                    String arg = args.next();
                                    try {
                                        params.add(Integer.parseInt(arg));
                                    } catch (NumberFormatException e) {
                                        throw args.createError(t("Expected an integer, but input '%s' was not", arg));
                                    }
                                }
                                return params.stream().mapToInt(i -> i).toArray();
                            }

                            @Override
                            public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
                                return Collections.emptyList();
                            }
                        })
                )
                .executor((src, args) -> {
                    final LanternParticleType particleType = args.<LanternParticleType>getOne("type").get();
                    final int particleId = particleType.getInternalType();
                    final Vector3f position = args.<Vector3d>getOne("position").get().toFloat();
                    final Vector3f offset = args.<Vector3d>getOne("offset").get().toFloat();
                    final float speed = args.<Double>getOne("speed").get().floatValue();
                    final int count = args.<Integer>getOne("count").orElse(1);
                    final boolean longDistance = args.<String>getOne("mode").map(mode -> mode.equalsIgnoreCase("force")).orElse(false);
                    final int[] params = args.<int[]>getOne("params").orElse(new int[0]);
                    final LanternWorld world = CommandHelper.getWorld(src, args);

                    final int dataLength;
                    if (particleType == ParticleTypes.BREAK_BLOCK ||
                            particleType == ParticleTypes.FALLING_DUST ||
                            particleType == ParticleTypes.BLOCK) {
                        dataLength = 1;
                    } else if (particleType == ParticleTypes.ITEM) {
                        dataLength = 2;
                    } else {
                        dataLength = 0;
                    }

                    if (params.length != dataLength) {
                        throw new CommandException(t("Invalid parameters (%s), length mismatch (got %s, expected %s) for the particle type %s",
                                Arrays.toString(params), params.length, dataLength, particleType.getKey()));
                    }

                    final SpawnParticlePacket message = new SpawnParticlePacket(
                            particleId, position, offset, speed, count, null, longDistance);
                    if (args.hasAny("player")) {
                        args.<LanternPlayer>getOne("player").get().getConnection().send(message);
                    } else {
                        for (LanternPlayer player : world.getRawPlayers()) {
                            player.getConnection().send(message);
                        }
                    }
                    src.sendMessage(t("commands.particle.success", particleType.getName(), count));
                    return CommandResult.success();
                });
    }
}
