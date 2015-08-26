package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector2i;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;

public class ImmutableBiomeViewTransform extends AbstractBiomeViewTransform<ImmutableBiomeArea> implements ImmutableBiomeArea {

    public ImmutableBiomeViewTransform(ImmutableBiomeArea area, DiscreteTransform2 transform) {
        super(area, transform);
    }

    @Override
    public ImmutableBiomeArea getBiomeView(Vector2i newMin, Vector2i newMax) {
        return new ImmutableBiomeViewDownsize(this.area, this.inverseTransform.transform(newMin), this.inverseTransform.transform(newMax)).getBiomeView(this.transform);
    }

    @Override
    public ImmutableBiomeArea getBiomeView(DiscreteTransform2 transform) {
        return new ImmutableBiomeViewTransform(this.area, this.transform.withTransformation(transform));
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
