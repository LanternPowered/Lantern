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
import org.lanternpowered.server.command.element.DelegateCompleterElement;
import org.lanternpowered.server.command.element.GenericArguments2;
import org.lanternpowered.server.command.targeted.TargetedVector3dElement;
import org.lanternpowered.server.effect.particle.LanternParticleEffectBuilder;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.effect.particle.BlockParticle;
import org.spongepowered.api.effect.particle.ColoredParticle;
import org.spongepowered.api.effect.particle.ItemParticle;
import org.spongepowered.api.effect.particle.NoteParticle;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ResizableParticle;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.World;

public final class CommandParticleEffect extends CommandProvider {

    public CommandParticleEffect() {
        super(2, "particleeffect", "particles");
    }

    @Override
    public void completeSpec(CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.catalogedElement(Text.of("type"), ParticleType.class),
                        TargetedVector3dElement.of(Text.of("position"), 0),
                        DelegateCompleterElement.defaultValues(GenericArguments.integer(Text.of("count")), false, 1),
                        // TODO: Can we place the world arg after the position without that the parsing system complains
                        // TODO: Tab complaining is currently throwing errors, but it's a small bug in SpongeAPI
                        GenericArguments.optional(GenericArguments.world(Text.of("world"))),
                        GenericArguments.flags()
                                .valueFlag(DelegateCompleterElement.vector3d(GenericArguments.vector3d(Text.of("offset")), 0.0), "-offset", "o")
                                .valueFlag(DelegateCompleterElement.vector3d(GenericArguments.vector3d(Text.of("motion")), 0.0), "-motion", "m")
                                .valueFlag(GenericArguments2.color(Text.of("color"), Color.CYAN), "-color", "c")
                                .valueFlag(DelegateCompleterElement.defaultValues(GenericArguments.doubleNum(Text.of("size")), false, 1.0), "-size", "s")
                                .valueFlag(GenericArguments.catalogedElement(Text.of("note"), NotePitch.class), "-note", "n")
                                .valueFlag(GenericArguments.catalogedElement(Text.of("block"), BlockState.class), "-block", "b")
                                .valueFlag(GenericArguments.catalogedElement(Text.of("item"), ItemType.class), "-item", "i")
                                .buildWith(GenericArguments.none())
                )
                .executor((src, args) -> {
                    ParticleType particleType = args.<ParticleType>getOne("type").get();
                    Vector3d position = args.<Vector3d>getOne("position").get();
                    World world = CommandHelper.getWorld(src, args).getWorld().get();

                    ParticleEffect.ParticleBuilder builder;
                    if (particleType instanceof ParticleType.Colorable) {
                        builder = new LanternParticleEffectBuilder.Colorable();
                    } else if (particleType instanceof ParticleType.Resizable) {
                        builder = new LanternParticleEffectBuilder.Resizable();
                    } else if (particleType instanceof ParticleType.Item) {
                        builder = new LanternParticleEffectBuilder.Item();
                    } else if (particleType instanceof ParticleType.Block) {
                        builder = new LanternParticleEffectBuilder.Block();
                    } else {
                        builder = new LanternParticleEffectBuilder();
                    }

                    int count = args.<Integer>getOne("count").get();

                    builder.type(particleType);
                    builder.count(count);
                    args.<Vector3d>getOne("offset").ifPresent(builder::offset);
                    args.<Vector3d>getOne("motion").ifPresent(builder::motion);
                    args.<Color>getOne("color").ifPresent(color -> {
                        if (builder instanceof ColoredParticle.Builder) {
                            ((ColoredParticle.Builder) builder).color(color);
                        }
                    });
                    args.<NotePitch>getOne("note").ifPresent(note -> {
                        if (builder instanceof NoteParticle.Builder) {
                            ((NoteParticle.Builder) builder).note(note);
                        }
                    });
                    args.<Double>getOne("size").ifPresent(size -> {
                        if (builder instanceof ResizableParticle.Builder) {
                            ((ResizableParticle.Builder) builder).size(size.floatValue());
                        }
                    });
                    args.<BlockState>getOne("block").ifPresent(block -> {
                        if (builder instanceof BlockParticle.Builder) {
                            ((BlockParticle.Builder) builder).block(block);
                        }
                    });
                    args.<ItemType>getOne("item").ifPresent(item -> {
                        if (builder instanceof ItemParticle.Builder) {
                            // TODO
                        }
                    });

                    world.spawnParticles(builder.build(), position);
                    src.sendMessage(t("commands.particle.success", particleType.getName(), count));
                    return CommandResult.success();
                });
    }
}
