/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.block.state;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.lanternpowered.server.block.LanternBlockSnapshot;
import org.lanternpowered.server.block.trait.BlockTraitKey;
import org.lanternpowered.server.block.trait.MutableBlockTraitValue;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.util.Cycleable;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class LanternBlockState implements BlockState {

    // A lookup table to get a specific state when you would change a value
    ImmutableTable<BlockTrait<?>, Comparable<?>, BlockState> propertyValueTable;

    // The values for every attached trait
    final ImmutableMap<BlockTrait<?>, Comparable<?>> traitValues;

    // The base block state
    private final LanternBlockStateBase baseState;

    LanternBlockState(LanternBlockStateBase baseState, ImmutableMap<BlockTrait<?>, Comparable<?>> traitValues) {
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
        if (!this.supports(key) || !((BlockTraitKey) key).getBlockTrait().getPredicate().test(value)) {
            return Optional.empty();
        }
        return Optional.of(this.propertyValueTable.row(((BlockTraitKey) key).getBlockTrait()).get(value));
    }

    @Override
    public Optional<BlockState> with(BaseValue<?> value) {
        if (!this.supports(value)) {
            return Optional.empty();
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
        // You cannot remove any data manipulators from a block state
        return Optional.empty();
    }

    @Override
    public BlockState merge(BlockState that) {
        // Huh? Not sure what to do here...
        return this;
    }

    @Override
    public BlockState merge(BlockState that, MergeFunction function) {
        // Huh? Not sure what to do here...
        return this;
    }

    @Override
    public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
        if (!this.supports(key)) {
            return Optional.empty();
        }
        BlockTrait<?> blockTrait = ((BlockTraitKey) key).getBlockTrait();
        return Optional.ofNullable((E) this.traitValues.get(blockTrait));
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
        if (!this.supports(key)) {
            return Optional.empty();
        }
        BlockTrait<?> blockTrait = ((BlockTraitKey) key).getBlockTrait();
        return Optional.of((V) new MutableBlockTraitValue(((BlockTraitKey) key), this.traitValues.get(blockTrait)));
    }

    @Override
    public boolean supports(Key<?> key) {
        return key instanceof BlockTraitKey && this.supportsTrait(((BlockTraitKey) key).getBlockTrait());
    }

    @Override
    public BlockState copy() {
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
        return new LanternBlockSnapshot(location, this);
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
            return Optional.empty();
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

        return this.withTrait(blockTrait, value);
    }

    @Override
    public Optional<BlockState> withTrait(BlockTrait<?> trait, Object value) {
        if (value instanceof String) {
            if (!this.supportsTrait(trait)) {
                return Optional.empty();
            }
            for (Object object : trait.getPossibleValues()) {
                if (object.toString().equals(value)) {
                    value = object;
                    break;
                }
            }
            if (value instanceof String) {
                return Optional.empty();
            }
        }
        if (!this.supportsTraitValue(trait, value)) {
            return Optional.empty();
        }
        return Optional.of(this.propertyValueTable.row(trait).get(value));
    }

    /**
     * Gets whether this block state the specified trait supports.
     * 
     * @param blockTrait the block trait
     * @return whether the block trait is supported
     */
    public boolean supportsTrait(BlockTrait<?> blockTrait) {
        return this.traitValues.containsKey(blockTrait);
    }

    /**
     * Gets whether this block state the specified trait supports.
     * 
     * @param blockTrait the block trait
     * @param value the value
     * @return whether the block trait and value are supported
     */
    public boolean supportsTraitValue(BlockTrait<?> blockTrait, Object value) {
        return this.supportsTrait(blockTrait) && ((Predicate) blockTrait.getPredicate()).test(value);
    }

    @Override
    public <T extends Comparable<T>> Optional<T> getTraitValue(BlockTrait<T> blockTrait) {
        return Optional.ofNullable((T) this.traitValues.get(blockTrait));
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

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Class<T> propertyClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Property<?, ?>> getApplicableProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Direction direction) {
        // TODO Auto-generated method stub
        return null;
    }
}
