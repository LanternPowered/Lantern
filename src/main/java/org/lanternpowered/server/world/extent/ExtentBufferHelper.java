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
package org.lanternpowered.server.world.extent;

import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.world.biome.BiomeRegistryModule;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.BiomeVolume;
import org.spongepowered.api.world.extent.BlockVolume;
import org.spongepowered.math.vector.Vector3i;

public final class ExtentBufferHelper {

    public static int[] copyToBiomeArray(BiomeVolume area, Vector3i min, Vector3i max, Vector3i size) {
        // Check if the volume has more biomes than can be stored in an array
        final long memory = (long) size.getX() * (long) size.getY() * (long) size.getZ();
        // Leave 8 bytes for a header used in some JVMs
        if (memory > Integer.MAX_VALUE - 8) {
            throw new OutOfMemoryError("Cannot copy the biomes to an array because the size limit was reached!");
        }
        final int[] copy = new int[(int) memory];
        int i = 0;
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                for (int y = min.getY(); y <= max.getY(); y++) {
                    copy[i++] = BiomeRegistryModule.get().getInternalId(area.getBiome(x, 0, z));
                }
            }
        }
        return copy;
    }

    public static BiomeType[] copyToBiomeObjectArray(BiomeVolume area, Vector3i min, Vector3i max, Vector3i size) {
        // Check if the volume has more biomes than can be stored in an array
        final long memory = (long) size.getX() * (long) size.getY() * (long) size.getZ();
        // Leave 8 bytes for a header used in some JVMs
        if (memory > Integer.MAX_VALUE - 8) {
            throw new OutOfMemoryError("Cannot copy the biomes to an array because the size limit was reached!");
        }
        final BiomeType[] copy = new BiomeType[(int) memory];
        int i = 0;
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                for (int y = min.getY(); y <= max.getY(); y++) {
                    copy[i++] = area.getBiome(x, y, z);
                }
            }
        }
        return copy;
    }

    public static int[] copyToBlockArray(BlockVolume volume, Vector3i min, Vector3i max, Vector3i size) {
        // Check if the volume has more blocks than can be stored in an array
        final long memory = (long) size.getX() * (long) size.getY() * (long) size.getZ();
        // Leave 8 bytes for a header used in some JVMs
        if (memory > Integer.MAX_VALUE - 8) {
            throw new OutOfMemoryError("Cannot copy the blocks to an array because the size limit was reached!");
        }
        final int[] copy = new int[(int) memory];
        int i = 0;
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                for (int y = min.getY(); y <= max.getY(); y++) {
                    copy[i++] = BlockRegistryModule.get().getStateInternalId(volume.getBlock(x, y, z));
                }
            }
        }
        return copy;
    }

    private ExtentBufferHelper() {
    }
}
