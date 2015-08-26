package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;

public class UnmodifiableBlockViewTransform extends AbstractBlockViewTransform<UnmodifiableBlockVolume> implements UnmodifiableBlockVolume {

    public UnmodifiableBlockViewTransform(UnmodifiableBlockVolume area, DiscreteTransform3 transform) {
        super(area, transform);
    }

    @Override
    public UnmodifiableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        return new UnmodifiableBlockViewDownsize(this.volume, this.inverseTransform.transform(newMin), this.inverseTransform.transform(newMax))
            .getBlockView(this.transform);
    }

    @Override
    public UnmodifiableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new UnmodifiableBlockViewTransform(this.volume, this.transform.withTransformation(transform));
    }

    @Override
    public UnmodifiableBlockVolume getRelativeBlockView() {
        return getBlockView(DiscreteTransform3.fromTranslation(this.min.negate()));
    }

    @Override
    public UnmodifiableBlockVolume getUnmodifiableBlockView() {
        return this;
    }

}
