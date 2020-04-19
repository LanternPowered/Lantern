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

import org.lanternpowered.server.util.gen.biome.AtomicObjectArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.biome.ObjectArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.block.AtomicIntArrayMutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.IntArrayMutableBlockBuffer;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.ArchetypeVolume;
import org.spongepowered.api.world.extent.ExtentBufferFactory;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.math.vector.Vector3i;

import java.util.Arrays;

public final class LanternExtentBufferFactory implements ExtentBufferFactory {

    public static final LanternExtentBufferFactory INSTANCE = new LanternExtentBufferFactory();

    private LanternExtentBufferFactory() {
    }

    @Override
    public MutableBiomeVolume createBiomeBuffer(Vector3i min, Vector3i size) {
        final BiomeType[] array = new BiomeType[size.getX() * size.getY() * size.getZ()];
        Arrays.fill(array, BiomeTypes.OCEAN);
        return new ObjectArrayMutableBiomeBuffer(array, min, size);
    }

    @Override
    public MutableBiomeVolume createThreadSafeBiomeBuffer(Vector3i min, Vector3i size) {
        final BiomeType[] array = new BiomeType[size.getX() * size.getY() * size.getZ()];
        Arrays.fill(array, BiomeTypes.OCEAN);
        return new AtomicObjectArrayMutableBiomeBuffer(array, min, size);
    }

    @Override
    public MutableBlockVolume createBlockBuffer(Vector3i min, Vector3i size) {
        return new IntArrayMutableBlockBuffer(min, size);
    }

    @Override
    public MutableBlockVolume createThreadSafeBlockBuffer(Vector3i min, Vector3i size) {
        return new AtomicIntArrayMutableBlockBuffer(min, size);
    }

    @Override
    public ArchetypeVolume createArchetypeVolume(Vector3i size, Vector3i origin) {
        return null;
    }
}
