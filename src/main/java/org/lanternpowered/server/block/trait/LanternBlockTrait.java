package org.lanternpowered.server.block.trait;

import java.util.Collection;

import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.value.mutable.Value;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

@SuppressWarnings({"unchecked","rawtypes"})
public class LanternBlockTrait<T extends Comparable<T>> implements BlockTrait<T> {

    private final BlockTraitKey<T, MutableBlockTraitValue<T>> key;
    private final ImmutableSet<T> possibleValues;
    private final Class<T> valueClass;
    private final String name;

    LanternBlockTrait(String name, Class<T> valueClass, ImmutableSet<T> possibleValues) {
        this.possibleValues = possibleValues;
        this.valueClass = valueClass;
        this.name = name;
        this.key = new BlockTraitKey(this, valueClass, Value.class);
    }

    /**
     * Gets the block trait key.
     * 
     * @return the block trait key
     */
    public BlockTraitKey<T, MutableBlockTraitValue<T>> getKey() {
        return this.key;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Collection<T> getPossibleValues() {
        return this.possibleValues;
    }

    @Override
    public Class<T> getValueClass() {
        return this.valueClass;
    }

    @Override
    public Predicate<T> getPredicate() {
        return Predicates.in(this.possibleValues);
    }
}
