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

import org.lanternpowered.server.block.BlockProperties;
import org.lanternpowered.server.block.property.FlammableInfo;
import org.spongepowered.api.block.BlockSoundGroup;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.type.InstrumentType;
import org.spongepowered.api.data.type.Matter;

import java.util.function.Function;

public final class PropertyProviders {

    public static PropertyProviderCollection matter(Matter constant) {
        return PropertyProviderCollection.constant(BlockProperties.MATTER, constant);
    }

    public static PropertyProviderCollection matter(PropertyProvider<Matter> provider) {
        return PropertyProviderCollection.of(BlockProperties.MATTER, provider);
    }

    public static PropertyProviderCollection hardness(double constant) {
        return PropertyProviderCollection.constant(BlockProperties.HARDNESS, constant);
    }

    public static PropertyProviderCollection hardness(PropertyProvider<Double> provider) {
        return PropertyProviderCollection.of(BlockProperties.HARDNESS, provider);
    }

    public static PropertyProviderCollection blastResistance(double constant) {
        return PropertyProviderCollection.constant(BlockProperties.BLAST_RESISTANCE, constant);
    }

    public static PropertyProviderCollection blastResistance(PropertyProvider<Double> provider) {
        return PropertyProviderCollection.of(BlockProperties.BLAST_RESISTANCE, provider);
    }

    public static PropertyProviderCollection unbreakable(boolean constant) {
        return PropertyProviderCollection.constant(BlockProperties.IS_UNBREAKABLE, constant);
    }

    public static PropertyProviderCollection unbreakable(PropertyProvider<Boolean> provider) {
        return PropertyProviderCollection.of(BlockProperties.IS_UNBREAKABLE, provider);
    }

    public static PropertyProviderCollection flammable(boolean constant) {
        return PropertyProviderCollection.constant(BlockProperties.IS_FLAMMABLE, constant);
    }

    public static PropertyProviderCollection flammable(PropertyProvider<Boolean> provider) {
        return PropertyProviderCollection.of(BlockProperties.IS_FLAMMABLE, provider);
    }

    public static PropertyProviderCollection lightEmission(double constant) {
        return PropertyProviderCollection.constant(BlockProperties.LIGHT_EMISSION, constant);
    }

    public static PropertyProviderCollection lightEmission(PropertyProvider<Double> provider) {
        return PropertyProviderCollection.of(BlockProperties.LIGHT_EMISSION, provider);
    }

    public static PropertyProviderCollection replaceable(boolean constant) {
        return PropertyProviderCollection.constant(BlockProperties.IS_REPLACEABLE, constant);
    }

    public static PropertyProviderCollection replaceable(PropertyProvider<Boolean> provider) {
        return PropertyProviderCollection.of(BlockProperties.IS_REPLACEABLE, provider);
    }

    public static PropertyProviderCollection solidCube(boolean constant) {
        return PropertyProviderCollection.constant(BlockProperties.IS_SOLID_CUBE, constant);
    }

    public static PropertyProviderCollection solidCube(PropertyProvider<Boolean> provider) {
        return PropertyProviderCollection.of(BlockProperties.IS_SOLID_CUBE, provider);
    }

    public static PropertyProviderCollection solidSide(boolean constant) {
        return PropertyProviderCollection.constant(BlockProperties.IS_SOLID_SIDE, constant);
    }

    public static PropertyProviderCollection solidSide(PropertyProvider<Boolean> provider) {
        return PropertyProviderCollection.of(BlockProperties.IS_SOLID_SIDE, provider);
    }

    public static PropertyProviderCollection solidMaterial(boolean constant) {
        return PropertyProviderCollection.constant(BlockProperties.IS_SOLID_MATERIAL, constant);
    }

    public static PropertyProviderCollection solidMaterial(PropertyProvider<Boolean> provider) {
        return PropertyProviderCollection.of(BlockProperties.IS_SOLID_MATERIAL, provider);
    }

    public static PropertyProviderCollection passable(boolean constant) {
        return PropertyProviderCollection.constant(BlockProperties.IS_PASSABLE, constant);
    }

    public static PropertyProviderCollection passable(PropertyProvider<Boolean> provider) {
        return PropertyProviderCollection.of(BlockProperties.IS_PASSABLE, provider);
    }

    public static PropertyProviderCollection gravityAffected(boolean constant) {
        return PropertyProviderCollection.constant(BlockProperties.IS_GRAVITY_AFFECTED, constant);
    }

    public static PropertyProviderCollection gravityAffected(PropertyProvider<Boolean> provider) {
        return PropertyProviderCollection.of(BlockProperties.IS_GRAVITY_AFFECTED, provider);
    }

    public static PropertyProviderCollection statisticsTracked(boolean constant) {
        return PropertyProviderCollection.constant(BlockProperties.HAS_STATISTICS_TRACKING, constant);
    }

    public static PropertyProviderCollection statisticsTracked(PropertyProvider<Boolean> provider) {
        return PropertyProviderCollection.of(BlockProperties.HAS_STATISTICS_TRACKING, provider);
    }

    public static PropertyProviderCollection surrogate(boolean constant) {
        return PropertyProviderCollection.constant(BlockProperties.IS_SURROGATE, constant);
    }

    public static PropertyProviderCollection surrogate(PropertyProvider<Boolean> provider) {
        return PropertyProviderCollection.of(BlockProperties.IS_SURROGATE, provider);
    }

    public static PropertyProviderCollection fullBlockSelectionBox(boolean constant) {
        return PropertyProviderCollection.constant(BlockProperties.HAS_FULL_BLOCK_SELECTION_BOX, constant);
    }

    public static PropertyProviderCollection fullBlockSelectionBox(PropertyProvider<Boolean> provider) {
        return PropertyProviderCollection.of(BlockProperties.HAS_FULL_BLOCK_SELECTION_BOX, provider);
    }

    public static PropertyProviderCollection flammableInfo(int encouragement, int flammability) {
        return flammableInfo(new FlammableInfo(encouragement, flammability));
    }

    public static PropertyProviderCollection flammableInfo(FlammableInfo flammableInfo) {
        return PropertyProviderCollection.builder()
                .addConstant(BlockProperties.IS_FLAMMABLE, true)
                .addConstant(BlockProperties.FLAMMABLE_INFO, flammableInfo)
                .build();
    }

    public static PropertyProviderCollection flammableInfo(PropertyProvider<FlammableInfo> provider) {
        final PropertyProvider<Boolean> flammableProvider;
        if (provider instanceof SimplePropertyProvider) {
            final Function<BlockState, FlammableInfo> function = ((SimplePropertyProvider<FlammableInfo>) provider).getFunction();
            flammableProvider = new SimplePropertyProvider<>(blockState -> function.apply(blockState) != null);
        } else {
            flammableProvider = (blockState, location, face) -> provider.get(blockState, location, face) != null;
        }
        return PropertyProviderCollection.builder()
                .add(BlockProperties.IS_FLAMMABLE, flammableProvider)
                .add(BlockProperties.FLAMMABLE_INFO, provider)
                .build();
    }

    public static PropertyProviderCollection instrument(InstrumentType constant) {
        return PropertyProviderCollection.constant(BlockProperties.REPRESENTED_INSTRUMENT, constant);
    }
    
    public static PropertyProviderCollection blockSoundGroup(BlockSoundGroup blockSoundGroup) {
        return PropertyProviderCollection.constant(BlockProperties.BLOCK_SOUND_GROUP, blockSoundGroup);
    }
}
