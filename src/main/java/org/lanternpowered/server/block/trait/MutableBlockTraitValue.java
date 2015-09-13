package org.lanternpowered.server.block.trait;

import org.lanternpowered.server.data.value.mutable.LanternValue;

public class MutableBlockTraitValue<V extends Comparable<V>> extends LanternValue<V> implements BlockTraitValue<V> {

    public MutableBlockTraitValue(BlockTraitKey<V, ? extends MutableBlockTraitValue<V>> key, V actualValue) {
        super(key, key.getBlockTrait().getPossibleValues().iterator().next(), actualValue);
    }

    public MutableBlockTraitValue(BlockTraitKey<V, ? extends MutableBlockTraitValue<V>> key) {
        super(key, key.getBlockTrait().getPossibleValues().iterator().next());
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImmutableBlockTraitValue<V> asImmutable() {
        return new ImmutableBlockTraitValue<V>((BlockTraitKey<V, ? extends MutableBlockTraitValue<V>>) this.getKey(), this.get());
    }
}
