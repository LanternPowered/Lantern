package org.lanternpowered.server.util.gen;

import com.flowpowered.math.vector.Vector3i;

import org.lanternpowered.server.block.LanternBlocks;
import org.lanternpowered.server.util.gen.concurrent.AtomicShortArrayMutableBlockBuffer;
import org.lanternpowered.server.world.extent.ImmutableBlockViewDownsize;
import org.lanternpowered.server.world.extent.ImmutableBlockViewTransform;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;

@NonnullByDefault
public class ShortArrayImmutableBlockBuffer extends AbstractBlockBuffer implements ImmutableBlockVolume {

    private final BlockState air = BlockTypes.AIR.getDefaultState();
    private final short[] blocks;

    public ShortArrayImmutableBlockBuffer(short[] blocks, Vector3i start, Vector3i size) {
        super(start, size);
        this.blocks = blocks.clone();
    }

    private ShortArrayImmutableBlockBuffer(Vector3i start, Vector3i size, short[] blocks) {
        super(start, size);
        this.blocks = blocks;
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        this.checkRange(x, y, z);
        short blockState = this.blocks[this.getIndex(x, y, z)];
        BlockState block = LanternBlocks.getStateById(blockState);
        return block == null ? this.air : block;
    }

    @Override
    public ImmutableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        this.checkRange(newMin.getX(), newMin.getY(), newMin.getZ());
        this.checkRange(newMax.getX(), newMax.getY(), newMax.getZ());
        return new ImmutableBlockViewDownsize(this, newMin, newMax);
    }

    @Override
    public ImmutableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new ImmutableBlockViewTransform(this, transform);
    }

    @Override
    public ImmutableBlockVolume getRelativeBlockView() {
        return this.getBlockView(DiscreteTransform3.fromTranslation(this.start.negate()));
    }

    @Override
    public UnmodifiableBlockVolume getUnmodifiableBlockView() {
        return this;
    }

    @Override
    public MutableBlockVolume getBlockCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new ShortArrayMutableBlockBuffer(this.blocks.clone(), this.start, this.size);
            case THREAD_SAFE:
                return new AtomicShortArrayMutableBlockBuffer(this.blocks, this.start, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBlockVolume getImmutableBlockCopy() {
        return this;
    }

    /**
     * This method doesn't clone the array passed into it. INTERNAL USE ONLY.
     * Make sure your code doesn't leak the reference if you're using it.
     *
     * @param blocks The blocks to store
     * @param start The start of the volume
     * @param size The size of the volume
     * @return A new buffer using the same array reference
     */
    public static ImmutableBlockVolume newWithoutArrayClone(short[] blocks, Vector3i start, Vector3i size) {
        return new ShortArrayImmutableBlockBuffer(start, size, blocks);
    }
}
