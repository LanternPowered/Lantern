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
import static org.lanternpowered.server.block.provider.property.PropertyProviders.solidMaterial;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.solidSide;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.statisticsTracked;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.surrogate;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.unbreakable;

import org.spongepowered.api.data.type.Matter;

/**
 * Some presents of {@link PropertyProviderCollection}s that can be
 * shared across block types.
 */
public final class PropertyProviderCollections {

    public static final PropertyProviderCollection DEFAULT = PropertyProviderCollection.builder()
            .add(matter(Matter.SOLID))
            .add(flammable(false))
            .add(hardness(1.0))
            .add(blastResistance(5.0))
            .add(lightEmission(0))
            .add(passable(false))
            .add(gravityAffected(false))
            .add(unbreakable(false))
            .add(replaceable(false))
            .add(surrogate(false))
            .add(statisticsTracked(true))
            .add(solidMaterial(true))
            .build();

    public static final PropertyProviderCollection PASSABLE = PropertyProviderCollection.builder()
            .add(passable(true))
            .add(solidCube(false))
            .add(solidSide(false))
            .add(solidMaterial(false))
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
            .add(matter(Matter.GAS))
            .add(solidMaterial(false))
            .add(replaceable(true))
            .add(PASSABLE)
            .build();

    public static final PropertyProviderCollection DEFAULT_LIQUID = DEFAULT.toBuilder()
            .add(matter(Matter.LIQUID))
            .add(solidMaterial(false))
            .add(replaceable(true))
            .add(PASSABLE)
            .build();

    private PropertyProviderCollections() {
    }
}
