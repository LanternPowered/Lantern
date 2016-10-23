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
package org.lanternpowered.server.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.inventory.LanternItemStackSnapshot;
import org.lanternpowered.server.network.entity.EntityProtocolTypes;
import org.lanternpowered.server.util.AABBs;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class LanternItem extends LanternEntity implements Item {

    private int counter;

    public LanternItem(UUID uniqueId) {
        super(uniqueId);
        setEntityProtocolType(EntityProtocolTypes.ITEM);
        setBoundingBoxBase(AABBs.ofCenterExpansion(new Vector3d(0.25, 0.25, 0.25)));
    }

    @Override
    public void registerKeys() {
        super.registerKeys();
        registerKey(Keys.REPRESENTED_ITEM, null);
        registerKey(Keys.PICKUP_DELAY, 60);
    }

    @Override
    public void pulse() {
        super.pulse();

        int delay = get(Keys.PICKUP_DELAY).orElse(0);
        if (delay != 32767 && delay > 0) {
            delay--;
            tryOffer(Keys.PICKUP_DELAY, delay);
        }
        if (this.counter++ % 20 == 0) {
            combineItemStacks();
            if (delay != 32767 && delay <= 0) {
                tryToPickupItems();
            }
        }
    }

    private void tryToPickupItems() {
        final Set<Entity> entities = getWorld().getIntersectingEntities(
                getBoundingBox().get().expand(1.0, 0.5, 1.0), entity -> entity != this && entity instanceof Carrier);
        if (entities.isEmpty()) {
            return;
        }
        ItemStack itemStack = get(Keys.REPRESENTED_ITEM).get().createStack();
        for (Entity entity : entities) {
            final Inventory inventory = ((Carrier) entity).getInventory();
            final InventoryTransactionResult result = inventory.offer(itemStack);
            final Collection<ItemStackSnapshot> rejected = result.getRejectedItems();
            if (!rejected.isEmpty()) {
                itemStack = rejected.iterator().next().createStack();
            } else {
                itemStack = null;
                break;
            }
        }
        if (itemStack != null) {
            offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
        } else {
            remove();
        }
    }

    private void combineItemStacks() {
        ItemStackSnapshot itemStackSnapshot1 = get(Keys.REPRESENTED_ITEM).get();
        if (itemStackSnapshot1.getCount() >= itemStackSnapshot1.getType().getMaxStackQuantity()) {
            return;
        }
        checkNotNull(getWorld());
        Set<Entity> entities = getWorld().getIntersectingEntities(
                getBoundingBox().get().expand(0.6, 0.0, 0.6), entity -> entity != this && entity instanceof LanternItem);
        if (!entities.isEmpty()) {
            ItemStack itemStack = null;
            for (Entity entity : entities) {
                final ItemStackSnapshot itemStackSnapshot2 = entity.get(Keys.REPRESENTED_ITEM).get();
                if (((LanternItemStackSnapshot) itemStackSnapshot1).isSimilar(itemStackSnapshot2)) {
                    if (itemStackSnapshot1.getCount() < itemStackSnapshot2.getCount()) {
                        continue;
                    }
                    int quantity = itemStackSnapshot1.getCount() + itemStackSnapshot2.getCount();
                    if (quantity > itemStackSnapshot1.getType().getMaxStackQuantity()) {
                        final ItemStack itemStack2 = itemStackSnapshot2.createStack();
                        final int max = itemStackSnapshot1.getType().getMaxStackQuantity();
                        itemStack2.setQuantity(quantity - max);
                        entity.offer(Keys.REPRESENTED_ITEM, itemStack2.createSnapshot());
                        quantity = max;
                    } else {
                        entity.remove();
                    }
                    if (itemStack == null) {
                        itemStack = itemStackSnapshot1.createStack();
                    }
                    itemStack.setQuantity(quantity);
                    if (quantity > itemStackSnapshot1.getType().getMaxStackQuantity()) {
                        break;
                    }
                }
            }
            if (itemStack != null) {
                offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
            }
        }
    }
}
