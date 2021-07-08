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
package org.lanternpowered.server.entity.shard

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.lanternpowered.api.shard.Shard
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.api.entity.event.animation.CollectEntityAnimation
import org.lanternpowered.api.entity.event.animation.DamageEntityAnimation
import org.lanternpowered.api.entity.event.EntityWorldShardevent
import org.lanternpowered.api.entity.event.animation.EntityLoveModeAnimation
import org.lanternpowered.server.entity.event.RequestPlayerAbilitiesRefreshShardevent
import org.lanternpowered.server.entity.event.SpectateEntityShardevent
import org.lanternpowered.api.entity.event.animation.SwingHandAnimation
import org.lanternpowered.server.network.entity.AbstractEntityProtocol
import org.lanternpowered.server.network.entity.EntityProtocolShardeventType
import org.lanternpowered.server.network.entity.EntityProtocolType
import org.lanternpowered.server.network.entity.EntityProtocolTypes
import org.lanternpowered.server.shards.OnAttach
import org.lanternpowered.server.shards.OnDetach
import org.lanternpowered.api.shard.event.Shardevent
import org.lanternpowered.api.shard.event.ShardeventListener
import org.spongepowered.api.entity.Entity
import kotlin.reflect.KClass

/**
 * A [Shard] that can be attached to a [Entity] to
 * give it the possibility to be visible/rendered on the client. Without
 * this component will be client never know about the [Entity].
 */
@ConfigSerializable
class NetworkShard : Shard<NetworkShard> {

    private val entity: LanternEntity by requireHolderOfType()

    @Setting(value = "type", comment = "The entity protocol type that should be used for the entity.")
    private var entityProtocolType: EntityProtocolType<*> = EntityProtocolTypes.CHICKEN // Chickens by default? Why not...

    @Setting(value = "tracking-range", comment = "The tracking range that the entity is visible to players.")
    var trackingRange = 64.0

    @Setting(value = "tracking-update-rate", comment = "The tracking update rate is the amount of ticks between each protocol update.")
    var trackingUpdateRate = 4

    private var entityProtocol: AbstractEntityProtocol<*>? = null

    constructor()

    constructor(entityProtocolType: EntityProtocolType<*>, trackingRange: Int, trackingUpdateRate: Int) {
        this.entityProtocolType = entityProtocolType
        this.trackingUpdateRate = trackingUpdateRate
        this.trackingRange = trackingRange.toDouble()
    }

    /**
     * Gets the [EntityProtocolType].
     *
     * @return The entity protocol type
     */
    fun getEntityProtocolType(): EntityProtocolType<*> {
        return this.entityProtocolType
    }

    /**
     * Sets the [EntityProtocolType]. Will cause
     * the [LanternEntity] to respawn on the client.
     *
     * @param entityProtocolType The entity protocol type
     */
    fun setEntityProtocolType(entityProtocolType: EntityProtocolType<*>) {
        this.entityProtocolType = entityProtocolType
        // The entity was already present in a world when the component got attached
        if (this.entity.existsInWorld()) {
            // Respawn the entity on the client, the old entry will be cleaned up
            this.entityProtocol = this.entity.world.entityProtocolManager.add(this.entity, this)
        }
    }

    @OnAttach
    private fun onAttach() {
        val shardeventBus = this.entity.shardeventBus
        // Delegate the events that are used in protocol
        delegatedEvents.forEach { (shardeventClass, shardeventType) ->
            shardeventBus.register(shardeventClass.java) { event ->
                if (this.entityProtocol != null) {
                    this.entityProtocol!!.addEvent(event, shardeventType)
                }
            }
        }
        // The entity was already present in a world when the component got attached
        if (this.entity.existsInWorld()) {
            // Spawn the entity on the client
            this.entityProtocol = this.entity.world.entityProtocolManager.add(this.entity, this)
        }
    }

    @OnDetach
    private fun onDetach() {
        // The entity was already present in a world when the component got detached
        if (this.entity.existsInWorld()) {
            // Remove the entity from the client
            this.entity.world.entityProtocolManager.remove(this.entity)
            // The entity is no more
            this.entityProtocol = null
        }
    }

    @ShardeventListener
    private fun onJoinWorld(event: EntityWorldShardevent.Join) {
        // Spawn the entity on the client
        this.entityProtocol = event.world.entityProtocolManager.add(this.entity, this)
    }

    @ShardeventListener
    private fun onLeaveWorld(event: EntityWorldShardevent.Leave) {
        // Remove the entity on the client
        event.world.entityProtocolManager.remove(this.entity)
        // The entity is no more
        this.entityProtocol = null
    }

    companion object {

        /**
         * A list with all the [Shardevent] that should be delegated to the [AbstractEntityProtocol].
         */
        private val delegatedEvents: List<Pair<KClass<out Shardevent>, EntityProtocolShardeventType>> = listOf(
                // Used to play damage animations
                Pair(DamageEntityAnimation::class, EntityProtocolShardeventType.ALIVE),
                // Used to request player ability updates
                Pair(RequestPlayerAbilitiesRefreshShardevent::class, EntityProtocolShardeventType.DEATH_OR_ALIVE),
                // Used to make the entity being collected (picked up)
                Pair(CollectEntityAnimation::class, EntityProtocolShardeventType.DEATH_OR_ALIVE),
                // Used when a player spectates a specific entity or stops spectating
                Pair(SpectateEntityShardevent::class, EntityProtocolShardeventType.ALIVE),
                // Used to play arm swing animations
                Pair(SwingHandAnimation::class, EntityProtocolShardeventType.ALIVE),
                // Used to play love particles
                Pair(EntityLoveModeAnimation::class, EntityProtocolShardeventType.ALIVE)
        )
    }
}
