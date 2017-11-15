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
package org.lanternpowered.server.util.gen.biome;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.game.registry.type.world.biome.BiomeRegistryModule;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.ImmutableBiomeVolume;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.StorageType;

import java.util.Arrays;

/**
 * Mutable biome volume backed by a short array. Reusable.

 * <p>Using {@link #detach()} the underlying short array can be accessed.
 * The short array can then be reused by calling {@link #reuse(Vector3i)}.</p>
 */
public class IntArrayMutableBiomeBuffer extends AbstractMutableBiomeBuffer {

    private boolean detached;
    private final int[] biomes;

    protected void checkOpen() {
        checkState(!this.detached, "Trying to use buffer after it's closed!");
    }

    public IntArrayMutableBiomeBuffer(Vector3i start, Vector3i size) {
        this(new int[size.getX() * size.getY() * size.getZ()], start, size);
    }

    public IntArrayMutableBiomeBuffer(int[] biomes, Vector3i start, Vector3i size) {
        super(start, size);
        this.biomes = biomes;
    }

    @Override
    public void setBiome(int x, int y, int z, BiomeType biome) {
        checkOpen();
        checkRange(x, y, z);
        this.biomes[index(x, y, z)] = BiomeRegistryModule.get().getInternalId(biome);
    }

    @Override
    public BiomeType getBiome(int x, int y, int z) {
        checkOpen();
        checkRange(x, y, z);
        return BiomeRegistryModule.get().getByInternalId(this.biomes[index(x, y, z)]).orElse(BiomeTypes.OCEAN);
    }

    /**
     * Gets the internal short array, and prevents further of it through this
     * object uses until {@link #reuse(Vector3i)} is called.
     *
     * @return The internal byte array.
     */
    public int[] detach() {
        checkOpen();
        this.detached = true;
        return this.biomes;
    }

    /**
     * Gets whether this biome volume is currently detached. When detached, this
     * object is available for reuse using {@link #reuse(Vector3i)}.
     *
     * @return Whether this biome volume is detached.
     */
    public boolean isDetached() {
        return this.detached;
    }

    /**
     * Changes the bounds of this biome volume, so that it can be reused for
     * another chunk.
     *
     * @param start New start position.
     */
    public void reuse(Vector3i start) {
        checkState(this.detached, "Cannot reuse while still in use");

        this.start = checkNotNull(start, "start");
        this.end = this.start.add(this.size).sub(Vector3i.ONE);
        Arrays.fill(this.biomes, (short) 0);

        this.detached = false;
    }

    @Override
    public MutableBiomeVolume getBiomeCopy(StorageType type) {
        checkOpen();
        switch (type) {
            case STANDARD:
                return new IntArrayMutableBiomeBuffer(this.biomes.clone(), this.start, this.size);
            case THREAD_SAFE:
                return new AtomicIntArrayMutableBiomeBuffer(this.biomes, this.start, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBiomeVolume getImmutableBiomeCopy() {
        checkOpen();
        return new IntArrayImmutableBiomeBuffer(this.biomes, this.start, this.size);
    }
}
