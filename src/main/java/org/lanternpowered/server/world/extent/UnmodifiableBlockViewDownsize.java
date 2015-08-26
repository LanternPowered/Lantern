package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.extent.BlockVolume;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;

public class UnmodifiableBlockViewDownsize extends AbstractBlockViewDownsize<BlockVolume> implements UnmodifiableBlockVolume {

    public UnmodifiableBlockViewDownsize(BlockVolume volume, Vector3i min, Vector3i max) {
        super(volume, min, max);
    }

    @Override
    public UnmodifiableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        this.checkRange(newMin.getX(), newMin.getY(), newMin.getZ());
        this.checkRange(newMax.getX(), newMax.getY(), newMax.getZ());
        return new UnmodifiableBlockViewDownsize(this.volume, newMin, newMax);
    }

    @Override
    public UnmodifiableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new UnmodifiableBlockViewTransform(this, transform);
    }

    @Override
    public UnmodifiableBlockVolume getRelativeBlockView() {
        return this.getBlockView(DiscreteTransform3.fromTranslation(this.min.negate()));
    }

    @Override
    public UnmodifiableBlockVolume getUnmodifiableBlockView() {
        return this;
    }

}
