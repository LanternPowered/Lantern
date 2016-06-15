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

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import org.lanternpowered.server.command.element.GenericArguments2;
import org.lanternpowered.server.effect.particle.LanternParticleType;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnParticle;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.StartsWithPredicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public final class CommandParticle extends CommandProvider {

    public CommandParticle() {
        super(2, "particle");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.catalogedElement(Text.of("type"), ParticleType.class),
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
                                    return Arrays.asList("normal", "force").stream()
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
                    LanternParticleType particleType = args.<LanternParticleType>getOne("type").get();
                    int particleId = particleType.getInternalId();
                    Vector3f position = args.<Vector3d>getOne("position").get().toFloat();
                    Vector3f offset = args.<Vector3d>getOne("offset").get().toFloat();
                    float speed = args.<Double>getOne("speed").get().floatValue();
                    int count = args.<Integer>getOne("count").orElse(1);
                    boolean longDistance = args.<String>getOne("mode").map(mode -> mode.equalsIgnoreCase("force")).orElse(false);
                    int[] params = args.<int[]>getOne("params").orElse(new int[0]);
                    LanternWorld world = (LanternWorld) CommandHelper.getWorld(src, args);

                    // TODO: Make this not hardcoded
                    int dataLength = 0;
                    if (particleType == ParticleTypes.BLOCK_CRACK || particleType == ParticleTypes.BLOCK_DUST) {
                        dataLength = 1;
                    } else if (particleType == ParticleTypes.ITEM_CRACK) {
                        dataLength = 2;
                    }

                    if (params.length != dataLength) {
                        throw new CommandException(t("Invalid parameters (%s), length mismatch (got %s, expected %s) for the particle type %s",
                                Arrays.toString(params), params.length, dataLength, particleType.getId()));
                    }

                    MessagePlayOutSpawnParticle message = new MessagePlayOutSpawnParticle(
                            particleId, position, offset, speed, count, params, longDistance);
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
