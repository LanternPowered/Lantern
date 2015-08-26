package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector2i;

import org.lanternpowered.server.util.VecHelper;
import org.lanternpowered.server.util.gen.ShortArrayImmutableBiomeBuffer;
import org.lanternpowered.server.util.gen.ShortArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.concurrent.AtomicShortArrayMutableBiomeBuffer;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.BiomeArea;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.StorageType;

public abstract class AbstractBiomeViewDownsize<A extends BiomeArea> implements BiomeArea {

    protected final A area;
    protected final Vector2i min;
    protected final Vector2i max;
    protected final Vector2i size;

    public AbstractBiomeViewDownsize(A area, Vector2i min, Vector2i max) {
        this.area = area;
        this.min = min;
        this.max = max;
        this.size = max.sub(min).add(Vector2i.ONE);
    }

    @Override
    public Vector2i getBiomeMin() {
        return this.min;
    }

    @Override
    public Vector2i getBiomeMax() {
        return this.max;
    }

    @Override
    public Vector2i getBiomeSize() {
        return this.size;
    }

    @Override
    public boolean containsBiome(Vector2i position) {
        return this.containsBiome(position.getX(), position.getY());
    }

    @Override
    public boolean containsBiome(int x, int z) {
        return VecHelper.inBounds(x, z, this.min, this.max);
    }

    protected final void checkRange(int x, int z) {
        if (!VecHelper.inBounds(x, z, this.min, this.max)) {
            throw new PositionOutOfBoundsException(new Vector2i(x, z), this.min, this.max);
        }
    }

    @Override
    public BiomeType getBiome(Vector2i position) {
        return this.getBiome(position.getX(), position.getY());
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        this.checkRange(x, z);
        return this.area.getBiome(x, z);
    }

    @Override
    public MutableBiomeArea getBiomeCopy() {
        return this.getBiomeCopy(StorageType.STANDARD);
    }

    @Override
    public MutableBiomeArea getBiomeCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new ShortArrayMutableBiomeBuffer(ExtentBufferUtil.copyToArray(this, this.min, this.max, this.size), this.min, this.size);
            case THREAD_SAFE:
                return new AtomicShortArrayMutableBiomeBuffer(ExtentBufferUtil.copyToArray(this, this.min, this.max, this.size), this.min, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBiomeArea getImmutableBiomeCopy() {
        return ShortArrayImmutableBiomeBuffer.newWithoutArrayClone(ExtentBufferUtil.copyToArray(this, this.min, this.max, this.size), this.min,
            this.size);
    }

}
