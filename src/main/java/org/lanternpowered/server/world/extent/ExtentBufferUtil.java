package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;

import org.lanternpowered.server.block.LanternBlocks;
import org.lanternpowered.server.world.biome.LanternBiomes;
import org.spongepowered.api.world.extent.BiomeArea;
import org.spongepowered.api.world.extent.BlockVolume;

public class ExtentBufferUtil {

    public static short[] copyToArray(BiomeArea area, Vector2i min, Vector2i max, Vector2i size) {
        // Check if the area has more biomes than can be stored in an array
        final long memory = (long) size.getX() * (long) size.getY();
        // Leave 8 bytes for a header used in some JVMs
        if (memory > Integer.MAX_VALUE - 8) {
            throw new OutOfMemoryError("Cannot copy the biomes to an array because the size limit was reached!");
        }
        final short[] copy = new short[(int) memory];
        int i = 0;
        for (int y = min.getY(); y <= max.getY(); y++) {
            for (int x = min.getX(); x <= max.getX(); x++) {
                copy[i++] = LanternBiomes.getId(area.getBiome(y, x));
            }
        }
        return copy;
    }

    public static short[] copyToArray(BlockVolume volume, Vector3i min, Vector3i max, Vector3i size) {
        // Check if the volume has more blocks than can be stored in an array
        final long memory = (long) size.getX() * (long) size.getY() * (long) size.getZ();
        // Leave 8 bytes for a header used in some JVMs
        if (memory > Integer.MAX_VALUE - 8) {
            throw new OutOfMemoryError("Cannot copy the blocks to an array because the size limit was reached!");
        }
        final short[] copy = new short[(int) memory];
        int i = 0;
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                for (int y = min.getY(); y <= max.getY(); y++) {
                    copy[i++] = LanternBlocks.getStateId(volume.getBlock(x, y, z));
                }
            }
        }
        return copy;
    }

}
