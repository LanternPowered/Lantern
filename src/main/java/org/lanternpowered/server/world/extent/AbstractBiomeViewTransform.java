package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector2i;

import org.lanternpowered.server.util.gen.ShortArrayImmutableBiomeBuffer;
import org.lanternpowered.server.util.gen.ShortArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.concurrent.AtomicShortArrayMutableBiomeBuffer;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.BiomeArea;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.StorageType;

public abstract class AbstractBiomeViewTransform<A extends BiomeArea> implements BiomeArea {

    protected final A area;
    protected final DiscreteTransform2 transform;
    protected final DiscreteTransform2 inverseTransform;
    protected final Vector2i min;
    protected final Vector2i max;
    protected final Vector2i size;

    public AbstractBiomeViewTransform(A area, DiscreteTransform2 transform) {
        this.area = area;
        this.transform = transform;
        this.inverseTransform = transform.invert();

        final Vector2i a = transform.transform(area.getBiomeMin());
        final Vector2i b = transform.transform(area.getBiomeMax());
        this.min = a.min(b);
        this.max = a.max(b);

        this.size = this.max.sub(this.min).add(Vector2i.ONE);
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
        return this.area.containsBiome(this.inverseTransform.transformX(x, z), this.inverseTransform.transformY(x, z));
    }

    @Override
    public BiomeType getBiome(Vector2i position) {
        return this.getBiome(position.getX(), position.getY());
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        return this.area.getBiome(this.inverseTransform.transformX(x, z), this.inverseTransform.transformY(x, z));
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
