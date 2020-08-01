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
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.item.inventory.stack.isSimilarTo
import org.lanternpowered.api.util.duration.max
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.world.getIntersectingBlockCollisionBoxes
import org.lanternpowered.api.world.getIntersectingEntities
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.effect.entity.EntityEffectCollection
import org.lanternpowered.server.effect.entity.EntityEffectTypes
import org.lanternpowered.server.effect.entity.particle.item.ItemDeathParticleEffect
import org.lanternpowered.server.entity.event.CollectEntityEvent
import org.lanternpowered.server.event.LanternEventContextKeys
import org.lanternpowered.server.inventory.IInventory
import org.lanternpowered.server.inventory.LanternItemStack
import org.lanternpowered.server.inventory.LanternItemStackSnapshot
import org.lanternpowered.server.network.entity.EntityProtocolTypes
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.Keys
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.Item
import org.spongepowered.api.entity.living.Living
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent.Pickup
import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.entity.PlayerInventory
import org.spongepowered.api.item.inventory.transaction.SlotTransaction
import org.spongepowered.api.util.AABB
import org.spongepowered.api.util.Direction
import org.spongepowered.math.vector.Vector3d
import kotlin.time.Duration
import kotlin.time.milliseconds
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
    }

    override fun registerKeys() {
        super.registerKeys()

        keyRegistry {
            register(Keys.ITEM_STACK_SNAPSHOT, ItemStackSnapshot.empty())
            registerBounded(LanternKeys.PICKUP_DELAY, 0.5.seconds).minimum(Duration.ZERO).coerceInBounds()
            registerBounded(LanternKeys.DESPAWN_DELAY, 300.seconds).minimum(Duration.ZERO).coerceInBounds()
            register(LanternKeys.GRAVITY_FACTOR, 0.002)
            register(Keys.INFINITE_PICKUP_DELAY, false)
            register(Keys.INFINITE_DESPAWN_DELAY, false)

            // TODO: Replace lantern keys with sponge keys once duration is added
            registerProvider(Keys.PICKUP_DELAY) {
                get { (this.require(LanternKeys.PICKUP_DELAY).inMilliseconds / 50).toInt() }
                setFastAnd { value -> this.offerFast(LanternKeys.PICKUP_DELAY, (value * 50).milliseconds) }
            }
            registerProvider(Keys.DESPAWN_DELAY) {
                get { (this.require(LanternKeys.DESPAWN_DELAY).inMilliseconds / 50).toInt() }
                setFastAnd { value -> this.offerFast(LanternKeys.DESPAWN_DELAY, (value * 50).milliseconds) }
            }
        }
    }

    override fun update(deltaTime: Duration) {
        super.update(deltaTime)

        var pickupDelay = this.require(LanternKeys.PICKUP_DELAY)
        var despawnDelay = this.require(LanternKeys.DESPAWN_DELAY)

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
            this.offer(LanternKeys.PICKUP_DELAY, pickupDelay)
        if (despawnDelay != oldDespawnDelay)
            this.offer(LanternKeys.DESPAWN_DELAY, despawnDelay)
        if (despawnDelay <= Duration.ZERO) {
            val causeStack = CauseStack.current()
            causeStack.pushCauseFrame().use { frame ->
                frame.pushCause(this)

                // Throw the expire entity event
                val event = LanternEventFactory.createExpireEntityEvent(
                        causeStack.currentCause, this)
                EventManager.post(event)

                // Remove the item, also within this context
                this.remove()
            }

            // Play the death effect?
            this.effectCollection.getCombinedOrEmpty(EntityEffectTypes.DEATH).play(this)
        } else {
            this.updatePhysics()
        }
    }

    private fun updatePhysics() {
        // Get the current velocity
        var velocity = this.require(Keys.VELOCITY)
        // Update the position based on the velocity
        this.position = this.position.add(velocity)

        // We will check if there is a collision box under the entity
        var ground = false
        val thisBox = boundingBox.get().offset(0.0, -0.1, 0.0)
        val boxes: Set<AABB> = world.getIntersectingBlockCollisionBoxes(thisBox)
        for (box in boxes) {
            val factor = box.center.sub(thisBox.center)
            if (Direction.getClosest(factor).isUpright)
                ground = true
        }
        if (!ground) {
            val gravityFactor = this.get(LanternKeys.GRAVITY_FACTOR).orNull()
            if (gravityFactor != null) {
                // Apply the gravity factor
                velocity = velocity.add(0.0, -gravityFactor, 0.0)
            }
        }
        velocity = velocity.mul(0.98, 0.98, 0.98)
        if (ground)
            velocity = velocity.mul(1.0, -0.5, 1.0)
        // Offer the velocity back
        this.offer(Keys.VELOCITY, velocity)
    }

    private fun tryToPickupItems() {
        val entities: Set<Entity> = this.world.getIntersectingEntities(
                this.boundingBox.get().expand(2.0, 0.5, 2.0)) { entity -> entity !== this && entity is Carrier }
        if (entities.isEmpty())
            return
        val stack = this.require(Keys.ITEM_STACK_SNAPSHOT).createStack() as LanternItemStack
        if (stack.isEmpty) {
            this.remove()
            return
        }
        // TODO: Call pre pickup event
        for (entity in entities) {
            // Ignore dead entities
            if (entity is LanternLiving && entity.isDead) {
                continue
            }
            var inventory: Inventory = (entity as Carrier).inventory
            if (inventory is PlayerInventory) {
                // TODO: Get priority hotbar inventory
                //inventory = inventory.primary.transform(InventoryTransforms.PRIORITY_HOTBAR)
            }

            // Copy before consuming
            val originalStack = stack.copy()
            val peekResult = (inventory as IInventory).peekOffer(stack)
            val causeStack = CauseStack.current()
            var event: Pickup
            causeStack.withFrame { frame ->
                frame.addContext(LanternEventContextKeys.ORIGINAL_ITEM_STACK, originalStack)
                if (stack.isNotEmpty) {
                    frame.addContext(LanternEventContextKeys.REST_ITEM_STACK, stack)
                }
                event = LanternEventFactory.createChangeInventoryEventPickup(
                        causeStack.currentCause, inventory, peekResult.transactions)
                event.isCancelled = peekResult.transactions.isEmpty()
                Sponge.getEventManager().post(event)
            }
            // Don't continue if the entity was removed during the event
            if (event.isCancelled && !this.isRemoved)
                continue

            event.transactions.stream()
                    .filter { obj: SlotTransaction -> obj.isValid }
                    .forEach { transaction: SlotTransaction -> transaction.slot.set(transaction.final.createStack()) }

            val added = originalStack.quantity - stack.quantity
            if (added != 0 && entity is Living)
                this.triggerEvent(CollectEntityEvent(entity as Living, added))
            if (isRemoved) {
                stack.quantity = 0
            }
            if (stack.isEmpty)
                break
        }
        if (stack.isNotEmpty) {
            this.offer(Keys.ITEM_STACK_SNAPSHOT, stack.toWrappedSnapshot())
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
        val causeStack = CauseStack.current()
        val frame = causeStack.pushCauseFrame()
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
            val otherPickupDelay = other.require(LanternKeys.PICKUP_DELAY)
            val otherItem = other.require(Keys.ITEM_STACK_SNAPSHOT)

            var otherQuantity = otherItem.quantity
            // Don't bother stacks that are already filled and
            // make sure that the stacks can be merged
            if (otherQuantity >= max || !item.isSimilarTo(otherItem))
                continue

            // Call the merge event
            val event = LanternEventFactory.createItemMergeWithItemEvent(
                    causeStack.currentCause, other as Item, this)
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
                other.offer(Keys.ITEM_STACK_SNAPSHOT, LanternItemStackSnapshot.wrap(newOtherItem))
            } else {
                // The other entity is completely drained and will be removed
                other.offer(Keys.ITEM_STACK_SNAPSHOT, ItemStackSnapshot.empty())
                other.remove()
            }
            // The item stack has changed
            if (newItem == null)
                newItem = item.createStack()
            newItem!!.quantity = quantity

            // When merging items, also merge the pickup and despawn delays
            newPickupDelay = max(newPickupDelay, otherPickupDelay)
            newDespawnDelay = max(newDespawnDelay, other.require(LanternKeys.DESPAWN_DELAY))

            // The stack is already full, stop here
            if (quantity == max)
                break
        }
        causeStack.popCauseFrame(frame)
        if (newItem != null) {
            this.offer(Keys.ITEM_STACK_SNAPSHOT, LanternItemStackSnapshot.wrap(newItem))
            return CombineData(newPickupDelay, newDespawnDelay)
        }
        return null
    }
}
