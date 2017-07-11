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
package org.lanternpowered.server.block.provider.property;

import static org.lanternpowered.server.block.provider.property.PropertyProviders.blastResistance;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.flammable;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.flammableInfo;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.gravityAffected;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.hardness;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.instrument;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.lightAbsorption;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.lightEmission;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.matter;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.passable;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.pushBehavior;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.replaceable;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.slipperiness;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.solidCube;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.solidSide;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.statisticsTracked;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.surrogateBlock;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.unbreakable;

import org.lanternpowered.server.block.property.PushBehavior;
import org.lanternpowered.server.game.registry.type.data.InstrumentTypeRegistryModule;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.data.type.InstrumentTypes;

/**
 * Some presents of {@link PropertyProviderCollection}s that can be
 * shared across block types.
 */
public final class PropertyProviderCollections {

    // TODO: Add map colors

    /**
     * The default {@link PropertyProviderCollection} for
     * {@link MatterProperty.Matter#SOLID} materials.
     */
    public static final PropertyProviderCollection DEFAULT = PropertyProviderCollection.builder()
            .add(matter(MatterProperty.Matter.SOLID))
            .add(flammable(false))
            .add(hardness(1.0))
            .add(blastResistance(5.0))
            .add(lightEmission(0))
            .add(passable(false))
            .add(solidCube(true))
            .add(solidSide(true))
            .add(gravityAffected(false))
            .add(unbreakable(false))
            .add(replaceable(false))
            .add(surrogateBlock(false))
            .add(statisticsTracked(true))
            .add(slipperiness(0.6))
            .add(lightAbsorption(15))
            .add(instrument(InstrumentTypes.HARP))
            .add(pushBehavior(PushBehavior.PUSH))
            .build();

    /**
     * A {@link PropertyProviderCollection} that make
     * the material passable. Passable means that
     * there are no collisions with the block.
     */
    public static final PropertyProviderCollection PASSABLE = PropertyProviderCollection.builder()
            .add(passable(true))
            .add(solidCube(false))
            .add(solidSide(false))
            .build();

    /**
     * A {@link PropertyProviderCollection} that makes
     * the material unbreakable from every source in
     * the game except creative breaking. Natural breaking,
     * explosions, etc. won't affect the block.
     */
    public static final PropertyProviderCollection UNBREAKABLE = PropertyProviderCollection.builder()
            .add(unbreakable(true))
            .add(hardness(-1.0))
            .add(blastResistance(6000000.0))
            .add(statisticsTracked(false))
            .add(pushBehavior(PushBehavior.BLOCK))
            .build();

    /**
     * A {@link PropertyProviderCollection} that makes it possible
     * to break the target block instantly.
     */
    public static final PropertyProviderCollection INSTANT_BROKEN = PropertyProviderCollection.builder()
            .add(hardness(0.0))
            .add(blastResistance(0.0))
            .add(pushBehavior(PushBehavior.REPLACE))
            .build();

    /**
     * The default {@link PropertyProviderCollection} for
     * {@link MatterProperty.Matter#GAS} materials.
     */
    public static final PropertyProviderCollection DEFAULT_GAS = DEFAULT.toBuilder()
            .add(matter(MatterProperty.Matter.GAS))
            .add(replaceable(true))
            .add(pushBehavior(PushBehavior.REPLACE))
            .add(lightAbsorption(0)) // Gases don't block light by default
            .add(PASSABLE)
            .build();

    /**
     * The default {@link PropertyProviderCollection} for
     * {@link MatterProperty.Matter#LIQUID} materials.
     */
    public static final PropertyProviderCollection DEFAULT_LIQUID = DEFAULT.toBuilder()
            .add(matter(MatterProperty.Matter.LIQUID))
            .add(replaceable(true))
            .add(pushBehavior(PushBehavior.REPLACE))
            .add(PASSABLE)
            .build();

    // Start material based collections,
    // see: http://minecraft.gamepedia.com/Materials

    /**
     * The {@link PropertyProviderCollection} for air.
     */
    public static final PropertyProviderCollection AIR = DEFAULT_GAS.toBuilder()
            .build();

    /**
     * The {@link PropertyProviderCollection} for grass blocks.
     */
    public static final PropertyProviderCollection GRASS = DEFAULT.toBuilder()
            .build();

    /**
     * The {@link PropertyProviderCollection} for ground/dirt blocks.
     */
    public static final PropertyProviderCollection GROUND = DEFAULT.toBuilder()
            .build();

    /**
     * The {@link PropertyProviderCollection} for wooden blocks.
     */
    public static final PropertyProviderCollection WOOD = DEFAULT.toBuilder()
            .add(flammableInfo(5, 20)) // The default flammable settings
            .add(instrument(InstrumentTypes.BASS_ATTACK))
            .build();

    /**
     * The {@link PropertyProviderCollection} for stone/rock blocks.
     */
    // TODO: Requires tool
    public static final PropertyProviderCollection STONE = DEFAULT.toBuilder()
            .add(instrument(InstrumentTypes.BASS_DRUM))
            .build();

    /**
     * The {@link PropertyProviderCollection} for mineral blocks.
     */
    // TODO: Requires tool
    public static final PropertyProviderCollection MINERAL = DEFAULT.toBuilder()
            .build();

    /**
     * The {@link PropertyProviderCollection} for iron blocks.
     */
    public static final PropertyProviderCollection IRON = MINERAL;

    /**
     * The {@link PropertyProviderCollection} for anvil blocks.
     */
    public static final PropertyProviderCollection ANVIL = IRON.toBuilder()
            .add(pushBehavior(PushBehavior.BLOCK))
            .build();

    /**
     * The {@link PropertyProviderCollection} for water blocks.
     */
    public static final PropertyProviderCollection WATER = DEFAULT_LIQUID.toBuilder()
            .add(lightAbsorption(3))
            .build();

    /**
     * The {@link PropertyProviderCollection} for lava blocks.
     */
    public static final PropertyProviderCollection LAVA = DEFAULT_LIQUID.toBuilder()
            .build();

    /**
     * The {@link PropertyProviderCollection} for leaves blocks.
     */
    public static final PropertyProviderCollection LEAVES = DEFAULT.toBuilder()
            .add(flammableInfo(30, 60)) // The default flammable settings
            .build();

    /**
     * The {@link PropertyProviderCollection} for plant blocks.
     */
    public static final PropertyProviderCollection PLANT = DEFAULT.toBuilder()
            .add(flammableInfo(60, 100)) // The default flammable settings
            .add(pushBehavior(PushBehavior.REPLACE))
            .add(PASSABLE)
            .build();

    /**
     * The {@link PropertyProviderCollection} for instant broken plant blocks.
     */
    public static final PropertyProviderCollection INSTANT_BROKEN_PLANT = PLANT.toBuilder()
            .add(INSTANT_BROKEN)
            .build();

    /**
     * The {@link PropertyProviderCollection} for replaceable plant blocks.
     */
    public static final PropertyProviderCollection REPLACEABLE_PLANT = PLANT.toBuilder()
            .add(replaceable(true))
            .build();

    /**
     * The {@link PropertyProviderCollection} for instant broken replaceable plant blocks.
     */
    public static final PropertyProviderCollection INSTANT_BROKEN_REPLACEABLE_PLANT = REPLACEABLE_PLANT.toBuilder()
            .add(INSTANT_BROKEN)
            .build();

    /**
     * The {@link PropertyProviderCollection} for sponge blocks.
     */
    public static final PropertyProviderCollection SPONGE = DEFAULT.toBuilder()
            .build();

    /**
     * The {@link PropertyProviderCollection} for cloth/wool blocks.
     */
    public static final PropertyProviderCollection CLOTH = DEFAULT.toBuilder()
            .add(flammableInfo(30, 60)) // The default flammable settings
            .build();

    /**
     * The {@link PropertyProviderCollection} for fire blocks.
     */
    public static final PropertyProviderCollection FIRE = DEFAULT.toBuilder()
            .add(INSTANT_BROKEN)
            .add(PASSABLE)
            .build();

    /**
     * The {@link PropertyProviderCollection} for sand blocks.
     */
    public static final PropertyProviderCollection SAND = DEFAULT.toBuilder()
            .add(instrument(InstrumentTypes.SNARE))
            .build();

    /**
     * The {@link PropertyProviderCollection} for non solid blocks.
     *
     * TODO: This name doesn't feel right
     */
    public static final PropertyProviderCollection NON_SOLID = DEFAULT.toBuilder()
            .add(pushBehavior(PushBehavior.REPLACE))
            .add(PASSABLE)
            .build();

    /**
     * The {@link PropertyProviderCollection} for glass blocks.
     */
    // TODO: Breakable in adventure
    public static final PropertyProviderCollection GLASS = DEFAULT.toBuilder()
            .add(instrument(InstrumentTypes.HIGH_HAT))
            .build();

    /**
     * The {@link PropertyProviderCollection} for carpet blocks.
     */
    public static final PropertyProviderCollection CARPET = CLOTH.toBuilder()
            .add(PASSABLE)
            .build();

    /**
     * The {@link PropertyProviderCollection} for TNT blocks.
     */
    public static final PropertyProviderCollection TNT = DEFAULT.toBuilder()
            .add(flammableInfo(15, 100))
            .build();

    /**
     * The {@link PropertyProviderCollection} for gourd blocks.
     */
    public static final PropertyProviderCollection GOURD = DEFAULT.toBuilder()
            .build();

    /**
     * The {@link PropertyProviderCollection} for web blocks.
     */
    public static final PropertyProviderCollection WEB = DEFAULT.toBuilder()
            .add(pushBehavior(PushBehavior.REPLACE))
            .add(lightAbsorption(1))
            .add(PASSABLE)
            .build();

    /**
     * The {@link PropertyProviderCollection} for clay blocks.
     */
    public static final PropertyProviderCollection CLAY = DEFAULT.toBuilder()
            .add(instrument(InstrumentTypeRegistryModule.get().getById("minecraft:flute").get()))
            .build();

    /**
     * The {@link PropertyProviderCollection} for ice blocks.
     */
    // TODO: Breakable in adventure
    public static final PropertyProviderCollection ICE = DEFAULT.toBuilder()
            .add(lightAbsorption(3))
            .add(slipperiness(0.98))
            .build();

    /**
     * The {@link PropertyProviderCollection} for packed ice blocks.
     */
    // TODO: Breakable in adventure
    public static final PropertyProviderCollection PACKED_ICE = ICE.toBuilder()
            .add(instrument(InstrumentTypeRegistryModule.get().getById("minecraft:chime").get()))
            .build();

    /**
     * The {@link PropertyProviderCollection} for barrier blocks.
     */
    public static final PropertyProviderCollection BARRIER = UNBREAKABLE;

    private PropertyProviderCollections() {
    }
}
