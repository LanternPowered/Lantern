/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.lanternpowered.server.block.LanternBlockSnapshot;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.entity.LanternBlockEntity;
import org.lanternpowered.server.block.trait.LanternBlockTrait;
import org.lanternpowered.server.catalog.AbstractCatalogType;
import org.lanternpowered.server.data.ImmutableDataHolder;
import org.lanternpowered.server.data.property.DirectionRelativePropertyHolderBase;
import org.lanternpowered.server.data.value.ValueFactory;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.Queries;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.state.StateProperty;
import org.spongepowered.api.util.Cycleable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.math.vector.Vector3i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings({"rawtypes", "unchecked", "SuspiciousMethodCalls"})
public final class LanternBlockState extends AbstractCatalogType implements CatalogType, BlockState,
        DirectionRelativePropertyHolderBase, ImmutableDataHolder<BlockState> {

    private static final DataQuery NAME = DataQuery.of("Name");
    private static final DataQuery PROPERTIES = DataQuery.of("Properties");

    // A lookup table to get a specific state when you would change a value
    ImmutableTable<BlockTrait<?>, Comparable<?>, BlockState> propertyValueTable;

    // The values for every attached trait
    final ImmutableMap<BlockTrait<?>, Comparable<?>> traitValues;

    // A list with all the values of this state
    private final ImmutableSet<Value.Immutable<?>> values;

    // The lookup to convert between key <--> trait
    private final ImmutableMap<Key<Value.Mutable<?>>, BlockTrait<?>> keyToBlockTrait;

    // The base block state
    private final LanternBlockStateMap baseState;

    // A cache to reuse constructed data manipulators
    private final ImmutableContainerCache immutableContainerCache = new ImmutableContainerCache();

    // The name of the block state
    private final CatalogKey key;

    // A internal id that is used for faster lookups
    private final int internalId;

    private final DataView serialized;

    @SuppressWarnings("RedundantCast")
    LanternBlockState(LanternBlockStateMap baseState, ImmutableMap<BlockTrait<?>, Comparable<?>> traitValues, int internalId) {
        this.traitValues = traitValues;
        this.baseState = baseState;
        this.internalId = internalId;

        final ImmutableBiMap.Builder<Key<Value.Mutable<?>>, BlockTrait<?>> keyToBlockTraitBuilder = ImmutableBiMap.builder();
        final ImmutableSet.Builder<Value.Immutable<?>> valuesBuilder = ImmutableSet.builder();
        for (Map.Entry<BlockTrait<?>, Comparable<?>> entry : traitValues.entrySet()) {
            final LanternBlockTrait trait = (LanternBlockTrait) entry.getKey();
            final Key key = (Key) trait.getValueKey();
            keyToBlockTraitBuilder.put(key, trait);
            final Value value = (Value) ValueFactory.INSTANCE.immutableOf(key, entry.getValue());
            valuesBuilder.add(value instanceof Value.Immutable ? (Value.Immutable) value : ((Value.Mutable) value).asImmutable());
        }
        this.keyToBlockTrait = keyToBlockTraitBuilder.build();
        this.values = valuesBuilder.build();

        // Serialize the block state
        this.serialized = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        this.serialized.set(NAME, baseState.getBlockType().getKey());

        final StringBuilder idBuilder = new StringBuilder();
        idBuilder.append(baseState.getBlockType().getKey().getValue());
        if (!traitValues.isEmpty()) {
            final DataView serializedProperties = this.serialized.createView(PROPERTIES);

            idBuilder.append('[');
            final Joiner joiner = Joiner.on(',');
            final List<String> propertyValues = new ArrayList<>();
            for (Map.Entry<BlockTrait<?>, Comparable<?>> entry : traitValues.entrySet()) {
                String value;
                final Comparable<?> comparable = entry.getValue();
                if (comparable instanceof Enum) {
                    value = ((Enum) comparable).name();
                } else {
                    value = comparable.toString();
                }
                value = value.toLowerCase();
                final String name = entry.getKey().getName();
                // Name generation
                propertyValues.add(name + "=" + value);
                // Serialized
                serializedProperties.set(DataQuery.of(name), value);
            }
            idBuilder.append(joiner.join(propertyValues));
            idBuilder.append(']');
        }
        this.key = CatalogKey.of(baseState.getBlockType().getKey().getNamespace(), idBuilder.toString());
    }

    public int getInternalId() {
        return this.internalId;
    }

    @Override
    public ImmutableContainerCache getContainerCache() {
        return this.immutableContainerCache;
    }

    @Override
    public DataContainer toContainer() {
        final DataContainer dataContainer = DataContainer.createNew();
        dataContainer.set(Queries.BLOCK_TYPE, this.baseState.getBlockType().getKey());
        for (Map.Entry<BlockTrait<?>, Comparable<?>> entry : this.traitValues.entrySet()) {
            final Object value = entry.getValue();
            dataContainer.set(((LanternBlockTrait) entry.getKey()).getValueKey().getQuery(), value);
        }
        return dataContainer;
    }

    @Override
    public LanternBlockType getType() {
        return this.baseState.getBlockType();
    }

    @Override
    public BlockState withExtendedProperties(Location location) {
        // Extended block states are no more, got removed
        // in 1.13, all states are now server side.
        return this;
    }

    @Override
    public <E> Optional<BlockState> transform(Key<? extends Value<E>> key, Function<E, E> function) {
        if (!this.supports(key)) {
            return Optional.empty();
        } else {
            final E current = get(key).get();
            final E newVal = checkNotNull(function.apply(current));
            return with(key, newVal);
        }
    }

    @Override
    public <E> Optional<BlockState> with(Key<? extends Value<E>> key, E value) {
        BlockTrait trait;
        if (!supports(key) || !(trait = this.keyToBlockTrait.get(key)).getPredicate().test(value)) {
            return Optional.empty();
        }
        if (((LanternBlockTrait) trait).getKeyTraitValueTransformer()
                .toKeyValue(this.traitValues.get(trait)) == value) {
            return Optional.of(this);
        }
        return Optional.of(this.propertyValueTable.row(trait).get(value));
    }

    @Override
    public Optional<BlockState> with(Value<?> value) {
        if (!supports(value)) {
            return Optional.empty();
        }
        final BlockTrait trait = this.keyToBlockTrait.get(value.getKey());
        if (((LanternBlockTrait) trait).getKeyTraitValueTransformer()
                .toKeyValue(this.traitValues.get(trait)) == value.get()) {
            return Optional.of(this);
        }
        return Optional.of(this.propertyValueTable.row(trait).get(value.get()));
    }

    @Override
    public Optional<BlockState> with(ImmutableDataManipulator<?, ?> valueContainer) {
        Optional<BlockState> state = null;
        for (Value.Immutable<?> value : valueContainer.getValues()) {
            state = with(value);
            if (!state.isPresent()) {
                return state;
            }
        }
        return state == null ? Optional.of(this) : state;
    }

    @Override
    public Optional<BlockState> with(Iterable<ImmutableDataManipulator<?, ?>> valueContainers) {
        Optional<BlockState> state = null;
        for (ImmutableDataManipulator<?, ?> valueContainer : valueContainers) {
            state = with(valueContainer);
            if (!state.isPresent()) {
                return state;
            }
        }
        return state == null ? Optional.of(this) : state;
    }

    @Override
    public Optional<BlockState> without(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        // You cannot remove any data manipulators from a block state
        return Optional.empty();
    }

    @Override
    public BlockState merge(BlockState that) {
        if (!getType().equals(that.getType())) {
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
                final ImmutableDataManipulator old = temp.get(manipulator.getClass()).orElse(null);
                final Optional<BlockState> optional = temp.with(checkNotNull(function.merge(old, manipulator)));
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
    public <E> Optional<E> get(Key<? extends Value<E>> key) {
        if (!supports(key)) {
            return Optional.empty();
        }
        final BlockTrait<?> blockTrait = this.keyToBlockTrait.get(key);
        return Optional.of((E) ((LanternBlockTrait) blockTrait)
                .getKeyTraitValueTransformer().toKeyValue(this.traitValues.get(blockTrait)));
    }

    @Override
    public <E, V extends Value<E>> Optional<V> getRawMutableValueFor(Key<V> key) {
        if (!supports(key)) {
            return Optional.empty();
        }
        final BlockTrait<?> blockTrait = this.keyToBlockTrait.get(key);
        return Optional.of((V) new LanternValue(key, ((LanternBlockTrait) blockTrait)
                .getKeyTraitValueTransformer().toKeyValue(this.traitValues.get(blockTrait))));
    }

    @Override
    public boolean supports(Key<?> key) {
        return this.keyToBlockTrait.containsKey(checkNotNull(key, "key"));
    }

    @Override
    public ImmutableSet<Key<?>> getKeys() {
        return this.baseState.keys;
    }

    @Override
    public ImmutableSet<Value.Immutable<?>> getValues() {
        return this.values;
    }

    @Override
    public BlockState cycleValue(Key<? extends Value<? extends Cycleable<?>>> key) {
        return supports(key) ? cycleTraitValue(this.keyToBlockTrait.get(key)).get() : this;
    }

    @Override
    public BlockSnapshot snapshotFor(Location location) {
        final World world = location.getWorld();
        final Vector3i pos = location.getBlockPosition();
        final LanternBlockEntity tileEntity = (LanternBlockEntity) getType().getBlockEntityProvider()
                .map(provider -> provider.get(this, location, null))
                .orElse(null);
        return new LanternBlockSnapshot(location, this, world.getCreator(pos).orElse(null),
                world.getNotifier(pos).orElse(null), tileEntity);
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

        if (!supportsTrait(blockTrait)) {
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
            final List<T> list = Lists.newArrayList(blockTrait.getPossibleValues());
            list.sort(Comparator.naturalOrder());
            final Iterator<T> it = list.iterator();
            while (it.hasNext()) {
                if (it.next() == value) {
                    if (it.hasNext()) {
                        value = it.next();
                    } else {
                        value = list.get(0);
                    }
                }
            }
        }

        return withTrait(blockTrait, value);
    }

    @Override
    public Optional<BlockState> withTrait(BlockTrait<?> trait, Object value) {
        checkNotNull(trait, "trait");
        checkNotNull(value, "value");
        if (value instanceof String) {
            if (!supportsTrait(trait)) {
                return Optional.empty();
            }
            for (Object object : trait.getPossibleValues()) {
                if (object.toString().equals(value) || (object instanceof CatalogType && ((CatalogType) object).getKey().toString()
                        .equalsIgnoreCase((String) value))) {
                    value = object;
                    break;
                }
            }
            if (value instanceof String) {
                return Optional.empty();
            }
        }
        if (!supportsTraitValue(trait, value)) {
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
        return supportsTrait(checkNotNull(blockTrait, "blockTrait")) &&
                ((Predicate) blockTrait.getPredicate()).test(checkNotNull(value, "value"));
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
    public CatalogKey getKey() {
        return this.key;
    }

    /**
     * Serializes the {@link BlockState} into the format
     * used by {@link BlockPalette} to store block states.
     * <p>The serialized data view should be copied before
     * modifying.
     *
     * {
     *   Name: "minecraft:furnace",
     *   Properties: {
     *     "lit": "true"
     *   }
     * }
     *
     * @param blockState The block state to serialize
     * @return The serialized block state
     */
    public static DataView serialize(BlockState blockState) {
        return ((LanternBlockState) blockState).serialized;
    }

    /**
     * Deserializes the {@link BlockState} from the format
     * used by {@link BlockPalette} to store block states.
     *
     * {
     *   Name: "minecraft:furnace",
     *   Properties: {
     *     "lit": "true"
     *   }
     * }
     *
     * @param dataView The data view to deserialize
     * @return The deserialized block state
     */
    public static BlockState deserialize(DataView dataView) {
        final String id = dataView.getString(NAME).get();
        final BlockType blockType = BlockRegistryModule.get().get(CatalogKey.resolve(id)).get();

        BlockState blockState = blockType.getDefaultState();
        final DataView properties = dataView.getView(PROPERTIES).orElse(null);
        if (properties != null) {
            for (Map.Entry<DataQuery, Object> entry : properties.getValues(false).entrySet()) {
                final StateProperty stateProperty = blockState.getStatePropertyByName(entry.getKey().toString()).orElse(null);
                if (stateProperty != null) {
                    final Comparable value = (Comparable) stateProperty.parseValue(entry.getValue().toString()).orElse(null);
                    if (value != null) {
                        final BlockState newState = (BlockState) blockState.withStateProperty(stateProperty, value).orElse(null);
                        if (newState != null) {
                            blockState = newState;
                        }
                    }
                }
            }
        }

        return blockState;
    }
}
