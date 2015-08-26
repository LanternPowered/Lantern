package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;

public class UnmodifiableBlockVolumeWrapper implements UnmodifiableBlockVolume {

    private final MutableBlockVolume volume;

    public UnmodifiableBlockVolumeWrapper(MutableBlockVolume volume) {
        this.volume = volume;
    }

    @Override
    public Vector3i getBlockMin() {
        return this.volume.getBlockMin();
    }

    @Override
    public Vector3i getBlockMax() {
        return this.volume.getBlockMax();
    }

    @Override
    public Vector3i getBlockSize() {
        return this.volume.getBlockSize();
    }

    @Override
    public boolean containsBlock(Vector3i position) {
        return this.volume.containsBlock(position);
    }

    @Override
    public boolean containsBlock(int x, int y, int z) {
        return this.volume.containsBlock(x, y, z);
    }

    @Override
    public BlockType getBlockType(Vector3i position) {
        return this.volume.getBlockType(position);
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        return this.volume.getBlockType(x, y, z);
    }

    @Override
    public BlockState getBlock(Vector3i position) {
        return this.volume.getBlock(position);
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        return this.volume.getBlock(x, y, z);
    }

    @Override
    public UnmodifiableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        return new UnmodifiableBlockVolumeWrapper(this.volume.getBlockView(newMin, newMax));
    }

    @Override
    public UnmodifiableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new UnmodifiableBlockVolumeWrapper(this.volume.getBlockView(transform));
    }

    @Override
    public UnmodifiableBlockVolume getRelativeBlockView() {
        return new UnmodifiableBlockVolumeWrapper(this.volume.getRelativeBlockView());
    }

    @Override
    public UnmodifiableBlockVolume getUnmodifiableBlockView() {
        return this;
    }

    @Override
    public MutableBlockVolume getBlockCopy() {
        return this.volume.getBlockCopy();
    }

    @Override
    public MutableBlockVolume getBlockCopy(StorageType type) {
        return this.volume.getBlockCopy(type);
    }

    @Override
    public ImmutableBlockVolume getImmutableBlockCopy() {
        return this.volume.getImmutableBlockCopy();
    }
}
