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
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.event.CollectEntityEvent;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.AbstractInventory;
import org.lanternpowered.server.inventory.LanternItemStackSnapshot;
import org.lanternpowered.server.inventory.PeekOfferTransactionsResult;
import org.lanternpowered.server.network.entity.EntityProtocolTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.statistic.achievement.Achievements;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;

import java.util.Optional;
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
        registerKey(LanternKeys.GRAVITY_FACTOR, 0.002);
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
        } else {
            pulsePhysics();
        }
    }

    private void pulsePhysics() {
        // Get the current velocity
        Vector3d velocity = getVelocity();
        // Update the position based on the velocity
        setPosition(getPosition().add(velocity));

        // We will check if there is a collision box under the entity
        boolean ground = false;

        final AABB thisBox = getBoundingBox().get().offset(0, -0.1, 0);
        final Set<AABB> boxes = getWorld().getIntersectingBlockCollisionBoxes(thisBox);
        for (AABB box : boxes) {
            final Vector3d factor = box.getCenter().sub(thisBox.getCenter());
            if (Direction.getClosest(factor).isUpright()) {
                ground = true;
            }
        }
        if (!ground) {
            final Optional<Double> gravityFactor = get(LanternKeys.GRAVITY_FACTOR);
            if (gravityFactor.isPresent()) {
                // Apply the gravity factor
                velocity = velocity.add(0, -gravityFactor.get(), 0);
            }
        }
        velocity = velocity.mul(0.98, 0.98, 0.98);
        if (ground) {
            velocity = velocity.mul(1, -0.5, 1);
        }
        // Offer the velocity back
        offer(Keys.VELOCITY, velocity);
    }

    private void tryToPickupItems() {
        final Set<Entity> entities = getWorld().getIntersectingEntities(
                getBoundingBox().get().expand(2.0, 0.5, 2.0), entity -> entity != this && entity instanceof Carrier);
        if (entities.isEmpty()) {
            return;
        }
        ItemStack itemStack = get(Keys.REPRESENTED_ITEM).map(ItemStackSnapshot::createStack).orElse(null);
        if (itemStack == null) {
            remove();
            return;
        }
        for (Entity entity : entities) {
            Inventory inventory = ((Carrier) entity).getInventory();
            if (inventory instanceof PlayerInventory) {
                inventory = ((PlayerInventory) inventory).getMain();
            }
            final PeekOfferTransactionsResult result = ((AbstractInventory) inventory).peekOfferFastTransactions(itemStack);
            final ItemStack rest = result.getOfferResult().getRest();
            final Cause.Builder cause = Cause.source(entity);
            cause.named("OriginalItemStack", itemStack);
            if (rest != null) {
                cause.named("RestItemStack", rest);
            }
            final ChangeInventoryEvent.Pickup event = SpongeEventFactory.createChangeInventoryEventPickup(
                    cause.build(), this, inventory, result.getTransactions());
            event.setCancelled(!result.getOfferResult().isSuccess());
            Sponge.getEventManager().post(event);
            if (event.isCancelled() && !isRemoved()) { // Don't continue if the entity was removed during the event
                continue;
            }
            event.getTransactions().stream()
                    .filter(Transaction::isValid)
                    .forEach(transaction -> transaction.getSlot().set(transaction.getFinal().createStack()));
            final int added;
            if (rest != null) {
                added = itemStack.getQuantity() - rest.getQuantity();
                itemStack = rest;
            } else {
                added = itemStack.getQuantity();
            }
            if (added != 0 && entity instanceof Living) {
                // Trigger achievements
                if (entity instanceof Player) {
                    final LanternPlayer player = (LanternPlayer) entity;
                    final ItemType itemType = itemStack.getItem();
                    if (itemType == BlockTypes.LOG.getItem().get() ||
                            itemType == BlockTypes.LOG2.getItem().get()) {
                        player.triggerAchievement(Achievements.MINE_WOOD);
                    } else if (itemType == ItemTypes.LEATHER) {
                        player.triggerAchievement(Achievements.KILL_COW);
                    } else if (itemType == ItemTypes.DIAMOND) {
                        player.triggerAchievement(Achievements.DIAMONDS);
                    } else if (itemType == ItemTypes.BLAZE_ROD) {
                        player.triggerAchievement(Achievements.BLAZE_ROD);
                    }
                }
                triggerEvent(new CollectEntityEvent((Living) entity, added));
            }
            if (rest == null || isRemoved()) {
                itemStack = null;
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
        ItemStackSnapshot itemStackSnapshot1 = get(Keys.REPRESENTED_ITEM).orElse(null);
        if (itemStackSnapshot1 == null) {
            remove();
            return null;
        }
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
