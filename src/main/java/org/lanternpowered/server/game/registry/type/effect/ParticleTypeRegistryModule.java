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
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.data.NotePitchRegistryModule;
import org.lanternpowered.server.game.registry.type.item.FireworkShapeRegistryModule;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.NotePitches;
import org.spongepowered.api.effect.particle.ParticleOption;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Direction;

import java.util.Collections;
import java.util.Map;
import java.util.OptionalInt;

@RegistrationDependency({ ParticleOptionRegistryModule.class, NotePitchRegistryModule.class, BlockRegistryModule.class,
        ItemRegistryModule.class, PotionEffectTypeRegistryModule.class, FireworkShapeRegistryModule.class })
public final class ParticleTypeRegistryModule extends PluginCatalogRegistryModule<ParticleType> {

    public ParticleTypeRegistryModule() {
        super(ParticleTypes.class);
    }

    @Override
    public void registerDefaults() {
        this.registerParticle(16, "ambient_mob_spell", false, ImmutableMap.of(
                ParticleOptions.COLOR, Color.BLACK));
        this.registerParticle(20, "angry_villager", false);
        this.registerParticle(35, "barrier", false);
        this.registerParticle(37, "block_crack", true, ImmutableMap.of(
                ParticleOptions.BLOCK_STATE, BlockTypes.STONE.getDefaultState(),
                ParticleOptions.ITEM_STACK_SNAPSHOT, new LanternItemStack(BlockTypes.STONE).createSnapshot()));
        this.registerParticle(38, "block_dust", true, ImmutableMap.of(
                ParticleOptions.BLOCK_STATE, BlockTypes.STONE.getDefaultState(),
                ParticleOptions.ITEM_STACK_SNAPSHOT, new LanternItemStack(BlockTypes.STONE).createSnapshot()));
        this.registerEffect("break_block", ImmutableMap.of(
                ParticleOptions.BLOCK_STATE, BlockTypes.STONE.getDefaultState(),
                ParticleOptions.ITEM_STACK_SNAPSHOT, new LanternItemStack(BlockTypes.STONE).createSnapshot()));
        this.registerParticle(29, "cloud", true);
        this.registerParticle(9, "critical_hit", true);
        this.registerParticle(44, "damage_indicator", true);
        this.registerParticle(42, "dragon_breath", true);
        this.registerEffect("dragon_breath_attack", ImmutableMap.of());
        this.registerParticle(19, "drip_lava", false);
        this.registerParticle(18, "drip_water", false);
        this.registerParticle(25, "enchanting_glyphs", true);
        this.registerParticle(43, "end_rod", true);
        this.registerEffect("ender_teleport", ImmutableMap.of());
        this.registerParticle(0, "explosion", true);
        this.registerParticle(46, "falling_dust", false, ImmutableMap.of(
                ParticleOptions.BLOCK_STATE, BlockTypes.STONE.getDefaultState(),
                ParticleOptions.ITEM_STACK_SNAPSHOT, new LanternItemStack(BlockTypes.STONE).createSnapshot()));
        this.registerEffect("fertilizer", ImmutableMap.of(
                ParticleOptions.QUANTITY, 15));
        this.registerParticle(3, "fireworks_spark", true);
        this.registerEffect("fireworks", ImmutableMap.of(
                ParticleOptions.FIREWORK_EFFECTS, ImmutableList.of(
                        FireworkEffect.builder().color(Color.BLACK).build())));
        this.registerEffect("fire_smoke", ImmutableMap.of(
                ParticleOptions.DIRECTION, Direction.UP));
        this.registerParticle(26, "flame", true);
        this.registerParticle(28, "footstep", false);
        this.registerParticle(41, "guardian_appearance", false);
        this.registerParticle(21, "happy_villager", true);
        this.registerParticle(34, "heart", false);
        this.registerParticle(2, "huge_explosion", false);
        this.registerParticle(14, "instant_spell", true, ImmutableMap.of(
                ParticleOptions.SLOW_HORIZONTAL_VELOCITY, false));
        this.registerParticle(36, "item_crack", true, ImmutableMap.of(
                ParticleOptions.ITEM_STACK_SNAPSHOT, new LanternItemStack(BlockTypes.STONE).createSnapshot()));
        this.registerParticle(1, "large_explosion", false, ImmutableMap.of(
                ParticleOptions.SCALE, 1.0));
        this.registerParticle(12, "large_smoke", true);
        this.registerParticle(27, "lava", false);
        this.registerParticle(10, "magic_critical_hit", true);
        this.registerEffect("mobspawner_flames", ImmutableMap.of());
        this.registerParticle(15, "mob_spell", false, ImmutableMap.of(
                ParticleOptions.COLOR, Color.BLACK));
        this.registerParticle(23, "note", false, ImmutableMap.of(
                ParticleOptions.NOTE, NotePitches.F_SHARP0));
        this.registerParticle(24, "portal", true);
        this.registerParticle(30, "redstone_dust", false, ImmutableMap.of(
                ParticleOptions.COLOR, Color.RED));
        this.registerParticle(33, "slime", false);
        this.registerParticle(11, "smoke", true);
        this.registerParticle(31, "snowball", false);
        this.registerParticle(32, "snow_shovel", true);
        this.registerParticle(13, "spell", true, ImmutableMap.of(
                ParticleOptions.SLOW_HORIZONTAL_VELOCITY, false));
        this.registerEffect("splash_potion", ImmutableMap.of(
                ParticleOptions.POTION_EFFECT_TYPE, PotionEffectTypes.NIGHT_VISION));
        this.registerParticle(7, "suspended", false);
        this.registerParticle(8, "suspended_depth", false);
        this.registerParticle(45, "sweep_attack", false, ImmutableMap.of(
                ParticleOptions.SCALE, 1.0));
        this.registerParticle(22, "town_aura", true);
        this.registerParticle(4, "water_bubble", true);
        this.registerParticle(39, "water_drop", false);
        this.registerParticle(4, "water_splash", true);
        this.registerParticle(5, "water_wake", true);
        this.registerParticle(17, "witch_spell", true, ImmutableMap.of(
                ParticleOptions.SLOW_HORIZONTAL_VELOCITY, false));
        // Is not exposed in the api, since it doesn't do anything
        this.registerParticle(40, "item_take", false);
    }

    private void registerParticle(int internalType, String id, boolean velocity) {
        this.registerParticle(internalType, id, velocity, Collections.emptyMap());
    }

    private void registerParticle(int internalType, String id, boolean velocity,
            Map<ParticleOption<?>, Object> extraOptions) {
        final ImmutableMap.Builder<ParticleOption<?>, Object> options = ImmutableMap.builder();
        options.put(ParticleOptions.OFFSET, Vector3d.ZERO);
        options.put(ParticleOptions.QUANTITY, 1);
        if (velocity) {
            options.put(ParticleOptions.VELOCITY, Vector3d.ZERO);
        }
        options.putAll(extraOptions);
        this.registerEffect(id, OptionalInt.of(internalType), options.build());
    }

    private void registerEffect(String id, Map<ParticleOption<?>, Object> options) {
        this.registerEffect(id, OptionalInt.empty(), options);
    }

    private void registerEffect(String id, OptionalInt internalType, Map<ParticleOption<?>, Object> options) {
        this.register(new LanternParticleType("minecraft", id, id, internalType, options));
    }
}
