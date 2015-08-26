package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector2i;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;

public class ImmutableBiomeViewDownsize extends AbstractBiomeViewDownsize<ImmutableBiomeArea> implements ImmutableBiomeArea {

    public ImmutableBiomeViewDownsize(ImmutableBiomeArea area, Vector2i min, Vector2i max) {
        super(area, min, max);
    }

    @Override
    public ImmutableBiomeArea getBiomeView(Vector2i newMin, Vector2i newMax) {
        this.checkRange(newMin.getX(), newMin.getY());
        this.checkRange(newMax.getX(), newMax.getY());
        return new ImmutableBiomeViewDownsize(this.area, newMin, newMax);
    }

    @Override
    public ImmutableBiomeArea getBiomeView(DiscreteTransform2 transform) {
        return new ImmutableBiomeViewTransform(this, transform);
    }

    @Override
    public ImmutableBiomeArea getRelativeBiomeView() {
        return this.getBiomeView(DiscreteTransform2.fromTranslation(this.min.negate()));
    }

    @Override
    public UnmodifiableBiomeArea getUnmodifiableBiomeView() {
        return this;
    }

}
