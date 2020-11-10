/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.entity

import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.withFrame
import org.lanternpowered.api.data.Keys
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.item.inventory.Carrier
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.emptyItemStackSnapshot
import org.lanternpowered.api.item.inventory.fix
import org.lanternpowered.api.item.inventory.stack.asSnapshot
import org.lanternpowered.api.item.inventory.stack.isNotEmpty
import org.lanternpowered.api.item.inventory.stack.isSimilarTo
import org.lanternpowered.api.util.duration.max
import org.lanternpowered.api.util.math.plus
import org.lanternpowered.api.util.math.times
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.world.getIntersectingBlockCollisionBoxes
import org.lanternpowered.api.world.getIntersectingEntities
import org.lanternpowered.server.effect.entity.EntityEffectCollection
import org.lanternpowered.server.effect.entity.EntityEffectTypes
import org.lanternpowered.server.effect.entity.particle.item.ItemDeathParticleEffect
import org.lanternpowered.server.entity.event.CollectEntityEvent
import org.lanternpowered.server.event.LanternEventContextKeys
import org.lanternpowered.server.event.LanternEventFactory
import org.lanternpowered.server.network.entity.EntityProtocolTypes
import org.spongepowered.api.entity.Item
import org.spongepowered.api.entity.living.Living
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent.Pickup
import org.spongepowered.api.util.AABB
import org.spongepowered.api.util.Direction
import org.spongepowered.math.vector.Vector3d
import kotlin.time.Duration
import kotlin.time.seconds

class LanternItem(creationData: EntityCreationData) : LanternEntity(creationData), Item {

    companion object {

        val DEFAULT_EFFECT_COLLECTION = EntityEffectCollection.builder()
                .add(EntityEffectTypes.DEATH, ItemDeathParticleEffect)
                .build()

        val BOUNDING_BOX_EXTENT = AABB(Vector3d(-0.125, 0.0, -0.125), Vector3d(0.125, 0.25, 0.125))

        val DROPPED_PICKUP_DELAY = 2.seconds
    }

    private var timer = Duration.ZERO

    init {
        this.protocolType = EntityProtocolTypes.ITEM
        this.boundingBoxExtent = BOUNDING_BOX_EXTENT
        this.effectCollection = DEFAULT_EFFECT_COLLECTION.copy()

        keyRegistry {
            register(Keys.ITEM_STACK_SNAPSHOT, emptyItemStackSnapshot())
            registerBounded(Keys.PICKUP_DELAY, 0.5.seconds).minimum(Duration.ZERO).coerceInBounds()
            registerBounded(Keys.DESPAWN_DELAY, 300.seconds).minimum(Duration.ZERO).coerceInBounds()
            register(Keys.GRAVITATIONAL_ACCELERATION, 0.04)
            register(Keys.INFINITE_PICKUP_DELAY, false)
            register(Keys.INFINITE_DESPAWN_DELAY, false)
        }
    }

    override fun update(deltaTime: Duration) {
        super.update(deltaTime)

        var pickupDelay = this.require(Keys.PICKUP_DELAY)
        var despawnDelay = this.require(Keys.DESPAWN_DELAY)

        val infinitePickupDelay = this.require(Keys.INFINITE_PICKUP_DELAY)
        val infiniteDespawnDelay = this.require(Keys.INFINITE_DESPAWN_DELAY)

        val oldPickupDelay = pickupDelay
        val oldDespawnDelay = despawnDelay

        if (!infinitePickupDelay)
            pickupDelay -= deltaTime

        if (!infiniteDespawnDelay)
            despawnDelay -= deltaTime

        this.timer += deltaTime
        val timer = this.timer
        if (timer > 1.seconds) {
            val data = combineItemStacks(pickupDelay, despawnDelay)
            if (data != null) {
                pickupDelay = data.pickupDelay
                despawnDelay = data.despawnDelay

                // Play the merge effect?
                this.effectCollection.getCombinedOrEmpty(EntityEffectTypes.MERGE).play(this)
            }
            this.timer = Duration.ZERO
        }
        if (timer > 0.5.seconds && !infinitePickupDelay && pickupDelay <= Duration.ZERO)
            this.tryToPickupItems()
        if (pickupDelay != oldPickupDelay)
            this.offer(Keys.PICKUP_DELAY, pickupDelay)
        if (despawnDelay != oldDespawnDelay)
            this.offer(Keys.DESPAWN_DELAY, despawnDelay)
        if (despawnDelay <= Duration.ZERO) {
            CauseStack.withFrame { frame ->
                frame.pushCause(this)

                // Throw the expire entity event
                val event = LanternEventFactory.createExpireEntityEvent(
                        frame.currentCause, this)
                EventManager.post(event)

                // Remove the item, also within this context
                this.remove()
            }

            // Play the death effect?
            this.effectCollection.getCombinedOrEmpty(EntityEffectTypes.DEATH).play(this)
        } else {
            this.updatePhysics(deltaTime)
        }
    }

    private fun updatePhysics(deltaTime: Duration) {
        // Get the current velocity
        var velocity = this.require(Keys.VELOCITY)
        // Update the position based on the velocity
        this.position = this.position + (velocity * deltaTime.inSeconds)

        // We will check if there is a collision box under the entity
        var ground = false
        val thisBox = this.boundingBox.get().offset(0.0, -0.1, 0.0)
        val boxes: Set<AABB> = this.world.getIntersectingBlockCollisionBoxes(thisBox)
        for (box in boxes) {
            val factor = box.center.sub(thisBox.center)
            if (Direction.getClosest(factor).isUpright)
                ground = true
        }
        if (!ground && this.get(Keys.IS_GRAVITY_AFFECTED).orElse(true)) {
            val constant = this.get(Keys.GRAVITATIONAL_ACCELERATION).orNull()
            if (constant != null) {
                // Apply the gravity factor
                velocity = velocity.add(0.0, -constant * deltaTime.inSeconds, 0.0)
            }
        }
        velocity = velocity.mul(0.98, 0.98, 0.98)
        if (ground)
            velocity = velocity.mul(1.0, -0.5, 1.0)
        // Offer the velocity back
        this.offer(Keys.VELOCITY, velocity)
    }

    private fun tryToPickupItems() {
        val entities = this.world.getIntersectingEntities(
                this.boundingBox.get().expand(2.0, 0.5, 2.0)) { entity -> entity !== this && entity is Carrier }
        if (entities.isEmpty())
            return
        val stack = this.require(Keys.ITEM_STACK_SNAPSHOT).createStack()
        if (stack.isEmpty) {
            this.remove()
            return
        }
        // TODO: Call pre pickup event
        for (entity in entities) {
            // Ignore dead entities
            if (entity is LanternLiving && entity.isDead)
                continue
            val inventory = (entity as Carrier).inventory.fix()
            /*
            if (inventory is PlayerInventory) {
                // TODO: Get priority hotbar inventory
                //inventory = inventory.primary.transform(InventoryTransforms.PRIORITY_HOTBAR)
            }
            */

            // Copy before consuming
            val originalStack = stack.copy()
            val peekResult = inventory.peekOffer(stack)
            var event: Pickup
            CauseStack.withFrame { frame ->
                frame.addContext(LanternEventContextKeys.ORIGINAL_ITEM_STACK, originalStack)
                if (stack.isNotEmpty)
                    frame.addContext(LanternEventContextKeys.REST_ITEM_STACK, stack)
                event = LanternEventFactory.createChangeInventoryEventPickup(
                        frame.currentCause, inventory, peekResult.transactions)
                event.isCancelled = peekResult.transactions.isEmpty()
                EventManager.post(event)
            }
            // Don't continue if the entity was removed during the event
            if (event.isCancelled && !this.isRemoved)
                continue

            event.transactions.stream()
                    .filter { transaction -> transaction.isValid }
                    .forEach { transaction -> transaction.slot.set(transaction.final.createStack()) }

            val added = originalStack.quantity - stack.quantity
            if (added != 0 && entity is Living)
                this.triggerEvent(CollectEntityEvent(entity as Living, added))
            if (this.isRemoved)
                stack.quantity = 0
            if (stack.isEmpty)
                break
        }
        if (stack.isNotEmpty) {
            this.offer(Keys.ITEM_STACK_SNAPSHOT, stack.asSnapshot())
        } else {
            this.remove()
        }
    }

    private inner class CombineData(
            val pickupDelay: Duration,
            val despawnDelay: Duration
    )

    private fun combineItemStacks(pickupDelay: Duration, despawnDelay: Duration): CombineData? {
        // Remove items with no item stack
        val item = this.require(Keys.ITEM_STACK_SNAPSHOT)
        if (item.isEmpty) {
            this.remove()
            return null
        }
        val max = item.type.maxStackQuantity
        var quantity = item.quantity
        // Check if the stack is already at it's maximum size
        if (quantity >= max)
            return null
        val frame = CauseStack.pushCauseFrame()
        frame.pushCause(this)
        // Search for surrounding items
        val entities = this.world.getIntersectingEntities(
                this.boundingBox.get().expand(0.6, 0.0, 0.6)) { entity -> entity !== this && entity is LanternItem }
        var newPickupDelay = pickupDelay
        var newDespawnDelay = despawnDelay
        var newItem: ItemStack? = null
        for (other in entities) {
            val otherInfinitePickupDelay = other.require(Keys.INFINITE_PICKUP_DELAY)
            if (otherInfinitePickupDelay)
                continue
            val otherPickupDelay = other.require(Keys.PICKUP_DELAY)
            val otherItem = other.require(Keys.ITEM_STACK_SNAPSHOT)

            var otherQuantity = otherItem.quantity
            // Don't bother stacks that are already filled and
            // make sure that the stacks can be merged
            if (otherQuantity >= max || !item.isSimilarTo(otherItem))
                continue

            // Call the merge event
            val event = LanternEventFactory.createItemMergeWithItemEvent(
                    frame.currentCause, other as Item, this)
            EventManager.post(event)
            if (event.isCancelled)
                continue

            // Merge the items
            quantity += otherQuantity
            if (quantity > max) {
                otherQuantity = quantity - max
                quantity = max

                // Create a new stack and offer it back the entity
                val newOtherItem = otherItem.createStack()
                newOtherItem.quantity = otherQuantity

                // The snapshot can be wrapped
                other.offer(Keys.ITEM_STACK_SNAPSHOT, newOtherItem.asSnapshot())
            } else {
                // The other entity is completely drained and will be removed
                other.offer(Keys.ITEM_STACK_SNAPSHOT, emptyItemStackSnapshot())
                other.remove()
            }
            // The item stack has changed
            if (newItem == null)
                newItem = item.createStack()
            newItem!!.quantity = quantity

            // When merging items, also merge the pickup and despawn delays
            newPickupDelay = max(newPickupDelay, otherPickupDelay)
            newDespawnDelay = max(newDespawnDelay, other.require(Keys.DESPAWN_DELAY))

            // The stack is already full, stop here
            if (quantity == max)
                break
        }
        frame.close()
        if (newItem != null) {
            this.offer(Keys.ITEM_STACK_SNAPSHOT, newItem.asSnapshot())
            return CombineData(newPickupDelay, newDespawnDelay)
        }
        return null
    }
}
