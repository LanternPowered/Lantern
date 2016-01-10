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
package org.lanternpowered.server.data.property.block;

import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.data.property.common.AbstractBlockPropertyStore;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.util.Direction;

import java.util.Optional;

import javax.annotation.Nullable;

public final class MatterPropertyStore extends AbstractBlockPropertyStore<MatterProperty> {

    private static final Optional<MatterProperty> SOLID = Optional.of(new MatterProperty(MatterProperty.Matter.SOLID));
    private static final Optional<MatterProperty> LIQUID = Optional.of(new MatterProperty(MatterProperty.Matter.LIQUID));
    private static final Optional<MatterProperty> GAS = Optional.of(new MatterProperty(MatterProperty.Matter.GAS));

    @Override
    public Optional<MatterProperty> getFor(BlockState blockState, @Nullable Direction direction) {
        MatterProperty.Matter matter = ((LanternBlockType) blockState.getType()).getMatter(blockState);
        if (matter == MatterProperty.Matter.SOLID) {
            return SOLID;
        } else if (matter == MatterProperty.Matter.LIQUID) {
            return LIQUID;
        } else {
            return GAS;
        }
    }
}
