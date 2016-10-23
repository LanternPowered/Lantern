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

import org.lanternpowered.server.block.trait.LanternEnumTrait;
import org.lanternpowered.server.data.type.LanternSlabType;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;

import java.util.function.Function;

import javax.annotation.Nullable;

public class BlockStoneSlab1 extends BlockStoneSlabBase {

    @SuppressWarnings("unchecked")
    public static final EnumTrait<LanternSlabType> TYPE = LanternEnumTrait.of("variant", (Key) Keys.SLAB_TYPE, LanternSlabType.class,
            type -> type.ordinal() >= 0 && type.ordinal() < 8);

    public BlockStoneSlab1(String pluginId, String identifier, @Nullable Function<BlockType, ItemType> itemTypeBuilder, boolean doubleSlab) {
        super(pluginId, identifier, "stoneSlab", itemTypeBuilder, doubleSlab, TYPE);
        modifyDefaultState(state -> state.withTrait(TYPE, LanternSlabType.STONE).get());
    }

    @Override
    public BlockType getHalf() {
        return this.doubleBlock ? BlockTypes.STONE_SLAB : this;
    }

    @Override
    public BlockType getDouble() {
        return this.doubleBlock ? this : BlockTypes.DOUBLE_STONE_SLAB;
    }
}
