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
package org.lanternpowered.server.block.behavior.simple.drops;

import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.Parameters;
import org.lanternpowered.server.block.behavior.simple.AbstractBlockDropsProviderBehavior;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;

public class PlacedBlockDropsProviderBehavior extends AbstractBlockDropsProviderBehavior {

    @Override
    protected void collectDrops(BehaviorContext context, List<ItemStackSnapshot> itemStacks) {
        final Location<World> location = context.tryGet(Parameters.BLOCK_LOCATION);
        final BlockState blockState = location.getBlock();
        blockState.getType().getItem().ifPresent(itemType -> {
            final ItemStack itemStack = new LanternItemStack(itemType);// ItemStack.of(itemType, 1); TODO
            blockState.getValues().forEach(itemStack::offer);
            location.getTileEntity().ifPresent(tile -> tile.getValues().forEach(itemStack::offer));
            itemStacks.add(itemStack.createSnapshot());
        });
    }
}
