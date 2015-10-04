package org.lanternpowered.server.block.state;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.lanternpowered.server.block.trait.LanternBlockTrait;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.key.Key;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class LanternBlockStateBase {

    private final ImmutableMap<String, BlockTrait<?>> blockTraits;
    private final ImmutableList<BlockState> blockStates;
    private final ImmutableSet<Key<?>> keys;
    private final BlockType blockType;

    @SuppressWarnings("rawtypes")
    public LanternBlockStateBase(BlockType blockType, Iterable<BlockTrait<?>> blockTraits) {
        this.blockType = blockType;

        // There are no block traits
        if (!blockTraits.iterator().hasNext()) {
            LanternBlockState blockState = new LanternBlockState(this, ImmutableMap.of());
            blockState.propertyValueTable = ImmutableTable.of();
            this.blockStates = ImmutableList.of(blockState);
            this.blockTraits = ImmutableMap.of();
            this.keys = ImmutableSet.of();
            return;
        }

        // Convert to a list so it can be sorted
        List<BlockTrait<?>> list = Lists.newArrayList(blockTraits);

        // Sort the traits be name
        Collections.sort(list, (o1, o2) -> o1.getName().compareTo(o2.getName()));

        // The builder for the name to trait lookup
        ImmutableMap.Builder<String, BlockTrait<?>> builder = ImmutableMap.builder();
        ImmutableSet.Builder<Key<?>> keys = ImmutableSet.builder();

        // All the sets with all the allowed values
        List<Set<Comparable<?>>> allowedValues = Lists.newArrayList();

        for (BlockTrait<?> trait : list) {
            allowedValues.add(Sets.newHashSet(trait.getPossibleValues()));
            keys.add(((LanternBlockTrait) trait).getKey());
            builder.put(trait.getName(), trait);
        }

        // Build the lookups
        this.blockTraits = builder.build();
        this.keys = keys.build();

        // A map with as the key the trait values map and as value the state
        LinkedHashMap<Map<?, ?>, BlockState> stateByValuesMap = Maps.newLinkedHashMap();

        // The block states
        ImmutableList.Builder<BlockState> blockStates = ImmutableList.builder();

        // Do the cartesian product to get all the possible combinations
        Iterator<List<Comparable<?>>> cartesianProductIt = Sets.cartesianProduct(allowedValues).iterator();
        while (cartesianProductIt.hasNext()) {
            Iterator<Comparable<?>> objectsIt = cartesianProductIt.next().iterator();

            ImmutableMap.Builder<BlockTrait<?>, Comparable<?>> traitValuesBuilder = ImmutableMap.builder();
            for (BlockTrait<?> trait : list) {
                traitValuesBuilder.put(trait, objectsIt.next());
            }

            ImmutableMap<BlockTrait<?>, Comparable<?>> traitValues = traitValuesBuilder.build();
            LanternBlockState blockState = new LanternBlockState(this, traitValues);
            stateByValuesMap.put(traitValues, blockState);
            blockStates.add(blockState);
        }

        this.blockStates = blockStates.build();

        Iterator<BlockState> blockStateIt = this.blockStates.iterator();
        while (blockStateIt.hasNext()) {
            LanternBlockState blockState = (LanternBlockState) blockStateIt.next();
            HashBasedTable<BlockTrait<?>, Comparable<?>, BlockState> table = HashBasedTable.create();

            for (BlockTrait<?> trait : list) {
                for (Comparable<?> value : trait.getPossibleValues()) {
                    if (value != blockState.getTraitValue(trait).get()) {
                        Map<BlockTrait<?>, Comparable<?>> valueByTrait = Maps.newHashMap();
                        valueByTrait.putAll(blockState.traitValues);
                        valueByTrait.put(trait, value);

                        table.put(trait, value, stateByValuesMap.get(valueByTrait));
                    }
                }
            }

            blockState.propertyValueTable = ImmutableTable.copyOf(table);
        }
    }

    public ImmutableSet<Key<?>> getKeys() {
        return this.keys;
    }

    public BlockType getBlockType() {
        return this.blockType;
    }

    public BlockState getDefaultBlockState() {
        return this.blockStates.get(0);
    }

    public Collection<BlockState> getBlockStates() {
        return this.blockStates;
    }

    public Collection<BlockTrait<?>> getTraits() {
        return this.blockTraits.values();
    }

    public Optional<BlockTrait<?>> getTrait(String name) {
        if (this.blockTraits.containsKey(checkNotNull(name, "name"))) {
            return Optional.of(this.blockTraits.get(name));
        }
        return Optional.empty();
    }
}
