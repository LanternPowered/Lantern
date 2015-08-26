package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector2i;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;

public class UnmodifiableBiomeAreaWrapper implements UnmodifiableBiomeArea {

    private final MutableBiomeArea area;

    public UnmodifiableBiomeAreaWrapper(MutableBiomeArea area) {
        this.area = area;
    }

    @Override
    public Vector2i getBiomeMin() {
        return this.area.getBiomeMin();
    }

    @Override
    public Vector2i getBiomeMax() {
        return this.area.getBiomeMax();
    }

    @Override
    public Vector2i getBiomeSize() {
        return this.area.getBiomeSize();
    }

    @Override
    public boolean containsBiome(Vector2i position) {
        return this.area.containsBiome(position);
    }

    @Override
    public boolean containsBiome(int x, int z) {
        return this.area.containsBiome(x, z);
    }

    @Override
    public BiomeType getBiome(Vector2i position) {
        return this.area.getBiome(position);
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        return this.area.getBiome(x, z);
    }

    @Override
    public UnmodifiableBiomeArea getBiomeView(Vector2i newMin, Vector2i newMax) {
        return new UnmodifiableBiomeAreaWrapper(this.area.getBiomeView(newMin, newMax));
    }

    @Override
    public UnmodifiableBiomeArea getBiomeView(DiscreteTransform2 transform) {
        return new UnmodifiableBiomeAreaWrapper(this.area.getBiomeView(transform));
    }

    @Override
    public UnmodifiableBiomeArea getRelativeBiomeView() {
        return new UnmodifiableBiomeAreaWrapper(this.area.getRelativeBiomeView());
    }

    @Override
    public UnmodifiableBiomeArea getUnmodifiableBiomeView() {
        return this;
    }

    @Override
    public MutableBiomeArea getBiomeCopy() {
        return this.area.getBiomeCopy();
    }

    @Override
    public MutableBiomeArea getBiomeCopy(StorageType type) {
        return this.area.getBiomeCopy(type);
    }

    @Override
    public ImmutableBiomeArea getImmutableBiomeCopy() {
        return this.area.getImmutableBiomeCopy();
    }
}
