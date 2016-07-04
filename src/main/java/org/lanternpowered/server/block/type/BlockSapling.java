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

import org.lanternpowered.server.block.PropertyProviderCollections;
import org.lanternpowered.server.block.PropertyProviders;
import org.lanternpowered.server.block.trait.LanternEnumTrait;
import org.lanternpowered.server.block.trait.LanternIntegerTrait;
import org.lanternpowered.server.data.type.LanternTreeType;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.block.trait.IntegerTrait;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;

import java.util.function.Function;

import javax.annotation.Nullable;

public final class BlockSapling extends VariantBlock<LanternTreeType> {

    @SuppressWarnings("unchecked")
    public static final EnumTrait<LanternTreeType> TYPE = LanternEnumTrait.of("type", (Key) Keys.TREE_TYPE, LanternTreeType.class);
    public static final IntegerTrait STAGE = LanternIntegerTrait.of("stage", Keys.GROWTH_STAGE, 0, 1);

    public BlockSapling(String pluginId, String identifier, @Nullable Function<BlockType, ItemType> itemTypeBuilder) {
        super(pluginId, identifier, itemTypeBuilder, TYPE, STAGE);
        this.modifyDefaultState(state -> state.withTrait(TYPE, LanternTreeType.OAK).get().withTrait(STAGE, 0).get());
        this.modifyPropertyProviders(builder -> {
            builder.add(PropertyProviderCollections.PASSABLE);
            builder.add(PropertyProviders.hardness(0.0));
            builder.add(PropertyProviders.blastResistance(0.0));
        });
    }

    @Override
    protected String getTranslationKey(LanternTreeType element) {
        return "tile.sapling." + element.getTranslationKeyBase() + ".name";
    }
}
