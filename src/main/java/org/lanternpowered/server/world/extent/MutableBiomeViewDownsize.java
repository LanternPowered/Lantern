package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector2i;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;

public class MutableBiomeViewDownsize extends AbstractBiomeViewDownsize<MutableBiomeArea> implements MutableBiomeArea {

    public MutableBiomeViewDownsize(MutableBiomeArea area, Vector2i min, Vector2i max) {
        super(area, min, max);
    }

    @Override
    public void setBiome(Vector2i position, BiomeType biome) {
        this.setBiome(position.getX(), position.getY(), biome);
    }

    @Override
    public void setBiome(int x, int z, BiomeType biome) {
        this.checkRange(x, z);
        this.area.setBiome(x, z, biome);
    }

    @Override
    public MutableBiomeArea getBiomeView(Vector2i newMin, Vector2i newMax) {
        this.checkRange(newMin.getX(), newMin.getY());
        this.checkRange(newMax.getX(), newMax.getY());
        return new MutableBiomeViewDownsize(this.area, newMin, newMax);
    }

    @Override
    public MutableBiomeArea getBiomeView(DiscreteTransform2 transform) {
        return new MutableBiomeViewTransform(this, transform);
    }

    @Override
    public MutableBiomeArea getRelativeBiomeView() {
        return this.getBiomeView(DiscreteTransform2.fromTranslation(this.min.negate()));
    }

    @Override
    public UnmodifiableBiomeArea getUnmodifiableBiomeView() {
        return new UnmodifiableBiomeAreaWrapper(this);
    }
}
