package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;

public class ImmutableBlockViewTransform extends AbstractBlockViewTransform<ImmutableBlockVolume> implements ImmutableBlockVolume {

    public ImmutableBlockViewTransform(ImmutableBlockVolume area, DiscreteTransform3 transform) {
        super(area, transform);
    }

    @Override
    public ImmutableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        return new ImmutableBlockViewDownsize(this.volume, this.inverseTransform.transform(newMin), this.inverseTransform.transform(newMax))
            .getBlockView(this.transform);
    }

    @Override
    public ImmutableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new ImmutableBlockViewTransform(this.volume, this.transform.withTransformation(transform));
    }

    @Override
    public ImmutableBlockVolume getRelativeBlockView() {
        return this.getBlockView(DiscreteTransform3.fromTranslation(this.min.negate()));
    }

    @Override
    public UnmodifiableBlockVolume getUnmodifiableBlockView() {
        return this;
    }

}
