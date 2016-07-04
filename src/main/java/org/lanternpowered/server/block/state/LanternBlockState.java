/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import org.lanternpowered.server.block.LanternBlockSnapshot;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.trait.LanternBlockTrait;
import org.lanternpowered.server.data.property.AbstractDirectionRelativePropertyHolder;
import org.lanternpowered.server.data.value.mutable.LanternValue;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.Cycleable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class LanternBlockState implements BlockState, AbstractDirectionRelativePropertyHolder {

    // A lookup table to get a specific state when you would change a value
    ImmutableTable<BlockTrait<?>, Comparable<?>, BlockState> propertyValueTable;

    // The values for every attached trait
    final ImmutableMap<BlockTrait<?>, Comparable<?>> traitValues;

    // The lookup to convert between key <--> trait
    private final ImmutableMap<Key<Value<?>>, BlockTrait<?>> keyToBlockTrait;

    // The base block state
    private final LanternBlockStateMap baseState;

    // The id of the block state
    private final String id;

    // Whether this state is extended
    boolean extended;

    LanternBlockState(LanternBlockStateMap baseState, ImmutableMap<BlockTrait<?>, Comparable<?>> traitValues) {
        this.traitValues = traitValues;
        this.baseState = baseState;

        ImmutableBiMap.Builder<Key<Value<?>>, BlockTrait<?>> builder = ImmutableBiMap.builder();
        for (BlockTrait trait : traitValues.keySet()) {
            builder.put(((LanternBlockTrait) trait).getKey(), trait);
        }
        this.keyToBlockTrait = builder.build();

        StringBuilder idBuilder = new StringBuilder();
        idBuilder.append(baseState.getBlockType().getId());
        if (!traitValues.isEmpty()) {
            idBuilder.append('[');
            Joiner joiner = Joiner.on(',');
            List<String> propertyValues = new ArrayList<>();
            for (Map.Entry<BlockTrait<?>, Comparable<?>> entry : traitValues.entrySet()) {
                propertyValues.add(entry.getKey().getName() + "=" + entry.getValue());
            }
            idBuilder.append(joiner.join(propertyValues));
            idBuilder.append(']');
        }
        this.id = idBuilder.toString();
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        final DataContainer dataContainer = new MemoryDataContainer();
        dataContainer.set(DataQuery.of("BlockType"), this.baseState.getBlockType().getId());
        for (Map.Entry<BlockTrait<?>, Comparable<?>> entry : this.traitValues.entrySet()) {
            Object value = entry.getValue();
            dataContainer.set(((LanternBlockTrait) entry.getKey()).getKey().getQuery(),
                    value instanceof CatalogType ? ((CatalogType) value).getId() : value);
        }
        return dataContainer;
    }

    @Override
    public LanternBlockType getType() {
        return this.baseState.getBlockType();
    }

    @Override
    public BlockState withExtendedProperties(Location<World> location) {
        return this.baseState.getBlockType().getExtendedState(this, checkNotNull(location, "location"));
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
        if (!this.supports(key)) {
            return Optional.empty();
        } else {
            E current = this.get(key).get();
            final E newVal = checkNotNull(function.apply(current));
            return this.with(key, newVal);
        }
    }

    @Override
    public <E> Optional<BlockState> with(Key<? extends BaseValue<E>> key, E value) {
        BlockTrait trait;
        if (!this.supports(key) || !(trait = this.keyToBlockTrait.get(key)).getPredicate().test(value)) {
            return Optional.empty();
        }
        if (this.traitValues.get(trait) == value) {
            return Optional.of(this);
        }
        return Optional.of(this.propertyValueTable.row(trait).get(value));
    }

    @Override
    public Optional<BlockState> with(BaseValue<?> value) {
        if (!this.supports(value)) {
            return Optional.empty();
        }
        BlockTrait trait = this.keyToBlockTrait.get(value.getKey());
        if (this.traitValues.get(trait) == value.get()) {
            return Optional.of(this);
        }
        return Optional.of(this.propertyValueTable.row(trait).get(value.get()));
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
        if (!this.getType().equals(that.getType())) {
            return this;
        } else {
            BlockState temp = this;
            for (ImmutableDataManipulator<?, ?> manipulator : that.getManipulators()) {
                Optional<BlockState> optional = temp.with(manipulator);
                if (optional.isPresent()) {
                    temp = optional.get();
                } else {
                    return temp;
                }
            }
            return temp;
        }
    }

    @Override
    public BlockState merge(BlockState that, MergeFunction function) {
        if (!getType().equals(that.getType())) {
            return this;
        } else {
            BlockState temp = this;
            for (ImmutableDataManipulator<?, ?> manipulator : that.getManipulators()) {
                @Nullable ImmutableDataManipulator old = temp.get(manipulator.getClass()).orElse(null);
                Optional<BlockState> optional = temp.with(checkNotNull(function.merge(old, manipulator)));
                if (optional.isPresent()) {
                    temp = optional.get();
                } else {
                    return temp;
                }
            }
            return temp;
        }
    }

    @Override
    public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
        if (!this.supports(key)) {
            return Optional.empty();
        }
        return Optional.ofNullable((E) this.traitValues.get(this.keyToBlockTrait.get(key)));
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
        if (!this.supports(key)) {
            return Optional.empty();
        }
        BlockTrait<?> blockTrait = this.keyToBlockTrait.get(key);
        return Optional.of((V) new LanternValue(key, this.traitValues.get(blockTrait)));
    }

    @Override
    public boolean supports(Key<?> key) {
        return this.keyToBlockTrait.containsKey(checkNotNull(key, "key"));
    }

    @Override
    public BlockState copy() {
        return this;
    }

    @Override
    public ImmutableSet<Key<?>> getKeys() {
        return this.baseState.keys;
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
        return this.cycleTraitValue(this.keyToBlockTrait.get(key)).get();
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
        checkNotNull(blockTrait, "blockTrait");

        if (!this.supportsTrait(blockTrait)) {
            return Optional.empty();
        }

        T value = (T) this.traitValues.get(blockTrait);
        if (value instanceof Cycleable) {
            T last = value;
            T next;
            while ((next = (T) ((Cycleable) last).cycleNext()) != value) {
                if (blockTrait.getPredicate().test(next)) {
                    value = next;
                    break;
                }
                last = next;
            }
        } else {
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
        }

        return this.withTrait(blockTrait, value);
    }

    @Override
    public Optional<BlockState> withTrait(BlockTrait<?> trait, Object value) {
        checkNotNull(trait, "trait");
        checkNotNull(value, "value");
        if (value instanceof String) {
            if (!this.supportsTrait(trait)) {
                return Optional.empty();
            }
            for (Object object : trait.getPossibleValues()) {
                if (object.toString().equals(value) || (object instanceof CatalogType && ((CatalogType) object).getId()
                        .equalsIgnoreCase((String) value))) {
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
        if (this.traitValues.get(trait) == value) {
            return Optional.of(this);
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
        return this.traitValues.containsKey(checkNotNull(blockTrait, "blockTrait"));
    }

    /**
     * Gets whether this block state the specified trait supports.
     * 
     * @param blockTrait the block trait
     * @param value the value
     * @return whether the block trait and value are supported
     */
    public boolean supportsTraitValue(BlockTrait<?> blockTrait, Object value) {
        return this.supportsTrait(checkNotNull(blockTrait, "blockTrait")) &&
                ((Predicate) blockTrait.getPredicate()).test(checkNotNull(value, "value"));
    }

    public boolean isExtended() {
        return this.extended;
    }

    @Override
    public <T extends Comparable<T>> Optional<T> getTraitValue(BlockTrait<T> blockTrait) {
        return Optional.ofNullable((T) this.traitValues.get(checkNotNull(blockTrait, "blockTrait")));
    }

    @Override
    public Optional<BlockTrait<?>> getTrait(String blockTrait) {
        return this.baseState.getTrait(checkNotNull(blockTrait, "blockTrait"));
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
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.id;
    }
}
