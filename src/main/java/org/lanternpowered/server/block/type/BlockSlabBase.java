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

import org.apache.commons.lang3.ArrayUtils;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.PropertyProviders;
import org.lanternpowered.server.block.trait.LanternEnumTrait;
import org.lanternpowered.server.data.type.LanternPortionType;
import org.lanternpowered.server.item.BlockItemSlab;
import org.lanternpowered.server.item.BlockItemType;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;

import java.util.function.Function;

import javax.annotation.Nullable;

public abstract class BlockSlabBase extends LanternBlockType {

    public static final Function<BlockType, ItemType> ITEM_TYPE_BUILDER =
            type -> new BlockItemSlab(((LanternBlockType) type).getPluginId(), type.getName(), (BlockSlabBase) type);

    @SuppressWarnings("unchecked")
    public static final EnumTrait<LanternPortionType> PORTION = LanternEnumTrait.of("half", (Key) Keys.PORTION_TYPE, LanternPortionType.class);

    private final EnumTrait<?> variantTrait;
    protected final boolean doubleBlock;

    public BlockSlabBase(String pluginId, String identifier, String translationKey, @Nullable Function<BlockType, ItemType> itemTypeBuilder,
            boolean doubleBlock, EnumTrait<?> variantTrait, BlockTrait<?>... blockTraits) {
        super(pluginId, identifier, translationKey, itemTypeBuilder, ArrayUtils.add(blockTraits, variantTrait));
        this.variantTrait = variantTrait;
        this.doubleBlock = doubleBlock;
        modifyPropertyProviders(builder -> {
            if (!doubleBlock) {
                builder.add(PropertyProviders.solidCube(false));
            }
        });
    }

    public EnumTrait<?> getVariantTrait() {
        return this.variantTrait;
    }

    public boolean isHalf() {
        return !this.doubleBlock;
    }

    public boolean isDouble() {
        return this.doubleBlock;
    }

    public abstract BlockType getHalf();

    public abstract BlockType getDouble();
}
