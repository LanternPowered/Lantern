/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.block.palette;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.schematic.BlockPalette;
import org.spongepowered.api.world.schematic.BlockPaletteType;
import org.spongepowered.api.world.schematic.BlockPaletteTypes;

import java.util.BitSet;
import java.util.Collection;
import java.util.Optional;

public class LocalBlockPalette implements BlockPalette {

    private static final int DEFAULT_ALLOCATION_SIZE = 64;
    private static final int INVALID_ID = -1;

    private final Object2IntMap<BlockState> idByState = new Object2IntOpenHashMap<>();
    private final Int2ObjectMap<BlockState> stateById = new Int2ObjectOpenHashMap<>();

    {
        this.idByState.defaultReturnValue(INVALID_ID);
    }

    private final BitSet allocation = new BitSet(DEFAULT_ALLOCATION_SIZE);
    private int highestId = 0;

    @Override
    public BlockPaletteType getType() {
        return BlockPaletteTypes.LOCAL;
    }

    @Override
    public int getHighestId() {
        return this.highestId;
    }

    @Override
    public Optional<Integer> get(BlockState state) {
        checkNotNull(state, "state");
        final int id = this.idByState.getInt(state);
        return id == INVALID_ID ? Optional.empty() : Optional.of(id);
    }

    @Override
    public int getOrAssign(BlockState state) {
        checkNotNull(state, "state");
        final int id = this.idByState.getInt(state);
        if (id != INVALID_ID) {
            return id;
        }
        final int next = this.allocation.nextClearBit(0);
        if (this.highestId < id) {
            this.highestId = id;
        }
        this.allocation.set(id);
        this.stateById.put(id, state);
        this.idByState.put(state, id);
        return next;
    }

    @Override
    public Optional<BlockState> get(int id) {
        return Optional.ofNullable(this.stateById.get(id));
    }

    @Override
    public boolean remove(BlockState state) {
        final int id = this.idByState.getInt(state);
        if (id == INVALID_ID) {
            return false;
        }
        this.allocation.clear(id);
        if (id == this.highestId) {
            this.highestId = this.allocation.previousSetBit(this.highestId);
        }
        this.stateById.remove(id);
        this.idByState.removeInt(state);
        return true;
    }

    @Override
    public Collection<BlockState> getEntries() {
        return ImmutableSet.copyOf(this.idByState.keySet());
    }
}
