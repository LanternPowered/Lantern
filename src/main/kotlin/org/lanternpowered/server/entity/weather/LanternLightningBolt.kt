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
package org.lanternpowered.server.entity.weather

import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.entity.damage.DamageTypes
import org.lanternpowered.api.cause.withFrame
import org.lanternpowered.api.effect.sound.SoundCategory
import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.util.collections.asNonNullList
import org.lanternpowered.api.world.getIntersectingEntities
import org.lanternpowered.server.effect.entity.EntityEffectCollection
import org.lanternpowered.server.effect.entity.EntityEffectTypes
import org.lanternpowered.server.effect.entity.sound.weather.LightningSoundEffect
import org.lanternpowered.server.entity.EntityCreationData
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.EntityProtocolTypes
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.Transaction
import org.spongepowered.api.entity.weather.LightningBolt
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource
import org.spongepowered.api.util.AABB
import org.spongepowered.api.world.BlockChangeFlags
import kotlin.time.Duration
import kotlin.time.seconds

class LanternLightningBolt(creationData: EntityCreationData) : LanternEntity(creationData), LightningBolt {

    companion object {

        val DEFAULT_SOUND_COLLECTION = EntityEffectCollection.builder()
                .add(EntityEffectTypes.LIGHTNING, LightningSoundEffect)
                .build()

        /**
         * The region that the [LightningBolt] will damage [Entity]s.
         */
        private val ENTITY_STRIKE_REGION = AABB(-3.0, -3.0, -3.0, 3.0, 9.0, 3.0)
    }

    /**
     * The time that the lightning bolt will be alive.
     */
    private var timeToLive = 0.5.seconds

    private var remove = false

    init {
        this.protocolType = EntityProtocolTypes.LIGHTNING
        this.effectCollection = DEFAULT_SOUND_COLLECTION.copy()
        this.soundCategory = SoundCategory.WEATHER
    }

    override fun registerKeys() {
        super.registerKeys()

        keyRegistry {
            register(Keys.IS_EFFECT_ONLY, false)
        }
    }

    override fun update(deltaTime: Duration) {
        super.update(deltaTime)

        this.timeToLive -= deltaTime
        if (this.timeToLive > Duration.ZERO)
            return

        val causeStack = CauseStack.current()
        if (this.remove) {
            causeStack.withFrame { frame ->
                // Add this entity to the cause of removal
                frame.pushCause(this)
                // Throw the expire and post event
                val cause = frame.currentCause
                EventManager.post(LanternEventFactory.createExpireEntityEvent(cause, this))
                EventManager.post(LanternEventFactory.createLightningEventPost(cause))
                this.remove()
            }
        } else {
            this.remove = true

            // Play the sound effect
            this.effectCollection.getCombinedOrEmpty(EntityEffectTypes.LIGHTNING).play(this)

            causeStack.withFrame { frame ->
                frame.pushCause(this)

                val entities: MutableList<Entity> = if (this.require(Keys.IS_EFFECT_ONLY)) {
                    mutableListOf()
                } else {
                    // Move the entity strike region to the lightning position
                    val strikeRegion = ENTITY_STRIKE_REGION.offset(this.position)
                    // Get all intersecting entities
                    this.world.getIntersectingEntities(strikeRegion) { entity -> entity != this }.toMutableList()
                }

                val blockChanges = mutableListOf<Transaction<BlockSnapshot>>()
                val event = LanternEventFactory.createLightningEventStrike(
                        frame.currentCause, entities, blockChanges.asNonNullList())

                // TODO: Create fire, once fire is implemented
                EventManager.post(event)

                if (event.isCancelled)
                    return

                // Construct the damage source
                val damageSource = EntityDamageSource.builder()
                        .entity(this).type(DamageTypes.LIGHTNING).build()
                // Apply all the block changes
                for (transaction in blockChanges) {
                    if (transaction.isValid)
                        transaction.final.restore(false, BlockChangeFlags.ALL)
                }
                // Damage all the entities within the region, or trigger other effects, like zombie pigman etc.
                for (entity in entities)
                    entity.damage(5.0, damageSource)
            }
        }
    }
}
