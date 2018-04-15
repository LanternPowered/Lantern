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
import static org.lanternpowered.server.block.provider.property.PropertyProviders.gravityAffected;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.hardness;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.lightEmission;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.matter;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.passable;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.replaceable;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.solidCube;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.solidSide;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.statisticsTracked;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.surrogateBlock;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.unbreakable;

import org.spongepowered.api.data.property.block.MatterProperty;

/**
 * Some presents of {@link PropertyProviderCollection}s that can be
 * shared across block types.
 */
public final class PropertyProviderCollections {

    public static final PropertyProviderCollection DEFAULT = PropertyProviderCollection.builder()
            .add(matter(MatterProperty.Matter.SOLID))
            .add(flammable(false))
            .add(hardness(1.0))
            .add(blastResistance(5.0))
            .add(lightEmission(0))
            .add(passable(false))
            .add(gravityAffected(false))
            .add(unbreakable(false))
            .add(replaceable(false))
            .add(surrogateBlock(false))
            .add(statisticsTracked(true))
            .build();

    public static final PropertyProviderCollection PASSABLE = PropertyProviderCollection.builder()
            .add(passable(true))
            .add(solidCube(false))
            .add(solidSide(false))
            .build();

    public static final PropertyProviderCollection UNBREAKABLE = PropertyProviderCollection.builder()
            .add(unbreakable(true))
            .add(hardness(-1.0))
            .add(blastResistance(6000000.0))
            .add(statisticsTracked(false))
            .build();

    public static final PropertyProviderCollection INSTANT_BROKEN = PropertyProviderCollection.builder()
            .add(hardness(0.0))
            .add(blastResistance(0.0))
            .build();

    public static final PropertyProviderCollection DEFAULT_GAS = DEFAULT.toBuilder()
            .add(matter(MatterProperty.Matter.GAS))
            .add(replaceable(true))
            .add(PASSABLE)
            .build();

    public static final PropertyProviderCollection DEFAULT_LIQUID = DEFAULT.toBuilder()
            .add(matter(MatterProperty.Matter.LIQUID))
            .add(replaceable(true))
            .add(PASSABLE)
            .build();

    private PropertyProviderCollections() {
    }
}
