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
package org.lanternpowered.server.block;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.data.DataHelper.checkDataExists;

import org.lanternpowered.server.data.DataQueries;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.Value;

import java.util.Optional;

public class LanternBlockStateBuilder extends AbstractDataBuilder<BlockState> implements BlockState.Builder {

    private BlockState blockState;

    public LanternBlockStateBuilder() {
        super(BlockState.class, 1);
        reset();
    }

    @Override
    public LanternBlockStateBuilder reset() {
        this.blockState = BlockTypes.STONE.getDefaultState();
        return this;
    }

    @Override
    public LanternBlockStateBuilder blockType(BlockType blockType) {
        this.blockState = checkNotNull(blockType, "blockType").getDefaultState();
        return this;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public LanternBlockStateBuilder add(DataManipulator<?, ?> manipulator) {
        return add(manipulator.asImmutable());
    }

    @Override
    public LanternBlockStateBuilder add(ImmutableDataManipulator<?, ?> manipulator) {
        this.blockState.with(manipulator).ifPresent(blockState -> this.blockState = blockState);
        return this;
    }

    @Override
    public <V> BlockState.Builder add(Key<? extends Value<V>> key, V value) {
        this.blockState.with(key, value).ifPresent(blockState -> this.blockState = blockState);
        return this;
    }

    @Override
    public LanternBlockStateBuilder from(BlockState holder) {
        this.blockState = checkNotNull(holder, "holder");
        return this;
    }

    @Override
    public BlockState build() {
        return this.blockState;
    }

    @Override
    protected Optional<BlockState> buildContent(DataView container) throws InvalidDataException {
        if (!container.contains(DataQueries.BLOCK_STATE)) {
            return Optional.empty();
        }
        checkDataExists(container, DataQueries.BLOCK_STATE);
        try {
            return container.getCatalogType(DataQueries.BLOCK_STATE, BlockState.class);
        } catch (Exception e) {
            throw new InvalidDataException("Could not retrieve a block state!", e);
        }
    }
}
