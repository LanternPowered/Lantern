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

import com.google.common.collect.Iterables;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.translation.Translation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

public class VariantBlock<E extends CatalogType & Comparable<E>> extends LanternBlockType {

    private final Map<E, Translation> translations = new HashMap<>();
    protected final BlockTrait<E> variantTrait;

    public VariantBlock(String pluginId, String identifier, @Nullable Function<BlockType, ItemType> itemTypeBuilder,
            BlockTrait<E> enumTrait, BlockTrait<?>... blockTraits) {
        this(pluginId, identifier, itemTypeBuilder, enumTrait, Arrays.asList(blockTraits));
    }

    public VariantBlock(String pluginId, String identifier, @Nullable Function<BlockType, ItemType> itemTypeBuilder,
            BlockTrait<E> variantTrait, Iterable<BlockTrait<?>> blockTraits) {
        super(pluginId, identifier, itemTypeBuilder, Iterables.concat(Collections.singleton(variantTrait), blockTraits));

        this.variantTrait = variantTrait;
        String pluginPath = "";
        if (!getPluginId().equalsIgnoreCase("minecraft")) {
            pluginPath = getPluginId() + '.';
        }
        for (E element : variantTrait.getPossibleValues()) {
            this.translations.put(element, Lantern.getRegistry().getTranslationManager().get(
                    pluginPath + getTranslationKey(element)));
        }
    }

    public BlockTrait<E> getVariantTrait() {
        return this.variantTrait;
    }

    protected String getTranslationKey(E element) {
        final String id = element.getId().toLowerCase();
        return "tile." + id + ".name";
    }

    @Override
    public Translation getTranslation(BlockState blockState) {
        return blockState.getTraitValue(this.variantTrait).map(this.translations::get)
                .orElseGet(() -> super.getTranslation(blockState));
    }
}
