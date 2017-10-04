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
package org.lanternpowered.server.item.property;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.event.CauseStack;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.item.PropertyProvider;
import org.lanternpowered.server.item.tool.ToolAspect;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.property.item.HarvestingProperty;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("ConstantConditions")
public final class HarvestingPropertyProvider implements PropertyProvider<HarvestingProperty> {

    public static final HarvestingPropertyProvider INSTANCE = new HarvestingPropertyProvider();

    private final Map<ItemType, HarvestingProperty> cache = new ConcurrentHashMap<>();

    private HarvestingPropertyProvider() {
    }

    @Nullable
    @Override
    public HarvestingProperty get(ItemType itemType, @Nullable ItemStack itemStack) {
        return this.cache.computeIfAbsent(itemType, type -> {
            final ImmutableSet.Builder<BlockType> blockTypes = ImmutableSet.builder();
            final ToolAspectsProperty toolAspectsProperty = itemType
                    .getDefaultProperty(ToolAspectsProperty.class).orElse(null);
            final CauseStack causeStack = CauseStack.currentOrEmpty();
            final ItemStack itemStack1 = ItemStack.of(itemType, 1);
            if (toolAspectsProperty != null) {
                for (BlockType blockType : BlockRegistryModule.get().getAll()) {
                    for (ToolAspect aspect : toolAspectsProperty.getValue()) {
                        if (aspect.isHarvestable(causeStack, blockType.getDefaultState(), itemStack1)) {
                            blockTypes.add(blockType);
                            break;
                        }
                    }
                }
            }
            final ImmutableSet<BlockType> set = blockTypes.build();
            return set.isEmpty() ? null : new HarvestingProperty(set);
        });
    }

    public void invalidate() {
        this.cache.clear();
    }
}
