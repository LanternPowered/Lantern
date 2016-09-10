/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.server.block.type;

import org.lanternpowered.server.block.PropertyProviders;
import org.lanternpowered.server.block.trait.LanternBooleanTrait;
import org.lanternpowered.server.data.type.LanternPortionType;
import org.lanternpowered.server.data.type.LanternSlabType;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BooleanTrait;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;

import java.util.function.Function;

import javax.annotation.Nullable;

public abstract class BlockStoneSlabBase extends BlockSlabBase {

    public static final BooleanTrait SEAMLESS = LanternBooleanTrait.of("seamless", Keys.SEAMLESS);

    public BlockStoneSlabBase(String pluginId, String identifier, String translationKey,
            @Nullable Function<BlockType, ItemType> itemTypeBuilder, boolean doubleSlab, EnumTrait<LanternSlabType> variantTrait) {
        super(pluginId, identifier, translationKey, itemTypeBuilder, doubleSlab, variantTrait,
                doubleSlab ? SEAMLESS : PORTION);
        this.modifyPropertyProviders(builder -> {
            builder.add(PropertyProviders.hardness(2.0));
            builder.add(PropertyProviders.blastResistance(10.0));
        });
        this.modifyDefaultState(state -> {
            if (doubleSlab) {
                return state.withTrait(SEAMLESS, false).get();
            } else {
                return state.withTrait(PORTION, LanternPortionType.BOTTOM).get();
            }
        });
    }
}