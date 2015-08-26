package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector2i;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.world.extent.BiomeArea;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;

public class UnmodifiableBiomeViewDownsize extends AbstractBiomeViewDownsize<BiomeArea> implements UnmodifiableBiomeArea {

    public UnmodifiableBiomeViewDownsize(BiomeArea area, Vector2i min, Vector2i max) {
        super(area, min, max);
    }

    @Override
    public UnmodifiableBiomeArea getBiomeView(Vector2i newMin, Vector2i newMax) {
        this.checkRange(newMin.getX(), newMin.getY());
        this.checkRange(newMax.getX(), newMax.getY());
        return new UnmodifiableBiomeViewDownsize(this.area, newMin, newMax);
    }

    @Override
    public UnmodifiableBiomeArea getBiomeView(DiscreteTransform2 transform) {
        return new UnmodifiableBiomeViewTransform(this, transform);
    }

    @Override
    public UnmodifiableBiomeArea getRelativeBiomeView() {
        return this.getBiomeView(DiscreteTransform2.fromTranslation(this.min.negate()));
    }

    @Override
    public UnmodifiableBiomeArea getUnmodifiableBiomeView() {
        return this;
    }

}
