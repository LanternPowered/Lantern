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
package org.lanternpowered.server.network.entity

import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.entity.event.EntityEvent
import org.lanternpowered.server.entity.event.EntityEventType
import org.lanternpowered.server.entity.player.LanternPlayer
import org.lanternpowered.server.network.packet.Packet
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.math.vector.Vector3d
import java.util.ArrayList
import java.util.HashSet
import java.util.Optional
import java.util.OptionalInt
import java.util.function.Supplier

/**
 * @property entity The entity that is being tracked.
 */
abstract class AbstractEntityProtocol<E : LanternEntity> protected constructor(
        val entity: E
) {

    lateinit var entityProtocolManager: EntityProtocolManager

    /**
     * All the players tracking this entity.
     */
    @JvmField
    val trackers: MutableSet<LanternPlayer> = HashSet()

    /**
     * The entity id of the entity.
     */
    var rootEntityId = EntityProtocolManager.INVALID_ENTITY_ID
        private set

    /**
     * The number of ticks between every update.
     */
    var tickRate = 4

    /**
     * The tracking range of the entity.
     */
    var trackingRange = 64.0

    private var tickCounter = 0

    @JvmField
    val playerInteractTimes: Object2LongMap<Player> = Object2LongOpenHashMap()

    @JvmField
    val entityEvents: MutableList<EntityEvent> = ArrayList()

    internal inner class SimpleEntityProtocolContext : EntityProtocolUpdateContext {

        lateinit var trackers: Set<LanternPlayer>

        override fun getById(entityId: Int): Optional<LanternEntity> {
            return entityProtocolManager.getEntityProtocolById(entityId).map { obj -> obj.entity }
        }

        override fun getId(entity: Entity): OptionalInt {
            val optProtocol = entityProtocolManager.getEntityProtocolByEntity(entity)
            return optProtocol.map { protocol: AbstractEntityProtocol<*> -> OptionalInt.of(protocol.rootEntityId) }.orElseGet { OptionalInt.empty() }
        }

        override fun sendToSelf(packet: Packet) {
            if (entity is LanternPlayer)
                entity.connection.send(packet)
        }

        override fun sendToSelf(messageSupplier: Supplier<Packet>) {
            if (entity is Player)
                this.sendToSelf(messageSupplier.get())
        }

        override fun sendToAll(packet: Packet) {
            for (tracker in this.trackers)
                tracker.connection.send(packet)
        }

        override fun sendToAll(message: Supplier<Packet>) {
            if (this.trackers.isNotEmpty()) {
                sendToAll(message.get())
            }
        }

        override fun sendToAllExceptSelf(packet: Packet) {
            for (tracker in this.trackers) {
                if (tracker != entity)
                    tracker.connection.send(packet)
            }
        }

        override fun sendToAllExceptSelf(messageSupplier: Supplier<Packet>) {
            if (this.trackers.isNotEmpty())
                this.sendToAllExceptSelf(messageSupplier.get())
        }
    }

    private fun newContext(): SimpleEntityProtocolContext = SimpleEntityProtocolContext()

    /**
     * Destroys the entity. This removes all the trackers and sends a destroy
     * message to the client.
     *
     * @param context The entity protocol context
     */
    fun destroy(context: EntityProtocolInitContext) {
        if (this.trackers.isNotEmpty()) {
            // Destroy the entity on all the clients
            val ctx = this.newContext()
            val events = this.processEvents(death = true, alive = true)
            ctx.trackers = this.trackers
            if (events?.deathOrAlive != null) {
                for (event in events.deathOrAlive)
                    this.handleEvent(ctx, event)
            }
            destroy(ctx)
            this.trackers.clear()
            synchronized(this.playerInteractTimes) {
                this.playerInteractTimes.clear()
            }
        }
        this.remove(context)
    }

    protected open fun remove(context: EntityProtocolInitContext) {
        // Release the entity id of the entity
        if (this.entity !is NetworkIdHolder) {
            context.release(this.rootEntityId)
        }
        this.rootEntityId = EntityProtocolManager.INVALID_ENTITY_ID
    }

    /**
     * Initializes this entity protocol. This acquires the ids
     * that are required to spawn the entity.
     *
     * @param context The entity protocol context
     */
    open fun init(context: EntityProtocolInitContext) {
        if (entity is NetworkIdHolder) {
            initRootId((entity as NetworkIdHolder).networkId)
        } else {
            // Allocate the next free id
            initRootId(context.acquire())
        }
    }

    /**
     * Initializes the root entity id of this protocol.
     *
     * @param rootEntityId The root entity id
     */
    protected fun initRootId(rootEntityId: Int) {
        check(rootEntityId != EntityProtocolManager.INVALID_ENTITY_ID) { "The root entity id cannot be invalid." }
        check(this.rootEntityId == EntityProtocolManager.INVALID_ENTITY_ID) { "This entity protocol is already initialized." }
        this.rootEntityId = rootEntityId
    }

    internal inner class TrackerUpdateContextData(val entityProtocol: AbstractEntityProtocol<*>) {
        val ctx = newContext()
        var added: MutableSet<LanternPlayer>? = null
        var removed: Set<LanternPlayer>? = null
        var update: MutableSet<LanternPlayer>? = null
    }

    internal fun buildUpdateContextData(players: Set<LanternPlayer>): TrackerUpdateContextData? {
        val mutablePlayers = HashSet(players)
        val removed: MutableSet<LanternPlayer> = HashSet()
        val added: MutableSet<LanternPlayer> = HashSet()
        val pos = this.entity.position
        val trackerIt = this.trackers.iterator()
        while (trackerIt.hasNext()) {
            val tracker = trackerIt.next()
            val flag = mutablePlayers.remove(tracker)
            if (tracker != this.entity && (!flag || !this.isVisible(pos, tracker))) {
                trackerIt.remove()
                removed.add(tracker)
            }
        }
        for (tracker in mutablePlayers) {
            if (tracker == entity || this.isVisible(pos, tracker))
                added.add(tracker)
        }
        val flag0 = this.tickCounter++ % this.tickRate == 0 && this.trackers.isNotEmpty()
        val flag1 = added.isNotEmpty()
        val flag2 = removed.isNotEmpty()
        if (!flag0 && !flag1 && !flag2)
            return null
        val contextData = TrackerUpdateContextData(this)
        if (flag0 || flag1)
            contextData.update = HashSet(this.trackers)
        if (flag1) {
            contextData.added = added
            this.trackers.addAll(added)
        }
        if (flag2)
            contextData.removed = removed
        return contextData
    }

    internal fun updateTrackers(contextData: TrackerUpdateContextData) {
        val ctx = contextData.ctx
        val events = processEvents(contextData.removed != null, true)
        val removed = contextData.removed
        if (removed != null) {
            ctx.trackers = removed
            if (events?.deathOrAlive != null) {
                for (event in events.deathOrAlive)
                    this.handleEvent(ctx, event)
            }
            this.destroy(ctx)
            synchronized(this.playerInteractTimes) {
                for (tracker in removed)
                    this.playerInteractTimes.removeLong(tracker)
            }
        }
        var trackers: MutableSet<LanternPlayer>? = null
        val update = contextData.update
        if (update != null) {
            ctx.trackers = update
            this.update(ctx)
            if (events != null) {
                if (trackers == null) {
                    trackers = update.toMutableSet()
                } else {
                    trackers.addAll(update)
                }
            }
        }
        val added = contextData.added
        if (added != null) {
            ctx.trackers = added
            this.spawn(ctx)
            if (events != null) {
                if (trackers == null) {
                    trackers = added
                } else {
                    trackers.addAll(added)
                }
            }
        }
        if (trackers != null) {
            ctx.trackers = trackers
            for (event in events!!.alive)
                this.handleEvent(ctx, event)
        }
    }

    fun updateTrackerLocale(player: LanternPlayer) {
        if (!trackers.contains(player)) {
            return
        }
        val ctx = this.newContext()
        ctx.trackers = setOf(player)
        this.updateTranslations(ctx)
    }

    private inner class TempEvents(val deathOrAlive: List<EntityEvent>?, val alive: List<EntityEvent>)

    private fun processEvents(death: Boolean, alive: Boolean): TempEvents? {
        if (!death && !alive)
            return null
        var aliveList: List<EntityEvent>
        synchronized(this.entityEvents) {
            if (this.entityEvents.isNotEmpty()) {
                aliveList = ArrayList(this.entityEvents)
                this.entityEvents.clear()
            } else {
                return null
            }
        }
        var deathOrAliveList: MutableList<EntityEvent>? = null
        if (death) {
            for (event in aliveList) {
                if (event.type() == EntityEventType.DEATH_OR_ALIVE) {
                    if (deathOrAliveList == null) {
                        deathOrAliveList = ArrayList()
                    }
                    deathOrAliveList.add(event)
                }
            }
        }
        return TempEvents(deathOrAliveList, aliveList)
    }

    internal fun postUpdateTrackers(contextData: TrackerUpdateContextData) {
        val ctx = contextData.ctx
        val update = contextData.update
        if (update != null) {
            ctx.trackers = update
            this.postUpdate(ctx)
        }
        val added = contextData.added
        if (added != null) {
            ctx.trackers = added
            this.postSpawn(ctx)
        }
    }

    private fun isVisible(pos: Vector3d, tracker: LanternPlayer): Boolean {
        return pos.distanceSquared(tracker.position) < trackingRange * trackingRange && isVisible(tracker)
    }

    /**
     * Gets whether the tracked entity is visible for the tracker.
     *
     * @param tracker The tracker
     * @return Whether the tracker can see the entity
     */
    protected fun isVisible(tracker: LanternPlayer): Boolean {
        return tracker.canSee(entity)
    }

    /**
     * Spawns the tracked entity.
     *
     * @param context The entity update context
     */
    protected abstract fun spawn(context: EntityProtocolUpdateContext)

    /**
     * Destroys the tracked entity.
     *
     * @param context The entity update context
     */
    protected abstract fun destroy(context: EntityProtocolUpdateContext)

    /**
     * Updates the tracked entity.
     *
     * @param context The entity update context
     */
    protected abstract fun update(context: EntityProtocolUpdateContext)

    /**
     * Updates the tracked entity for [Locale] changes.
     *
     * @param context The context
     */
    protected abstract fun updateTranslations(context: EntityProtocolUpdateContext)

    protected open fun handleEvent(context: EntityProtocolUpdateContext, event: EntityEvent) {}

    /**
     * Post spawns the tracked entity. This method will be called after
     * all the entities that were pending for updates/spawns are processed.
     *
     * @param context The entity update context
     */
    protected open fun postSpawn(context: EntityProtocolUpdateContext) {}

    /**
     * Post updates the tracked entity. This method will be called after
     * all the entities that were pending for updates/spawns are processed.
     *
     * @param context The entity update context
     */
    protected open fun postUpdate(context: EntityProtocolUpdateContext) {}

    /**
     * Is called when the specified [LanternPlayer] tries to interact
     * with this entity, or at least one of the ids assigned to it.
     *
     * @param player The player that interacted with the entity
     * @param entityId The entity the player interacted with
     * @param position The position where the player interacted with the entity, if present
     */
    fun playerInteract(player: LanternPlayer, entityId: Int, position: Vector3d?) {}

    /**
     * Is called when the specified [LanternPlayer] tries to attach
     * this entity, or at least one of the ids assigned to it.
     *
     * @param player The player that attack the entity
     * @param entityId The entity id the player attacked
     */
    fun playerAttack(player: LanternPlayer, entityId: Int) {}
}
