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
package org.lanternpowered.server.game.registry.type.effect;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.effect.particle.LanternParticleType;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.data.NotePitchRegistryModule;
import org.lanternpowered.server.game.registry.type.item.FireworkShapeRegistryModule;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.NotePitches;
import org.spongepowered.api.effect.particle.ParticleOption;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Direction;

import java.util.Collections;
import java.util.Map;
import java.util.OptionalInt;

@RegistrationDependency({ ParticleOptionRegistryModule.class, NotePitchRegistryModule.class, BlockRegistryModule.class,
        ItemRegistryModule.class, PotionEffectTypeRegistryModule.class, FireworkShapeRegistryModule.class })
public final class ParticleTypeRegistryModule extends DefaultCatalogRegistryModule<ParticleType> {

    private int internalId;

    public ParticleTypeRegistryModule() {
        super(ParticleTypes.class);
    }

    @Override
    public void registerDefaults() {
        registerParticle("ambient_entity_effect", false, ImmutableMap.of(
                ParticleOptions.COLOR, Color.BLACK));
        registerParticle("angry_villager", false);
        registerParticle("barrier", false);
        registerParticle("block", true, ImmutableMap.of(
                ParticleOptions.BLOCK_STATE, BlockTypes.STONE.getDefaultState(),
                ParticleOptions.ITEM_STACK_SNAPSHOT, new LanternItemStack(ItemTypes.STONE).createSnapshot()));
        registerParticle("bubble", true);
        registerParticle("cloud", true);
        registerParticle("critical_hit", true);
        registerParticle("damage_indicator", true);
        registerParticle("dragon_breath", true);
        registerParticle("dripping_lava", false);
        registerParticle("dripping_water", false);
        registerParticle("dust", true, ImmutableMap.of(
                ParticleOptions.COLOR, Color.RED,
                ParticleOptions.SCALE, 1.0));
        registerParticle("effect", true, ImmutableMap.of(
                ParticleOptions.SLOW_HORIZONTAL_VELOCITY, false));
        registerParticle("elder_guardian", false);
        registerParticle("enchanted_hit", true);
        registerParticle("enchanting_glyphs", true);
        registerParticle("end_rod", true);
        registerParticle("entity_effect", false, ImmutableMap.of(
                ParticleOptions.COLOR, Color.BLACK));
        registerParticle("explosion_emitter", true);
        registerParticle("explosion", false, ImmutableMap.of(
                ParticleOptions.SCALE, 1.0));
        registerParticle("falling_dust", true, ImmutableMap.of(
                ParticleOptions.BLOCK_STATE, BlockTypes.STONE.getDefaultState(),
                ParticleOptions.ITEM_STACK_SNAPSHOT, new LanternItemStack(ItemTypes.STONE).createSnapshot()));
        registerParticle("fireworks_spark", true);
        registerParticle("fishing", true);
        registerParticle("flame", true);
        registerParticle("happy_villager", true);
        registerParticle("heart", false);
        registerParticle("instant_effect", true, ImmutableMap.of(
                ParticleOptions.SLOW_HORIZONTAL_VELOCITY, false));
        registerParticle("item", true, ImmutableMap.of(
                ParticleOptions.ITEM_STACK_SNAPSHOT, new LanternItemStack(ItemTypes.STONE).createSnapshot()));
        registerParticle("item_slime", false);
        registerParticle("item_snowball", false);
        registerParticle("large_smoke", true);
        registerParticle("lava", false);
        registerParticle("mycelium", false);
        registerParticle("note", false, ImmutableMap.of(
                ParticleOptions.NOTE, NotePitches.F_SHARP0));
        registerParticle("snowball_poof", true);
        registerParticle("portal", true);
        registerParticle("rain_splash", true);
        registerParticle("smoke", true);
        registerParticle("spit", true);
        registerParticle("squid_ink", true);
        registerParticle("sweep_attack", false, ImmutableMap.of(
                ParticleOptions.SCALE, 1.0));
        registerParticle("totem_of_undying", true);
        registerParticle("underwater", false);
        registerParticle("water_splash", false);
        registerParticle("witch_magic", true, ImmutableMap.of(
                ParticleOptions.SLOW_HORIZONTAL_VELOCITY, false));
        registerParticle("bubble_pop", true);
        registerParticle("current_down", false);
        registerParticle("bubble_column_up", true);
        registerParticle("nautilus", true);
        registerParticle("dolphin_speed", false);

        // Extra effect types
        registerEffect("break_block", ImmutableMap.of(
                ParticleOptions.BLOCK_STATE, BlockTypes.STONE.getDefaultState(),
                ParticleOptions.ITEM_STACK_SNAPSHOT, new LanternItemStack(ItemTypes.STONE).createSnapshot()));
        registerEffect("break_eye_of_ender", ImmutableMap.of());
        registerEffect("break_splash_potion", ImmutableMap.of(
                ParticleOptions.POTION_EFFECT_TYPE, PotionEffectTypes.NIGHT_VISION));
        registerEffect("dragon_breath_attack", ImmutableMap.of());
        registerEffect("fertilizer", ImmutableMap.of(
                ParticleOptions.QUANTITY, 15));
        registerEffect("fireworks", ImmutableMap.of(
                ParticleOptions.FIREWORK_EFFECTS, ImmutableList.of(
                        FireworkEffect.builder().color(Color.BLACK).build())));
        registerEffect("fire_smoke", ImmutableMap.of(
                ParticleOptions.DIRECTION, Direction.UP));
        registerEffect("mobspawner_flames", ImmutableMap.of());
    }

    private void registerParticle(String id, boolean velocity) {
        registerParticle(id, velocity, Collections.emptyMap());
    }

    private void registerParticle(String id, boolean velocity,
            Map<ParticleOption<?>, Object> extraOptions) {
        final ImmutableMap.Builder<ParticleOption<?>, Object> options = ImmutableMap.builder();
        options.put(ParticleOptions.OFFSET, Vector3d.ZERO);
        options.put(ParticleOptions.QUANTITY, 1);
        if (velocity) {
            options.put(ParticleOptions.VELOCITY, Vector3d.ZERO);
        }
        options.putAll(extraOptions);
        registerEffect(id, OptionalInt.of(this.internalId++), options.build());
    }

    private void registerEffect(String id, Map<ParticleOption<?>, Object> options) {
        registerEffect(id, OptionalInt.empty(), options);
    }

    private void registerEffect(String id, OptionalInt internalType, Map<ParticleOption<?>, Object> options) {
        register(new LanternParticleType(CatalogKey.minecraft(id), internalType.isPresent() ? internalType.getAsInt() : null, options));
    }
}
