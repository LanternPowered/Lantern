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

import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.math.vector.Vector3d;

public final class CommandParticleEffect extends CommandProvider {

    public CommandParticleEffect() {
        super(2, "particleeffect", "particles");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.catalogedElement(Text.of("type"), ParticleType.class),
                        GenericArguments.vector3d(Text.of("position")),
                        GenericArguments.optional(GenericArguments.world(Text.of("world"))),
                        // TODO: Can we place the world arg after the position without that the parsing system complains
                        // TODO: Tab complaining is currently throwing errors, but it's a small bug in SpongeAPI
                        GenericArguments.flags()
                                .valueFlag(GenericArguments.integer(Text.of("quantity")), "-quantity", "q")
                                .valueFlag(GenericArguments.vector3d(Text.of("offset")), "-offset", "o")
                                .valueFlag(GenericArguments.vector3d(Text.of("velocity")), "-velocity", "v")
                                .valueFlag(GenericArguments.vector3d(Text.of("color")), "-color", "c")
                                .valueFlag(GenericArguments.doubleNum(Text.of("scale")), "-scale", "s")
                                .valueFlag(GenericArguments.catalogedElement(Text.of("note"), NotePitch.class), "-note", "n")
                                .valueFlag(GenericArguments.catalogedElement(Text.of("block"), BlockState.class), "-block", "b")
                                .valueFlag(GenericArguments.catalogedElement(Text.of("item"), ItemType.class), "-item", "i")
                                .valueFlag(GenericArguments.catalogedElement(Text.of("potion"), PotionEffectType.class), "-potion", "p")
                                .buildWith(GenericArguments.none()))
                .executor((src, args) -> {
                    final ParticleType particleType = args.<ParticleType>getOne("type").get();
                    final Vector3d position = args.<Vector3d>getOne("position").get();
                    final World world = args.<WorldProperties>getOne("world")
                            .map(props -> Sponge.getServer().getWorld(props.getUniqueId()).get())
                            .orElseGet(((Locatable) src)::getWorld);

                    final ParticleEffect.Builder builder = ParticleEffect.builder().type(particleType);
                    args.<Integer>getOne("quantity").ifPresent(builder::quantity);
                    args.<Vector3d>getOne("offset").ifPresent(builder::offset);
                    args.<Vector3d>getOne("velocity").ifPresent(builder::velocity);
                    args.<Vector3d>getOne("color").ifPresent(color ->
                            builder.option(ParticleOptions.COLOR, Color.of(color.toInt())));
                    args.<NotePitch>getOne("note").ifPresent(note ->
                            builder.option(ParticleOptions.NOTE, note));
                    args.<Double>getOne("scale").ifPresent(scale ->
                            builder.option(ParticleOptions.SCALE, scale));
                    args.<BlockState>getOne("block").ifPresent(blockState ->
                            builder.option(ParticleOptions.BLOCK_STATE, blockState));
                    args.<ItemType>getOne("item").ifPresent(item ->
                            builder.option(ParticleOptions.ITEM_STACK_SNAPSHOT, new LanternItemStack(item).createSnapshot()));
                    args.<PotionEffectType>getOne("potion").ifPresent(type ->
                            builder.option(ParticleOptions.POTION_EFFECT_TYPE, type));

                    world.spawnParticles(builder.build(), position);
                    src.sendMessage(t("Successfully spawned the particle %s", particleType.getName()));
                    return CommandResult.success();
                });
    }
}
