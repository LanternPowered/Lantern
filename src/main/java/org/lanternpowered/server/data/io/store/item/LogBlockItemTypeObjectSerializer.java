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
package org.lanternpowered.server.data.io.store.item;

import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;

public class LogBlockItemTypeObjectSerializer implements ItemTypeObjectSerializer {

    @Override
    public void serializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        final BlockType blockType = itemStack.getItem().getBlock().get();
        // TODO
        final BlockState blockState = blockType.getDefaultState();//.with(Keys.TREE_TYPE, itemStack.get(Keys.TREE_TYPE).get()).get();
        dataView.set(DATA_VALUE, BlockRegistryModule.get().getStateData(blockState));
    }

    @Override
    public void deserializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        final BlockType blockType = itemStack.getItem().getBlock().get();
        final BlockState blockState = BlockRegistryModule.get().getStateByTypeAndData(blockType,
                dataView.getShort(DATA_VALUE).orElse((short) 0).byteValue()).get();
        valueContainer.set(Keys.TREE_TYPE, blockState.get(Keys.TREE_TYPE).get());
    }
}
