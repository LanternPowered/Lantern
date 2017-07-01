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
package org.lanternpowered.server.block.behavior.simple;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.Parameters;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.type.TileEntityInventory;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collections;
import java.util.List;

public abstract class SimpleBlockDropsProviderBehavior extends AbstractBlockDropsProviderBehavior {

    /**
     * Will generate drop {@link ItemStack}s based on the {@link Inventory}
     * contents of a {@link TileEntity}.
     *
     * @return The behavior
     */
    public static SimpleBlockDropsProviderBehavior fromInventory() {
        return Inventory.INSTANCE;
    }

    /**
     * Will generate a {@link ItemStack} based on the {@link BlockState}
     * and {@link TileEntity} values of the block.
     *
     * @return The behavior
     */
    public static SimpleBlockDropsProviderBehavior fromBlock() {
        return Block.INSTANCE;
    }

    public static SimpleBlockDropsProviderBehavior fromItems(ItemStackSnapshot... itemStackSnapshots) {
        return new Items(ImmutableList.copyOf(itemStackSnapshots));
    }

    public static SimpleBlockDropsProviderBehavior fromItems(Iterable<ItemStackSnapshot> itemStackSnapshots) {
        return new Items(ImmutableList.copyOf(itemStackSnapshots));
    }

    private static class Items extends SimpleBlockDropsProviderBehavior {

        private final List<ItemStackSnapshot> itemStackSnapshots;

        public Items(List<ItemStackSnapshot> itemStackSnapshots) {
            this.itemStackSnapshots = itemStackSnapshots;
        }

        @Override
        protected void collectDrops(BehaviorContext context, List<ItemStackSnapshot> itemStacks) {
            itemStacks.addAll(this.itemStackSnapshots);
        }
    }

    private static class Block extends SimpleBlockDropsProviderBehavior {

        static final Block INSTANCE = new Block();

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

    private static class Inventory extends SimpleBlockDropsProviderBehavior {

        static final Inventory INSTANCE = new Inventory();

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
}
