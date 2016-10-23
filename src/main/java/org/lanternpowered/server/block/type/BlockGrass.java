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
package org.lanternpowered.server.block.type;

import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.PropertyProviders;
import org.lanternpowered.server.block.trait.LanternBooleanTrait;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BooleanTrait;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;

import java.util.function.Function;

import javax.annotation.Nullable;

public final class BlockGrass extends LanternBlockType {

    public static final BooleanTrait SNOWY = LanternBooleanTrait.of("snowy", Keys.SNOWED);

    public BlockGrass(String pluginId, String identifier, @Nullable Function<BlockType, ItemType> itemTypeBuilder) {
        super(pluginId, identifier, itemTypeBuilder, SNOWY);
        modifyDefaultState(state -> state.withTrait(SNOWY, false).get());
        modifyPropertyProviders(builder -> {
            builder.add(PropertyProviders.hardness(0.6));
            builder.add(PropertyProviders.blastResistance(3.0));
        });
    }

    @Override
    public BlockState removeExtendedState(BlockState blockState) {
        return blockState.withTrait(SNOWY, false).get();
    }
}
