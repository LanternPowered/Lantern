package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;

public class ImmutableBlockViewDownsize extends AbstractBlockViewDownsize<ImmutableBlockVolume> implements ImmutableBlockVolume {

    public ImmutableBlockViewDownsize(ImmutableBlockVolume volume, Vector3i min, Vector3i max) {
        super(volume, min, max);
    }

    @Override
    public ImmutableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        this.checkRange(newMin.getX(), newMin.getY(), newMin.getZ());
        this.checkRange(newMax.getX(), newMax.getY(), newMax.getZ());
        return new ImmutableBlockViewDownsize(this.volume, newMin, newMax);
    }

    @Override
    public ImmutableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new ImmutableBlockViewTransform(this, transform);
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
