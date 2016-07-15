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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.trait.LanternBlockTrait;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.key.Key;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class LanternBlockStateMap {

    private final ImmutableMap<String, BlockTrait<?>> blockTraits;
    private final ImmutableList<BlockState> blockStates;
    final ImmutableSet<Key<?>> keys;
    private final LanternBlockType blockType;

    @SuppressWarnings("rawtypes")
    public LanternBlockStateMap(LanternBlockType blockType, Iterable<BlockTrait<?>> blockTraits) {
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

        // Sort the traits by the name
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
        for (List<Comparable<?>> comparables : Sets.cartesianProduct(allowedValues)) {
            Iterator<Comparable<?>> objectsIt = comparables.iterator();

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
        this.blockStates.stream().map(state -> (LanternBlockState) state).forEach(state -> {
            ImmutableTable.Builder<BlockTrait<?>, Comparable<?>, BlockState> tableBuilder = ImmutableTable.builder();
            list.forEach(trait -> trait.getPossibleValues().stream().filter(value -> value != state.getTraitValue(trait).get()).forEach(value -> {
                Map<BlockTrait<?>, Comparable<?>> valueByTrait = Maps.newHashMap();
                valueByTrait.putAll(state.traitValues);
                valueByTrait.put(trait, value);
                tableBuilder.put(trait, value, stateByValuesMap.get(valueByTrait));
            }));
            state.propertyValueTable = tableBuilder.build();
        });

        this.blockStates.stream().map(state -> (LanternBlockState) state)
                .forEach(state -> state.extended = blockType.getExtendedBlockStateProvider().remove(state) != state);
    }

    public LanternBlockType getBlockType() {
        return this.blockType;
    }

    public BlockState getBaseState() {
        return this.blockStates.get(0);
    }

    public Collection<BlockState> getBlockStates() {
        return this.blockStates;
    }

    public Optional<BlockTrait<?>> getTrait(String name) {
        if (this.blockTraits.containsKey(checkNotNull(name, "name"))) {
            return Optional.of(this.blockTraits.get(name));
        }
        return Optional.empty();
    }
}
