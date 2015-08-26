package org.lanternpowered.server.util.gen.concurrent;

import com.flowpowered.math.vector.Vector2i;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.util.concurrent.AtomicShortArray;
import org.lanternpowered.server.util.gen.AbstractBiomeBuffer;
import org.lanternpowered.server.util.gen.ShortArrayImmutableBiomeBuffer;
import org.lanternpowered.server.util.gen.ShortArrayMutableBiomeBuffer;
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
        this.biomes.set(this.getIndex(x, z), LanternGame.get().getRegistry().getBiomeRegistry().getInternalId(biome));
    }

    @Override
    public BiomeType getBiome(Vector2i position) {
        return this.getBiome(position.getX(), position.getY());
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        this.checkRange(x, z);
        short biomeId = this.biomes.get(this.getIndex(x, z));
        BiomeType biomeType = LanternGame.get().getRegistry().getBiomeRegistry().getByInternalId(biomeId);
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
