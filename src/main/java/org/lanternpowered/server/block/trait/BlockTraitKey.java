package org.lanternpowered.server.block.trait;

import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;

import com.google.common.base.Objects;

/**
 * This object is a key that is used to modify values
 * of a block state.
 */
public final class BlockTraitKey<E extends Comparable<E>, V extends MutableBlockTraitValue<E>> implements Key<V> {

    private final BlockTrait<E> blockTrait;
    private final Class<E> elementClass;
    private final Class<V> valueClass;
    private final DataQuery path;

    public BlockTraitKey(BlockTrait<E> blockTrait, Class<E> elementClass, Class<V> valueClass) {
        this.path = DataQuery.of(blockTrait.getName());
        this.elementClass = elementClass;
        this.valueClass = valueClass;
        this.blockTrait = blockTrait;
    }

    /**
     * Gets the block trait attached to this key.
     * 
     * @return the block trait
     */
    public BlockTrait<E> getBlockTrait() {
        return this.blockTrait;
    }

    @Override
    public Class<V> getValueClass() {
        return this.valueClass;
    }

    @Override
    public DataQuery getQuery() {
        return this.path;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.blockTrait, this.elementClass, this.valueClass, this.path);
    }

    @Override
    public String toString() {
        return "BlockTraitKey{Value:" + this.valueClass.getName() + "<" + this.elementClass + ">, Query: " + this.path.toString() + "}";
    }
}
