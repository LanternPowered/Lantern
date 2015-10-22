/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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
package org.lanternpowered.server.util.gen.concurrent;

import com.flowpowered.math.vector.Vector2i;

import org.lanternpowered.server.util.concurrent.AtomicShortArray;
import org.lanternpowered.server.util.gen.AbstractBiomeBuffer;
import org.lanternpowered.server.util.gen.ShortArrayImmutableBiomeBuffer;
import org.lanternpowered.server.util.gen.ShortArrayMutableBiomeBuffer;
import org.lanternpowered.server.world.biome.LanternBiomes;
import org.lanternpowered.server.world.extent.MutableBiomeViewDownsize;
import org.lanternpowered.server.world.extent.MutableBiomeViewTransform;
import org.lanternpowered.server.world.extent.UnmodifiableBiomeAreaWrapper;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;

/**
 * Mutable biome area backed by a atomic short array.
 */
@NonnullByDefault
public final class AtomicShortArrayMutableBiomeBuffer extends AbstractBiomeBuffer implements MutableBiomeArea {

    private final AtomicShortArray biomes;

    public AtomicShortArrayMutableBiomeBuffer(Vector2i start, Vector2i size) {
        super(start, size);
        this.biomes = new AtomicShortArray(size.getX() * size.getY());
    }

    public AtomicShortArrayMutableBiomeBuffer(short[] biomes, Vector2i start, Vector2i size) {
        super(start, size);
        this.biomes = new AtomicShortArray(biomes);
    }

    @Override
    public void setBiome(Vector2i position, BiomeType biome) {
        this.setBiome(position.getX(), position.getY(), biome);
    }

    @Override
    public void setBiome(int x, int z, BiomeType biome) {
        this.checkRange(x, z);
        this.biomes.set(this.getIndex(x, z), LanternBiomes.getId(biome));
    }

    @Override
    public BiomeType getBiome(Vector2i position) {
        return this.getBiome(position.getX(), position.getY());
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        this.checkRange(x, z);
        short biomeId = this.biomes.get(this.getIndex(x, z));
        BiomeType biomeType = LanternBiomes.getById(biomeId);
        return biomeType == null ? BiomeTypes.OCEAN : biomeType;
    }

    @Override
    public MutableBiomeArea getBiomeView(Vector2i newMin, Vector2i newMax) {
        this.checkRange(newMin.getX(), newMin.getY());
        this.checkRange(newMax.getX(), newMax.getY());
        return new MutableBiomeViewDownsize(this, newMin, newMax);
    }

    @Override
    public MutableBiomeArea getBiomeView(DiscreteTransform2 transform) {
        return new MutableBiomeViewTransform(this, transform);
    }

    @Override
    public MutableBiomeArea getRelativeBiomeView() {
        return this.getBiomeView(DiscreteTransform2.fromTranslation(this.start.negate()));
    }

    @Override
    public UnmodifiableBiomeArea getUnmodifiableBiomeView() {
        return new UnmodifiableBiomeAreaWrapper(this);
    }

    @Override
    public MutableBiomeArea getBiomeCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new ShortArrayMutableBiomeBuffer(this.biomes.getArray(), this.start, this.size);
            case THREAD_SAFE:
                return new AtomicShortArrayMutableBiomeBuffer(this.biomes.getArray(), this.start, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBiomeArea getImmutableBiomeCopy() {
        return new ShortArrayImmutableBiomeBuffer(this.biomes.getArray(), this.start, this.size);
    }
}
