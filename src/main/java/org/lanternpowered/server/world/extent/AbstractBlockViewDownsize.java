package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector3i;

import org.lanternpowered.server.util.VecHelper;
import org.lanternpowered.server.util.gen.ShortArrayImmutableBlockBuffer;
import org.lanternpowered.server.util.gen.ShortArrayMutableBlockBuffer;
import org.lanternpowered.server.util.gen.concurrent.AtomicShortArrayMutableBlockBuffer;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.extent.BlockVolume;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;

public abstract class AbstractBlockViewDownsize<V extends BlockVolume> implements BlockVolume {

    protected final V volume;
    protected final Vector3i min;
    protected final Vector3i max;
    protected final Vector3i size;

    public AbstractBlockViewDownsize(V volume, Vector3i min, Vector3i max) {
        this.volume = volume;
        this.min = min;
        this.max = max;
        this.size = max.sub(min).add(Vector3i.ONE);
    }

    @Override
    public Vector3i getBlockMin() {
        return this.min;
    }

    @Override
    public Vector3i getBlockMax() {
        return this.max;
    }

    @Override
    public Vector3i getBlockSize() {
        return this.size;
    }

    @Override
    public boolean containsBlock(Vector3i position) {
        return this.containsBlock(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public boolean containsBlock(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, this.min, this.max);
    }

    protected final void checkRange(int x, int y, int z) {
        if (!VecHelper.inBounds(x, y, z, this.min, this.max)) {
            throw new PositionOutOfBoundsException(new Vector3i(x, y, z), this.min, this.max);
        }
    }

    @Override
    public BlockType getBlockType(Vector3i position) {
        return this.getBlockType(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        return this.getBlock(x, y, z).getType();
    }

    @Override
    public BlockState getBlock(Vector3i position) {
        return this.getBlock(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        this.checkRange(x, y, z);
        return this.volume.getBlock(x, y, z);
    }

    @Override
    public MutableBlockVolume getBlockCopy() {
        return this.getBlockCopy(StorageType.STANDARD);
    }

    @Override
    public MutableBlockVolume getBlockCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new ShortArrayMutableBlockBuffer(ExtentBufferUtil.copyToArray(this, this.min, this.max, this.size), this.min, this.size);
            case THREAD_SAFE:
                return new AtomicShortArrayMutableBlockBuffer(ExtentBufferUtil.copyToArray(this, this.min, this.max, this.size), this.min, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBlockVolume getImmutableBlockCopy() {
        return ShortArrayImmutableBlockBuffer.newWithoutArrayClone(ExtentBufferUtil.copyToArray(this, this.min, this.max, this.size), this.min,
            this.size);
    }

}
