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

import org.lanternpowered.server.game.registry.InternalIDRegistries;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.schematic.BlockPalette;
import org.spongepowered.api.world.schematic.BlockPaletteType;
import org.spongepowered.api.world.schematic.BlockPaletteTypes;

import java.util.Collection;
import java.util.Optional;

public final class GlobalBlockPalette implements BlockPalette {

    public static GlobalBlockPalette INSTANCE = new GlobalBlockPalette();

    private GlobalBlockPalette() {
    }

    @Override
    public BlockPaletteType getType() {
        return BlockPaletteTypes.GLOBAL;
    }

    @Override
    public int getHighestId() {
        return InternalIDRegistries.BLOCK_STATE_START_IDS.size() - 1; // Not all blocks may be assigned yet
    }

    @Override
    public Optional<Integer> get(BlockState state) {
        return Optional.of(BlockRegistryModule.get().getStateInternalId(state));
    }

    @Override
    public int getOrAssign(BlockState state) {
        return BlockRegistryModule.get().getStateInternalId(state);
    }

    @Override
    public Optional<BlockState> get(int id) {
        return BlockRegistryModule.get().getStateByInternalId(id);
    }

    @Override
    public boolean remove(BlockState state) {
        throw new UnsupportedOperationException("Cannot remove BlockStates from the global palette.");
    }

    @Override
    public Collection<BlockState> getEntries() {
        return Sponge.getRegistry().getAllOf(BlockState.class);
    }
}
