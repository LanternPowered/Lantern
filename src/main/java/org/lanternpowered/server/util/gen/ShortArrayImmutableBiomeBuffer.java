package org.lanternpowered.server.util.gen;

import com.flowpowered.math.vector.Vector2i;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.util.gen.concurrent.AtomicShortArrayMutableBiomeBuffer;
import org.lanternpowered.server.world.extent.ImmutableBiomeViewDownsize;
import org.lanternpowered.server.world.extent.ImmutableBiomeViewTransform;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;

/**
 * Immutable biome area, backed by a short array. The array passed to the
 * constructor is copied to ensure that the instance is immutable.
 */
@NonnullByDefault
public final class ShortArrayImmutableBiomeBuffer extends AbstractBiomeBuffer implements ImmutableBiomeArea {

    private final short[] biomes;

    public ShortArrayImmutableBiomeBuffer(short[] biomes, Vector2i start, Vector2i size) {
        super(start, size);
        this.biomes = biomes.clone();
    }

    private ShortArrayImmutableBiomeBuffer(Vector2i start, Vector2i size, short[] biomes) {
        super(start, size);
        this.biomes = biomes;
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        this.checkRange(x, z);
        short biomeId = this.biomes[this.getIndex(x, z)];
        BiomeType biomeType =  LanternGame.get().getRegistry().getBiomeRegistry().getByInternalId(biomeId);
        return biomeType == null ? BiomeTypes.OCEAN : biomeType;
    }

    @Override
    public BiomeType getBiome(Vector2i position) {
        return this.getBiome(position.getX(), position.getY());
    }

    @Override
    public ImmutableBiomeArea getBiomeView(Vector2i newMin, Vector2i newMax) {
        this.checkRange(newMin.getX(), newMin.getY());
        this.checkRange(newMax.getX(), newMax.getY());
        return new ImmutableBiomeViewDownsize(this, newMin, newMax);
    }

    @Override
    public ImmutableBiomeArea getBiomeView(DiscreteTransform2 transform) {
        return new ImmutableBiomeViewTransform(this, transform);
    }

    @Override
    public ImmutableBiomeArea getRelativeBiomeView() {
        return this.getBiomeView(DiscreteTransform2.fromTranslation(this.start.negate()));
    }

    @Override
    public UnmodifiableBiomeArea getUnmodifiableBiomeView() {
        return this;
    }

    @Override
    public MutableBiomeArea getBiomeCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new ShortArrayMutableBiomeBuffer(this.biomes.clone(), this.start, this.size);
            case THREAD_SAFE:
                return new AtomicShortArrayMutableBiomeBuffer(this.biomes, this.start, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBiomeArea getImmutableBiomeCopy() {
        return this;
    }

    /**
     * This method doesn't clone the array passed into it. INTERNAL USE ONLY.
     * Make sure your code doesn't leak the reference if you're using it.
     *
     * @param biomes The biomes to store
     * @param start The start of the area
     * @param size The size of the area
     * @return A new buffer using the same array reference
     */
    public static ImmutableBiomeArea newWithoutArrayClone(short[] biomes, Vector2i start, Vector2i size) {
        return new ShortArrayImmutableBiomeBuffer(start, size, biomes);
    }
}
