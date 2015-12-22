/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.block;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.util.persistence.InvalidDataException;

public class LanternBlockStateBuilder implements BlockState.Builder {

    private BlockState blockState;

    public LanternBlockStateBuilder() {
        this.reset();
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
        return this.add((ImmutableDataManipulator) manipulator.asImmutable());
    }

    @Override
    public LanternBlockStateBuilder add(ImmutableDataManipulator<?, ?> manipulator) {
        final Optional<BlockState> optional = this.blockState.with(manipulator);
        if (optional.isPresent()) {
            this.blockState = optional.get();
        }
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
    public Optional<BlockState> build(DataView container) throws InvalidDataException {
        // TODO Auto-generated method stub
        return null;
    }
}
