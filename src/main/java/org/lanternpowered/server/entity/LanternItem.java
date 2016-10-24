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
import org.lanternpowered.server.entity.event.CollectEntityEvent;
import org.lanternpowered.server.inventory.LanternItemStackSnapshot;
import org.lanternpowered.server.network.entity.EntityProtocolTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.util.AABB;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

public class LanternItem extends LanternEntity implements Item {

    private static final class EffectHolder {

        private static final ParticleEffect DEATH_EFFECT =
                ParticleEffect.builder().type(ParticleTypes.CLOUD).quantity(3).offset(Vector3d.ONE.mul(0.1)).build();
    }

    private static final AABB BOUNDING_BOX_BASE = new AABB(new Vector3d(-0.125, 0, -0.125), new Vector3d(0.125, 0.25, 0.125));
    private static final int NO_DESPAWN_DELAY = 59536;
    private static final int NO_PICKUP_DELAY = 32767;

    private int counter;

    public LanternItem(UUID uniqueId) {
        super(uniqueId);
        setEntityProtocolType(EntityProtocolTypes.ITEM);
        setBoundingBoxBase(BOUNDING_BOX_BASE);
    }

    @Override
    public void registerKeys() {
        super.registerKeys();
        registerKey(Keys.REPRESENTED_ITEM, null);
        registerKey(Keys.PICKUP_DELAY, 60);
        registerKey(Keys.DESPAWN_DELAY, 6000);
    }

    @Override
    public void pulse() {
        super.pulse();

        int pickupDelay = get(Keys.PICKUP_DELAY).orElse(0);
        int despawnDelay = get(Keys.DESPAWN_DELAY).orElse(NO_DESPAWN_DELAY);
        final int oldPickupDelay = pickupDelay;
        final int oldDespawnDelay = despawnDelay;
        if (pickupDelay != NO_PICKUP_DELAY && pickupDelay > 0) {
            pickupDelay--;
        }
        if (despawnDelay != NO_DESPAWN_DELAY && despawnDelay > 0) {
            despawnDelay--;
        }
        if (this.counter++ % 20 == 0) {
            final CombineData data = combineItemStacks(pickupDelay, despawnDelay);
            if (data != null) {
                pickupDelay = data.pickupDelay;
                despawnDelay = data.despawnDelay;
            }
        }
        if (this.counter % 10 == 0 && pickupDelay != NO_PICKUP_DELAY && pickupDelay <= 0) {
            tryToPickupItems();
        }
        if (pickupDelay != oldPickupDelay) {
            offer(Keys.PICKUP_DELAY, pickupDelay);
        }
        if (despawnDelay != oldDespawnDelay) {
            offer(Keys.DESPAWN_DELAY, despawnDelay);
        }
        if (despawnDelay <= 0) {
            // A death animation/particle?
            getWorld().spawnParticles(EffectHolder.DEATH_EFFECT, getBoundingBox().get().getCenter());
            remove();
        }
    }

    private void tryToPickupItems() {
        final Set<Entity> entities = getWorld().getIntersectingEntities(
                getBoundingBox().get().expand(2.0, 0.5, 2.0), entity -> entity != this && entity instanceof Carrier);
        if (entities.isEmpty()) {
            return;
        }
        ItemStack itemStack = get(Keys.REPRESENTED_ITEM).get().createStack();
        for (Entity entity : entities) {
            Inventory inventory = ((Carrier) entity).getInventory();
            if (inventory instanceof PlayerInventory) {
                inventory = ((PlayerInventory) inventory).getMain();
            }
            final InventoryTransactionResult result = inventory.offer(itemStack);
            final Collection<ItemStackSnapshot> rejected = result.getRejectedItems();
            final int added;
            if (!rejected.isEmpty()) {
                final ItemStack itemStack1 = rejected.iterator().next().createStack();
                added = itemStack.getQuantity() - itemStack1.getQuantity();
                itemStack = itemStack1;
            } else {
                added = itemStack.getQuantity();
                itemStack = null;
            }
            if (added != 0 && entity instanceof Living) {
                triggerEvent(new CollectEntityEvent((Living) entity, added));
            }
            if (itemStack == null) {
                break;
            }
        }
        if (itemStack != null) {
            offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
        } else {
            remove();
        }
    }

    private final class CombineData {

        private final int pickupDelay;
        private final int despawnDelay;

        private CombineData(int pickupDelay, int despawnDelay) {
            this.despawnDelay = despawnDelay;
            this.pickupDelay = pickupDelay;
        }
    }

    @Nullable
    private CombineData combineItemStacks(int pickupDelay, int despawnDelay) {
        ItemStackSnapshot itemStackSnapshot1 = get(Keys.REPRESENTED_ITEM).get();
        if (itemStackSnapshot1.getCount() >= itemStackSnapshot1.getType().getMaxStackQuantity()) {
            return null;
        }
        checkNotNull(getWorld());
        Set<Entity> entities = getWorld().getIntersectingEntities(
                getBoundingBox().get().expand(0.6, 0.0, 0.6), entity -> entity != this && entity instanceof LanternItem);
        if (!entities.isEmpty()) {
            ItemStack itemStack = null;
            for (Entity entity : entities) {
                final int pickupDelay1 = entity.get(Keys.PICKUP_DELAY).orElse(0);
                if (pickupDelay1 == NO_PICKUP_DELAY) {
                    continue;
                }
                final ItemStackSnapshot itemStackSnapshot2 = entity.get(Keys.REPRESENTED_ITEM).get();
                if (itemStackSnapshot2.getCount() < itemStackSnapshot1.getCount()) {
                    continue;
                }
                if (((LanternItemStackSnapshot) itemStackSnapshot1).isSimilar(itemStackSnapshot2)) {
                    final int max = itemStackSnapshot1.getType().getMaxStackQuantity();
                    int quantity = itemStackSnapshot1.getCount() + itemStackSnapshot2.getCount();
                    if (quantity > max) {
                        final ItemStack itemStack2 = itemStackSnapshot2.createStack();
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
                    pickupDelay = Math.max(pickupDelay, pickupDelay1);
                    despawnDelay = Math.max(despawnDelay, entity.get(Keys.DESPAWN_DELAY).orElse(NO_DESPAWN_DELAY));
                    if (quantity >= max) {
                        break;
                    }
                }
            }
            if (itemStack != null) {
                offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
                return new CombineData(pickupDelay, despawnDelay);
            }
        }
        return null;
    }
}
