package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector2i;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;

public class MutableBiomeViewTransform extends AbstractBiomeViewTransform<MutableBiomeArea> implements MutableBiomeArea {

    public MutableBiomeViewTransform(MutableBiomeArea area, DiscreteTransform2 transform) {
        super(area, transform);
    }

    @Override
    public void setBiome(Vector2i position, BiomeType biome) {
        this.setBiome(position.getX(), position.getY(), biome);
    }

    @Override
    public void setBiome(int x, int z, BiomeType biome) {
        this.area.setBiome(this.inverseTransform.transformX(x, z), this.inverseTransform.transformY(x, z), biome);
    }

    @Override
    public MutableBiomeArea getBiomeView(Vector2i newMin, Vector2i newMax) {
        return new MutableBiomeViewDownsize(this.area, this.inverseTransform.transform(newMin), this.inverseTransform.transform(newMax)).getBiomeView(this.transform);
    }

    @Override
    public MutableBiomeArea getBiomeView(DiscreteTransform2 transform) {
        return new MutableBiomeViewTransform(this.area, this.transform.withTransformation(transform));
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
