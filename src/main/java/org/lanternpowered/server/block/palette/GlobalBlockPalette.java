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
        return InternalIDRegistries.BLOCK_TYPE_IDS.size() - 1; // Not all blocks may be assigned yet
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
