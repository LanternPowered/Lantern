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
package org.lanternpowered.server.world.gen.debug;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.world.gen.LanternGeneratorType;
import org.lanternpowered.server.world.gen.LanternWorldGenerator;
import org.lanternpowered.server.world.gen.SingleBiomeGenerator;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.gen.WorldGenerator;

public final class DebugGeneratorType extends LanternGeneratorType {

    public DebugGeneratorType(CatalogKey key) {
        super(key);
    }

    @Override
    public WorldGenerator createGenerator(World world) {
        return new LanternWorldGenerator(world, new SingleBiomeGenerator(BiomeTypes.PLAINS),
                new DebugGenerationPopulator(Lantern.getGame().getRegistry()));
    }
}
