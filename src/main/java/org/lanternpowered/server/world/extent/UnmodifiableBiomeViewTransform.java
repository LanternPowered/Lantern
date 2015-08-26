package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector2i;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;

public class UnmodifiableBiomeViewTransform extends AbstractBiomeViewTransform<UnmodifiableBiomeArea> implements UnmodifiableBiomeArea {

    public UnmodifiableBiomeViewTransform(UnmodifiableBiomeArea area, DiscreteTransform2 transform) {
        super(area, transform);
    }

    @Override
    public UnmodifiableBiomeArea getBiomeView(Vector2i newMin, Vector2i newMax) {
        return new UnmodifiableBiomeViewDownsize(this.area, this.inverseTransform.transform(newMin), this.inverseTransform.transform(newMax)).getBiomeView(this.transform);
    }

    @Override
    public UnmodifiableBiomeArea getBiomeView(DiscreteTransform2 transform) {
        return new UnmodifiableBiomeViewTransform(this.area, this.transform.withTransformation(transform));
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
