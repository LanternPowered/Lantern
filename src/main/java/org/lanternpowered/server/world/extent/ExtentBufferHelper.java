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
package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.world.biome.BiomeRegistryModule;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.BiomeVolume;
import org.spongepowered.api.world.extent.BlockVolume;

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
