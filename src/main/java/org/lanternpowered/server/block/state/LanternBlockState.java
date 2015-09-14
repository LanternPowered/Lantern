package org.lanternpowered.server.block.state;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lanternpowered.server.block.trait.BlockTraitKey;
import org.lanternpowered.server.block.trait.MutableBlockTraitValue;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.util.Cycleable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;

@SuppressWarnings({"rawtypes", "unchecked"})
public class LanternBlockState implements BlockState {

    // A lookup table to get a specific state when you would change a value
    ImmutableTable<BlockTrait<?>, Comparable<?>, BlockState> propertyValueTable;

    // The values for every attached trait
    final ImmutableMap<BlockTrait<?>, Comparable<?>> traitValues;

    // The base block state
    private final BlockStateBase baseState;

    public LanternBlockState(BlockStateBase baseState, ImmutableMap<BlockTrait<?>, Comparable<?>> traitValues) {
        this.traitValues = traitValues;
        this.baseState = baseState;
    }

    @Override
    public DataContainer toContainer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockType getType() {
        return this.baseState.getBlockType();
    }

    @Override
    public <T extends ImmutableDataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends ImmutableDataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supports(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <E> Optional<BlockState> transform(Key<? extends BaseValue<E>> key, Function<E, E> function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> Optional<BlockState> with(Key<? extends BaseValue<E>> key, E value) {
        if (!this.supports(key) || !((BlockTraitKey) key).getBlockTrait().getPredicate().apply(value)) {
            return Optional.absent();
        }
        return Optional.of(this.propertyValueTable.row(((BlockTraitKey) key).getBlockTrait()).get(value));
    }

    @Override
    public Optional<BlockState> with(BaseValue<?> value) {
        if (!this.supports(value)) {
            return Optional.absent();
        }
        return Optional.of(this.propertyValueTable.row(((BlockTraitKey) value.getKey()).getBlockTrait())
                .get(value.get()));
    }

    @Override
    public Optional<BlockState> with(ImmutableDataManipulator<?, ?> valueContainer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<BlockState> with(Iterable<ImmutableDataManipulator<?, ?>> valueContainers) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<BlockState> without(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockState merge(BlockState that) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockState merge(BlockState that, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
        if (!this.supports(key)) {
            return Optional.absent();
        }
        BlockTrait<?> blockTrait = ((BlockTraitKey) key).getBlockTrait();
        return Optional.fromNullable((E) this.traitValues.get(blockTrait));
    }

    @Override
    public <E> E getOrNull(Key<? extends BaseValue<E>> key) {
        return this.getOrElse(key, null);
    }

    @Override
    public <E> E getOrElse(Key<? extends BaseValue<E>> key, E defaultValue) {
        if (!this.supports(key)) {
            return defaultValue;
        }
        BlockTrait<?> blockTrait = ((BlockTraitKey) key).getBlockTrait();
        return (E) this.traitValues.get(blockTrait);
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
        if (!this.supports(key)) {
            return Optional.absent();
        }
        BlockTrait<?> blockTrait = ((BlockTraitKey) key).getBlockTrait();
        return Optional.of((V) new MutableBlockTraitValue(((BlockTraitKey) key), this.traitValues.get(blockTrait)));
    }

    @Override
    public boolean supports(Key<?> key) {
        return key instanceof BlockTraitKey && this.supportsTrait(((BlockTraitKey) key).getBlockTrait());
    }

    @Override
    public boolean supports(BaseValue<?> baseValue) {
        Key<?> key = baseValue.getKey();
        return this.supports(key) && ((BlockTraitKey) key).getBlockTrait().getPredicate().apply(baseValue.get());
    }

    @Override
    public BlockState copy() {
        // Should be safe to do this, this class is immutable
        return this;
    }

    @Override
    public ImmutableSet<Key<?>> getKeys() {
        return this.baseState.getKeys();
    }

    @Override
    public ImmutableSet<ImmutableValue<?>> getValues() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockState cycleValue(Key<? extends BaseValue<? extends Cycleable<?>>> key) {
        if (!this.supports(key)) {
            return this;
        }
        return (BlockState) this.cycleTraitValue(((BlockTraitKey) key).getBlockTrait()).get();
    }

    @Override
    public BlockSnapshot snapshotFor(Location<World> location) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ImmutableDataManipulator<?, ?>> getManipulators() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ImmutableDataManipulator<?, ?>> getContainers() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Cycles to the next possible value of the block trait and returns
     * the new block state. Will return absent if the block trait or
     * the value isn't supported.
     * 
     * @param blockTrait the block trait
     * @return the block state if successful
     */
    public <T extends Comparable<T>> Optional<BlockState> cycleTraitValue(BlockTrait<T> blockTrait) {
        if (!this.supportsTrait(blockTrait)) {
            return Optional.absent();
        }

        T value = (T) this.traitValues.get(blockTrait);
        Iterator<T> it = blockTrait.getPossibleValues().iterator();

        while (it.hasNext()) {
            if (it.next() == value) {
                if (it.hasNext()) {
                    value = it.next();
                } else {
                    value = blockTrait.getPossibleValues().iterator().next();
                }
            }
        }

        return this.setTraitValue(blockTrait, value);
    }

    /**
     * Attempts to set the trait value and gets the new block state. Will return absent
     * if the block trait or the value isn't supported.
     * 
     * @param blockTrait the block trait
     * @param value the value
     * @return the new block state if successful
     */
    public <T extends Comparable<T>> Optional<BlockState> setTraitValue(BlockTrait<T> blockTrait, T value) {
        if (!this.supportsTraitValue(blockTrait, value)) {
            return Optional.absent();
        }
        return Optional.of(this.propertyValueTable.row(blockTrait).get(value));
    }

    /**
     * Gets whether this block state the specified trait supports.
     * 
     * @param blockTrait the block trait
     * @return whether the block trait is supported
     */
    public <T extends Comparable<T>> boolean supportsTrait(BlockTrait<T> blockTrait) {
        return this.traitValues.containsKey(blockTrait);
    }

    /**
     * Gets whether this block state the specified trait supports.
     * 
     * @param blockTrait the block trait
     * @param value the value
     * @return whether the block trait and value are supported
     */
    public <T extends Comparable<T>> boolean supportsTraitValue(BlockTrait<T> blockTrait, T value) {
        return this.supportsTrait(blockTrait) && blockTrait.getPredicate().apply(value);
    }

    @Override
    public <T extends Comparable<T>> Optional<T> getTraitValue(BlockTrait<T> blockTrait) {
        return Optional.fromNullable((T) this.traitValues.get(blockTrait));
    }

    @Override
    public Optional<BlockTrait<?>> getTrait(String blockTrait) {
        return this.baseState.getTrait(blockTrait);
    }

    @Override
    public Collection<BlockTrait<?>> getTraits() {
        return this.traitValues.keySet();
    }

    @Override
    public Collection<?> getTraitValues() {
        return this.traitValues.values();
    }

    @Override
    public Map<BlockTrait<?>, ?> getTraitMap() {
        return this.traitValues;
    }
}
