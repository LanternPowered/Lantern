package org.lanternpowered.server.util.gen;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.flowpowered.math.vector.Vector2i;

import org.lanternpowered.server.util.gen.concurrent.AtomicShortArrayMutableBiomeBuffer;
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

import java.util.Arrays;

/**
 * Mutable biome area backed by a short array. Reusable.

 * <p>Using {@link #detach()} the underlying short array can be accessed.
 * The short array can then be reused by calling {@link #reuse(Vector2i)}.</p>
 */
@NonnullByDefault
public class ShortArrayMutableBiomeBuffer extends AbstractBiomeBuffer implements MutableBiomeArea {

    private boolean detached;
    private final short[] biomes;

    private void checkOpen() {
        checkState(!this.detached, "Trying to use buffer after it's closed!");
    }

    public ShortArrayMutableBiomeBuffer(Vector2i start, Vector2i size) {
        this(new short[size.getX() * size.getY()], start, size);
    }

    public ShortArrayMutableBiomeBuffer(short[] biomes, Vector2i start, Vector2i size) {
        super(start, size);
        this.biomes = biomes;
    }

    @Override
    public void setBiome(Vector2i position, BiomeType biome) {
        this.setBiome(position.getX(), position.getY(), biome);
    }

    @Override
    public void setBiome(int x, int z, BiomeType biome) {
        this.checkOpen();
        this.checkRange(x, z);
        this.biomes[this.getIndex(x, z)] = LanternBiomes.getId(biome);
    }

    @Override
    public BiomeType getBiome(Vector2i position) {
        return this.getBiome(position.getX(), position.getY());
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        this.checkOpen();
        this.checkRange(x, z);

        short biomeId = this.biomes[this.getIndex(x, z)];
        BiomeType biomeType = LanternBiomes.getById(biomeId);
        return biomeType == null ? BiomeTypes.OCEAN : biomeType;
    }

    /**
     * Gets the internal short array, and prevents further of it through this
     * object uses until {@link #reuse(Vector2i)} is called.
     *
     * @return The internal byte array.
     */
    public short[] detach() {
        this.checkOpen();
        this.detached = true;
        return this.biomes;
    }

    /**
     * Gets whether this biome area is currently detached. When detached, this
     * object is available for reuse using {@link #reuse(Vector2i)}.
     *
     * @return Whether this biome area is detached.
     */
    public boolean isDetached() {
        return this.detached;
    }

    /**
     * Changes the bounds of this biome area, so that it can be reused for
     * another chunk.
     *
     * @param start New start position.
     */
    public void reuse(Vector2i start) {
        checkState(this.detached, "Cannot reuse while still in use");

        this.start = checkNotNull(start, "start");
        this.end = this.start.add(this.size).sub(Vector2i.ONE);
        Arrays.fill(this.biomes, (short) 0);

        this.detached = false;
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
        this.checkOpen();
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
        this.checkOpen();
        return new ShortArrayImmutableBiomeBuffer(this.biomes, this.start, this.size);
    }
}
