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

import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.effect.entity.EntityEffectCollection;
import org.lanternpowered.server.effect.entity.EntityEffectTypes;
import org.lanternpowered.server.effect.entity.particle.item.ItemDeathParticleEffect;
import org.lanternpowered.server.entity.event.CollectEntityEvent;
import org.lanternpowered.server.event.LanternEventContextKeys;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.inventory.LanternItemStackSnapshot;
import org.lanternpowered.server.inventory.PeekedOfferTransactionResult;
import org.lanternpowered.server.inventory.transformation.InventoryTransforms;
import org.lanternpowered.server.network.entity.EntityProtocolTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.entity.ExpireEntityEvent;
import org.spongepowered.api.event.entity.item.ItemMergeItemEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;
import org.spongepowered.math.vector.Vector3d;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

public class LanternItem extends LanternEntity implements Item {

    public static final EntityEffectCollection DEFAULT_EFFECT_COLLECTION = EntityEffectCollection.builder()
            .add(EntityEffectTypes.DEATH, new ItemDeathParticleEffect())
            .build();

    public static final int DROPPED_PICKUP_DELAY = 40;

    private static final AABB BOUNDING_BOX_BASE = new AABB(new Vector3d(-0.125, 0, -0.125), new Vector3d(0.125, 0.25, 0.125));
    private static final int NO_DESPAWN_DELAY = 59536;
    private static final int NO_PICKUP_DELAY = 32767;

    private int counter;

    public LanternItem(UUID uniqueId) {
        super(uniqueId);
        setEntityProtocolType(EntityProtocolTypes.ITEM);
        setBoundingBoxBase(BOUNDING_BOX_BASE);
        setEffectCollection(DEFAULT_EFFECT_COLLECTION.copy());
    }

    @Override
    public void registerKeys() {
        super.registerKeys();
        final ValueCollection c = getValueCollection();
        c.registerNonRemovable(Keys.REPRESENTED_ITEM, ItemStackSnapshot.empty());
        c.registerNonRemovable(Keys.PICKUP_DELAY, 10);
        c.registerNonRemovable(Keys.DESPAWN_DELAY, 6000);
        c.registerNonRemovable(LanternKeys.GRAVITY_FACTOR, 0.002);
    }

    @Override
    public void pulse(int deltaTicks) {
        super.pulse(deltaTicks);

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

                // Play the merge effect?
                getEffectCollection().getCombinedOrEmpty(EntityEffectTypes.MERGE).play(this);
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
            final CauseStack causeStack = CauseStack.current();
            try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
                frame.pushCause(this);

                // Throw the expire entity event
                final ExpireEntityEvent.TargetItem event = SpongeEventFactory.createExpireEntityEventTargetItem(
                        causeStack.getCurrentCause(), this);
                Sponge.getEventManager().post(event);

                // Remove the item, also within this context
                remove();
            }

            // Play the death effect?
            getEffectCollection().getCombinedOrEmpty(EntityEffectTypes.DEATH).play(this);
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
        final LanternItemStack stack = (LanternItemStack) get(Keys.REPRESENTED_ITEM).map(ItemStackSnapshot::createStack).get();
        if (stack.isEmpty()) {
            remove();
            return;
        }
        // TODO: Call pre pickup event
        for (Entity entity : entities) {
            // Ignore dead entities
            if (entity instanceof LanternLiving && ((LanternLiving) entity).isDead()) {
                continue;
            }
            Inventory inventory = ((Carrier) entity).getInventory();
            if (inventory instanceof PlayerInventory) {
                inventory = ((PlayerInventory) inventory).getPrimary().transform(InventoryTransforms.PRIORITY_HOTBAR);
            }

            // Copy before consuming
            final LanternItemStack originalStack = stack.copy();
            final PeekedOfferTransactionResult peekResult = ((IInventory) inventory).peekOffer(stack);

            final CauseStack causeStack = CauseStack.current();
            final ChangeInventoryEvent.Pickup event;
            try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
                frame.addContext(LanternEventContextKeys.ORIGINAL_ITEM_STACK, originalStack);
                if (stack.isNotEmpty()) {
                    frame.addContext(LanternEventContextKeys.REST_ITEM_STACK, stack);
                }

                event = SpongeEventFactory.createChangeInventoryEventPickup(
                        causeStack.getCurrentCause(), inventory, peekResult.getTransactions());
                event.setCancelled(peekResult.getTransactions().isEmpty());

                Sponge.getEventManager().post(event);
            }
            if (event.isCancelled() && !isRemoved()) { // Don't continue if the entity was removed during the event
                continue;
            }
            event.getTransactions().stream()
                    .filter(Transaction::isValid)
                    .forEach(transaction -> transaction.getSlot().set(transaction.getFinal().createStack()));
            final int added = originalStack.getQuantity() - stack.getQuantity();
            if (added != 0 && entity instanceof Living) {
                triggerEvent(new CollectEntityEvent((Living) entity, added));
            }
            if (isRemoved()) {
                stack.clear();
            }
            if (stack.isEmpty()) {
                break;
            }
        }
        if (stack.isNotEmpty()) {
            offer(Keys.REPRESENTED_ITEM, stack.toWrappedSnapshot());
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
        // Remove items with no item stack
        final ItemStackSnapshot itemStackSnapshot1 = get(Keys.REPRESENTED_ITEM).orElse(null);
        if (itemStackSnapshot1 == null || itemStackSnapshot1.isEmpty()) {
            remove();
            return null;
        }
        final int max = itemStackSnapshot1.getType().getMaxStackQuantity();
        int quantity1 = itemStackSnapshot1.getQuantity();
        // Check if the stack is already at it's maximum size
        if (quantity1 >= max) {
            return null;
        }
        final CauseStack causeStack = CauseStack.current();
        final CauseStack.Frame frame = causeStack.pushCauseFrame();
        frame.pushCause(this);
        // Search for surrounding items
        final Set<Entity> entities = getWorld().getIntersectingEntities(
                getBoundingBox().get().expand(0.6, 0.0, 0.6), entity -> entity != this && entity instanceof LanternItem);
        ItemStack itemStack1 = null;
        for (Entity entity : entities) {
            final int pickupDelay1 = entity.get(Keys.PICKUP_DELAY).orElse(0);
            if (pickupDelay1 == NO_PICKUP_DELAY) {
                continue;
            }
            final ItemStackSnapshot itemStackSnapshot2 = entity.get(Keys.REPRESENTED_ITEM).get();
            int quantity2 = itemStackSnapshot2.getQuantity();
            // Don't bother stacks that are already filled and
            // make sure that the stacks can be merged
            if (quantity2 >= max || !LanternItemStack.areSimilar(itemStackSnapshot1, itemStackSnapshot2)) {
                continue;
            }
            // Call the merge event
            final ItemMergeItemEvent event = SpongeEventFactory.createItemMergeItemEvent(causeStack.getCurrentCause(), (Item) entity, this);
            Sponge.getEventManager().post(event);
            if (event.isCancelled()) {
                continue;
            }
            // Merge the items
            quantity1 += quantity2;
            if (quantity1 > max) {
                quantity2 = quantity1 - max;
                quantity1 = max;

                // Create a new stack and offer it back the entity
                final ItemStack itemStack2 = itemStackSnapshot2.createStack();
                itemStack2.setQuantity(quantity2);

                // The snapshot can be wrapped
                entity.offer(Keys.REPRESENTED_ITEM, LanternItemStackSnapshot.wrap(itemStack2));
            } else {
                // The other entity is completely drained and will be removed
                entity.offer(Keys.REPRESENTED_ITEM, ItemStackSnapshot.empty());
                entity.remove();
            }
            // The item stack has changed
            if (itemStack1 == null) {
                itemStack1 = itemStackSnapshot1.createStack();
            }
            itemStack1.setQuantity(quantity1);

            // When merging items, also merge the pickup and despawn delays
            pickupDelay = Math.max(pickupDelay, pickupDelay1);
            despawnDelay = Math.max(despawnDelay, entity.get(Keys.DESPAWN_DELAY).orElse(NO_DESPAWN_DELAY));

            // The stack is already full, stop here
            if (quantity1 == max) {
                break;
            }
        }
        causeStack.popCauseFrame(frame);
        if (itemStack1 != null) {
            offer(Keys.REPRESENTED_ITEM, LanternItemStackSnapshot.wrap(itemStack1));
            return new CombineData(pickupDelay, despawnDelay);
        }
        return null;
    }
}
