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
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.type.TileEntityInventory;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collections;
import java.util.List;

public class InventoryDropsProviderBehavior extends AbstractBlockDropsProviderBehavior {

    @Override
    protected void collectDrops(BehaviorContext context, List<ItemStackSnapshot> itemStacks) {
        final Location<World> location = context.tryGet(Parameters.BLOCK_LOCATION);
        location.getTileEntity().ifPresent(tile -> {
            if (tile instanceof TileEntityCarrier) {
                final TileEntityInventory<TileEntityCarrier> inventory = ((TileEntityCarrier) tile).getInventory();
                final Iterable<Slot> slots = inventory instanceof Slot ? Collections.singleton((Slot) inventory) : inventory.slots();
                slots.forEach(slot -> slot.poll().ifPresent(itemStack -> itemStacks.add(itemStack.createSnapshot())));
            }
        });
    }
}
