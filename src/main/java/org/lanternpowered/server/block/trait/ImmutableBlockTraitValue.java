package org.lanternpowered.server.block.trait;

import org.lanternpowered.server.data.value.immutable.ImmutableLanternValue;

public class ImmutableBlockTraitValue<V extends Comparable<V>> extends ImmutableLanternValue<V> implements BlockTraitValue<V> {

    public ImmutableBlockTraitValue(BlockTraitKey<V, ? extends MutableBlockTraitValue<V>> key, V actualValue) {
        super(key, key.getBlockTrait().getPossibleValues().iterator().next(), actualValue);
    }

    public ImmutableBlockTraitValue(BlockTraitKey<V, ? extends MutableBlockTraitValue<V>> key) {
        super(key, key.getBlockTrait().getPossibleValues().iterator().next());
    }

    @SuppressWarnings("unchecked")
    @Override
    public MutableBlockTraitValue<V> asMutable() {
        return new MutableBlockTraitValue<V>((BlockTraitKey<V, ? extends MutableBlockTraitValue<V>>) getKey(), get());
    }
}
