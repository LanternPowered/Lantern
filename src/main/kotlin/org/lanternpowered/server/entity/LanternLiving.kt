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
import org.lanternpowered.api.cause.entity.health.source.HealingSources
import org.lanternpowered.api.cause.withFrame
import org.lanternpowered.api.effect.potion.PotionEffect
import org.lanternpowered.api.effect.potion.PotionEffectBuilder
import org.lanternpowered.api.effect.potion.PotionEffectTypes
import org.lanternpowered.api.entity.spawn.EntitySpawnEntry
import org.lanternpowered.api.entity.spawn.SpawnEventProvider
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.registry.builderOf
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.util.collections.asNonNullList
import org.lanternpowered.api.util.collections.immutableListBuilderOf
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.world.entitySpawner
import org.lanternpowered.api.world.getGameRule
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.LanternServer
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.effect.entity.EntityEffectCollection.Companion.builder
import org.lanternpowered.server.effect.entity.EntityEffectTypes
import org.lanternpowered.server.effect.entity.animation.DefaultLivingDeathAnimation
import org.lanternpowered.server.effect.entity.animation.DefaultLivingHurtAnimation
import org.lanternpowered.server.effect.entity.sound.DefaultLivingFallSoundEffect
import org.lanternpowered.server.effect.entity.sound.DefaultLivingSoundEffect
import org.lanternpowered.server.effect.potion.LanternPotionEffectType
import org.lanternpowered.server.entity.player.LanternPlayer
import org.lanternpowered.server.event.message.sendMessage
import org.spongepowered.api.data.Keys
import org.spongepowered.api.effect.sound.SoundTypes
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.attribute.Attribute
import org.spongepowered.api.entity.attribute.type.AttributeType
import org.spongepowered.api.entity.living.Living
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.entity.projectile.Projectile
import org.spongepowered.api.event.cause.EventContextKeys
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes
import org.spongepowered.api.event.entity.HarvestEntityEvent
import org.spongepowered.api.event.item.inventory.DropItemEvent
import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.world.World
import org.spongepowered.api.world.difficulty.Difficulties
import org.spongepowered.api.world.gamerule.GameRules
import org.spongepowered.math.vector.Vector2d
import org.spongepowered.math.vector.Vector3d
import java.util.Optional
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.seconds

open class LanternLiving(creationData: EntityCreationData) : LanternEntity(creationData), Living {

    companion object {

        val DEFAULT_EFFECT_COLLECTION = builder()
                .add(EntityEffectTypes.HURT, DefaultLivingSoundEffect(EntityBodyPosition.HEAD, SoundTypes.ENTITY_GENERIC_HURT))
                .add(EntityEffectTypes.HURT, DefaultLivingHurtAnimation)
                .add(EntityEffectTypes.DEATH, DefaultLivingSoundEffect(EntityBodyPosition.HEAD, SoundTypes.ENTITY_GENERIC_HURT))
                .add(EntityEffectTypes.DEATH, DefaultLivingDeathAnimation)
                .add(EntityEffectTypes.FALL, DefaultLivingFallSoundEffect(
                        SoundTypes.ENTITY_GENERIC_SMALL_FALL,
                        SoundTypes.ENTITY_GENERIC_BIG_FALL))
                .build()


        /**
         * The amount of time that a [Living] still exists after
         * being killed before it is removed from the [World].
         */
        val DEFAULT_DEATH_BEFORE_REMOVAL_TIME = 1.5.seconds
    }

    init {
        this.effectCollection = DEFAULT_EFFECT_COLLECTION.copy()

        keyRegistry {
            registerBounded(Keys.MAX_AIR, 300).minimum(0).maximum(Int.MAX_VALUE).coerceInBounds()
            registerBounded(Keys.REMAINING_AIR, 300).minimum(0).maximum(Keys.MAX_AIR).coerceInBounds()
            registerBounded(Keys.MAX_HEALTH, 20.0).minimum(0.0).maximum(1024.0).coerceInBounds()
            registerBounded(Keys.HEALTH, 20.0).minimum(0.0).maximum(Keys.MAX_HEALTH).coerceInBounds()
                    .addChangeListener { health ->
                        if (health ?: 0.0 <= 0.0)
                            this.handleDeath()
                    }
            registerBounded(Keys.ABSORPTION, 0.0).minimum(0.0).maximum(1024.0).coerceInBounds()

            register(Keys.POTION_EFFECTS, emptyList())
            register(Keys.LAST_ATTACKER)

            registerProvider(Keys.IS_SNEAKING) {
                supportedBy { this.supports(LanternKeys.POSE) }
                get { this.get(LanternKeys.POSE).orElse(Pose.STANDING) == Pose.SNEAKING }
                set { sneaking ->
                    val pose = this.get(LanternKeys.POSE).orElse(Pose.STANDING)
                    if (pose == Pose.SNEAKING && !sneaking) {
                        this.offer(LanternKeys.POSE, Pose.STANDING)
                    } else if (sneaking) {
                        this.offer(LanternKeys.POSE, Pose.SNEAKING)
                    }
                }
            }

            registerProvider(Keys.HEAD_ROTATION) {
                get { this.headRotation }
                set { value -> this.headRotation = value }
            }
        }
    }

    val server: LanternServer
        get() = LanternGame.server

    /**
     * The rotation of the head.
     */
    var headRotation: Vector3d = Vector3d.ZERO

    // region Death

    val isDead: Boolean
        get() = this.require(Keys.HEALTH) <= 0.0

    private var handledDeath = false

    private fun handleDeath() {
        // Can happen when a dead player joins, just mark the player
        // as dead since the events have already been thrown.
        if (this.nullableWorld == null)
            this.handledDeath = true

        if (this.handledDeath)
            return
        this.handledDeath = true

        val causeStack = CauseStack.current()

        val keepInventory = this is LanternPlayer && this.world.getGameRule(GameRules.KEEP_INVENTORY)
        val audience = this.world
        val nullableMessage = this.getDeathMessage()
        val message = nullableMessage ?: textOf("Living entity ($uniqueId) died.")

        // Post the entity destruction event
        val event = LanternEventFactory.createDestructEntityEventDeath(causeStack.currentCause,
                audience, audience, message, message, this, keepInventory, nullableMessage == null)
        EventManager.post(event)

        // Send the death message, if applicable
        event.sendMessage()

        causeStack.withFrame { frame ->
            // Add the destruct event to the cause, this can be used
            // to track the cause of the entity death.
            frame.pushCause(event)
            // Post the harvest event
            handleDeath(causeStack)
        }

        // Clear the inventory, if keepsInventory is false in the thrown Death event
        if (!event.keepInventory && this is Carrier)
            this.inventory.clear()

        val effects = this.effectCollection
        val effect = effects.getCombined(EntityEffectTypes.DEATH) ?: effects.getCombined(EntityEffectTypes.HURT)
        effect?.play(this)
    }

    protected open fun handleDeath(causeStack: CauseStack) {
        val experience = this.collectExperience(causeStack)
        // Humanoids get their own sub-interface for the event
        val harvestEvent = LanternEventFactory.createHarvestEntityEvent(
                causeStack.currentCause, experience, experience, this)
        EventManager.post(harvestEvent)
        // Finalize the harvest event
        this.finalizeHarvestEvent(causeStack, harvestEvent, mutableListOf())
    }

    /**
     * Finalize the [HarvestEntityEvent]. This will spawn all the dropped
     * [Item]s and [ExperienceOrb]s. But only if the event isn't cancelled.
     *
     * @param causeStack The cause stack
     * @param event The harvest event
     */
    protected open fun finalizeHarvestEvent(causeStack: CauseStack, event: HarvestEntityEvent, drops: MutableList<ItemStackSnapshot>) {
        if (event.isCancelled)
            return
        causeStack.pushCauseFrame().use { frame ->
            frame.pushCause(event)
            val experience = event.experience
            // No experience, don't spawn any entity
            if (experience > 0) {
                frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.EXPERIENCE)
                // Spawn a experience orb with the experience value
                this.world.entitySpawner.spawn(EntityTypes.EXPERIENCE_ORB, this.transform) { entity ->
                    entity.offer(Keys.EXPERIENCE, experience)
                }
            }
            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.DROPPED_ITEM)
            // Collect entity drops
            this.collectDrops(causeStack, drops)
            if (drops.isNotEmpty()) {
                val preDropEvent: DropItemEvent.Pre = LanternEventFactory.createDropItemEventPre(
                        frame.currentCause, drops.toImmutableList(), drops.asNonNullList())
                EventManager.post(preDropEvent)
                if (!preDropEvent.isCancelled) {
                    val transform = this.transform.withPosition(this.boundingBox.map { it.center }.orElse(Vector3d.ZERO))
                    val entries = drops.asSequence()
                            .filter { snapshot -> !snapshot.isEmpty }
                            .map { snapshot ->
                                EntitySpawnEntry(EntityTypes.ITEM, transform) {
                                    offer(Keys.ITEM_STACK_SNAPSHOT, snapshot)
                                    offer(Keys.PICKUP_DELAY, 15)
                                }
                            }
                            .toList()
                    this.world.entitySpawner.spawn(entries, SpawnEventProvider(LanternEventFactory::createDropItemEventDestruct))
                }
            }
        }
    }

    override fun handleDamage(causeStack: CauseStack, damageSource: DamageSource, damage: Double, newHealth: Double) {
        super.handleDamage(causeStack, damageSource, damage, newHealth)

        // The death animation will be played in handleDeath
        if (newHealth > 0)
            this.effectCollection.getCombinedOrEmpty(EntityEffectTypes.HURT).play(this)
    }

    /**
     * Collects a experience value from this [Living]. This is the
     * amount of experience that will be dropped when the entity is killed.
     *
     * The [CauseStack] may be used to retrieve contextual data how
     * the [Living] got killed.
     *
     * @param causeStack The cause stack
     * @return The experience value
     */
    protected open fun collectExperience(causeStack: CauseStack): Int = 0

    /**
     * Collects all the dropped [ItemStackSnapshot]s for this [Living].
     *
     * @param causeStack The cause stack
     * @param itemStackSnapshots The item stack snapshots
     */
    protected open fun collectDrops(causeStack: CauseStack, itemStackSnapshots: List<ItemStackSnapshot>) {}

    /**
     * The duration this entity has been death for.
     */
    private var deathTimer = Duration.ZERO

    /**
     * Updates this [Living] and checks if the entity is still alive, a
     * dead entity will be removed after a specific amount of time.
     *
     * @return Whether the entity is dead
     */
    protected open fun updateDeath(deltaTime: Duration): Boolean {
        return if (this.isDead) {
            this.deathTimer += deltaTime
            if (this.deathTimer > DEFAULT_DEATH_BEFORE_REMOVAL_TIME)
                this.unload(UnloadState.REMOVED)
            true
        } else {
            this.deathTimer = Duration.ZERO
            false
        }
    }

    protected open fun getDeathMessage(): Text? = null

    override fun postRemoveEvent() {
        // Is already handled with the death event
        if (this.isDead)
            return
        super.postRemoveEvent()
    }

    // endregion

    // region Food and Potions

    private fun updatePotionEffects(deltaTime: Duration) {
        // TODO: Move potion effects to a component? + The key registration
        val effects = this.require(Keys.POTION_EFFECTS)
        if (effects.isEmpty())
            return
        // TODO: Use duration
        val deltaTicks = (deltaTime.inMilliseconds / 50).toInt()

        val builder: PotionEffectBuilder = builderOf()
        val newEffects = immutableListBuilderOf<PotionEffect>()
        for (effect in effects) {
            val instant = effect.type.isInstant
            val duration = if (instant) 1 else effect.duration - deltaTicks
            if (duration > 0) {
                val newPotionEffect = builder.from(effect).duration(duration).build()
                (newPotionEffect.type as LanternPotionEffectType).effectConsumer(this, newPotionEffect)
                if (!instant)
                    newEffects.add(newPotionEffect)
                if (effect.type == PotionEffectTypes.GLOWING.get()) {
                    this.offer(Keys.IS_GLOWING, duration > 0)
                } else if (effect.type == PotionEffectTypes.INVISIBILITY.get()) {
                    this.offer(Keys.IS_INVISIBLE, duration > 0)
                } else if (effect.type == PotionEffectTypes.HUNGER.get() && this.supports(Keys.EXHAUSTION)) {
                    val oldExhaustion = this.get(Keys.EXHAUSTION).orElse(0.0)
                    val newExhaustion = oldExhaustion + deltaTicks.toDouble() * 0.005 * (effect.amplifier + 1.0)
                    this.offer(Keys.EXHAUSTION, newExhaustion)
                } else if (effect.type == PotionEffectTypes.SATURATION.get() && this.supports(Keys.SATURATION)) {
                    val amount = effect.amplifier + 1
                    val newFood = this.get(Keys.FOOD_LEVEL).orElse(0) + amount
                    val newSaturation = min(this.get(Keys.SATURATION).orElse(0.0) + amount * 2, newFood.toDouble())
                    this.offer(Keys.FOOD_LEVEL, newFood)
                    this.offer(Keys.SATURATION, newSaturation)
                }
            }
        }
        this.offer(Keys.POTION_EFFECTS, newEffects.build())
    }

    private var foodUpdateTimer = Duration.ZERO
    private var peacefulHealthUpdateTimer = Duration.ZERO
    private var peacefulFoodUpdateTimer = Duration.ZERO

    private fun updateFood(deltaTime: Duration) {
        if (!this.supports(Keys.FOOD_LEVEL) ||
                this.get(Keys.GAME_MODE).orElse(null) == GameModes.CREATIVE.get())
            return

        val difficulty = this.world.difficulty

        var exhaustion = this.getOrElse(Keys.EXHAUSTION, 0.0)
        var saturation = this.getOrElse(Keys.SATURATION, 0.0)
        var food = this.require(Keys.FOOD_LEVEL)

        val maxFood = this.getOrElse(LanternKeys.MAX_FOOD_LEVEL, 20)
        val maxExhaustion = this.getOrElse(LanternKeys.MAX_EXHAUSTION, 4.0)

        if (exhaustion >= maxExhaustion) {
            if (saturation > 0.0) {
                this.offer(Keys.SATURATION, saturation - 1.0)
                // Get the updated saturation
                saturation = this.getOrElse(Keys.SATURATION, 0.0)
            } else if (difficulty != Difficulties.PEACEFUL.get()) {
                this.offer(Keys.FOOD_LEVEL, food - 1)
                // Get the updated food level
                food = this.require(Keys.FOOD_LEVEL)
            }
            this.offer(Keys.EXHAUSTION, 0.0)
            exhaustion = this.getOrElse(Keys.EXHAUSTION, 0.0)
        }

        // Health regeneration by food

        val naturalRegeneration = this.world.getGameRule(GameRules.NATURAL_REGENERATION)

        this.foodUpdateTimer += deltaTime
        if (naturalRegeneration && saturation > 0.0 && food >= maxFood) {
            if (this.foodUpdateTimer > 0.5.seconds) {
                val amount = min(saturation, 6.0)
                this.heal(amount / 6.0, HealingSources.FOOD)
                this.offer(Keys.EXHAUSTION, exhaustion + amount)
                this.foodUpdateTimer -= 0.5.seconds
            }
        } else if (naturalRegeneration && food >= maxFood.toDouble() * 0.9) {
            if (this.foodUpdateTimer > 4.seconds) {
                this.heal(1.0, HealingSources.FOOD)
                this.offer(Keys.EXHAUSTION, exhaustion + 6.0)
                this.foodUpdateTimer -= 4.seconds
            }
        } else if (food <= 0.0) {
            if (this.foodUpdateTimer > 4.seconds) {
                val health = this.get(Keys.HEALTH).orElse(0.0)
                if ((health > 10.0 && difficulty == Difficulties.EASY.get()) ||
                        (health > 1.0 && difficulty == Difficulties.NORMAL.get()) || difficulty == Difficulties.HARD) {
                    this.damage(1.0, DamageSources.STARVATION)
                }
                this.foodUpdateTimer -= 4.seconds
            }
        } else {
            this.foodUpdateTimer = Duration.ZERO
        }

        // Peaceful health and food regeneration

        this.peacefulHealthUpdateTimer += deltaTime
        this.peacefulFoodUpdateTimer += deltaTime
        if (naturalRegeneration && difficulty == Difficulties.PEACEFUL.get()) {
            if (this.peacefulHealthUpdateTimer >= 1.seconds) {
                this.heal(1.0, HealingSources.MAGIC)
                this.peacefulHealthUpdateTimer -= 1.seconds
            }
            if (this.peacefulFoodUpdateTimer >= 0.5.seconds) {
                if (food < maxFood)
                    this.offer(Keys.FOOD_LEVEL, food + 1)
                this.peacefulFoodUpdateTimer -= 0.5.seconds
            }
        }
    }

    // endregion

    override fun update(deltaTime: Duration) {
        if (this.updateDeath(deltaTime))
            return
        this.updateLiving(deltaTime)
    }

    /**
     * Updates this entity during the state that
     * this entity is still alive.
     */
    protected open fun updateLiving(deltaTime: Duration) {
        super.update(deltaTime)

        this.updatePotionEffects(deltaTime)
        this.updateFood(deltaTime)
    }

    override fun getTeamRepresentation(): Text = textOf(this.uniqueId.toString())

    override fun getAttribute(type: AttributeType): Optional<Attribute> {
        TODO("Not yet implemented")
    }

    override fun <T : Projectile> launchProjectileTo(projectileClass: Class<T>, target: Entity): Optional<T> = emptyOptional()

    override fun <T : Projectile> launchProjectile(projectileClass: Class<T>): Optional<T> = emptyOptional()

    override fun <T : Projectile> launchProjectile(projectileClass: Class<T>, velocity: Vector3d): Optional<T> = emptyOptional()

    override fun lookAt(targetPos: Vector3d) {
        val eyePos = this.require(Keys.EYE_POSITION) ?: return

        // TODO: Simplify this

        val xz1 = eyePos.toVector2(true)
        val xz2 = targetPos.toVector2(true)
        val distance = xz1.distance(xz2)

        if (distance == 0.0)
            return

        // calculate pitch
        var p1 = Vector2d.UNIT_Y.mul(eyePos.y)
        var p2 = Vector2d(distance, targetPos.y)
        var v1 = p2.sub(p1)
        var v2 = Vector2d.UNIT_X.mul(distance)
        val pitchRad = acos(v1.dot(v2) / (v1.length() * v2.length()))
        val pitchDeg = pitchRad * 180 / Math.PI * (-v1.y / abs(v1.y))

        // calculate yaw
        p1 = xz1
        p2 = xz2
        v1 = p2.sub(p1)
        v2 = Vector2d.UNIT_Y.mul(v1.y)
        val yawRad = acos(v1.dot(v2) / (v1.length() * v2.length()))
        var yawDeg = yawRad * 180 / Math.PI
        if (v1.x < 0 && v1.y < 0) {
            yawDeg = 180 - yawDeg
        } else if (v1.x > 0 && v1.y < 0) {
            yawDeg = 270 - (90 - yawDeg)
        } else if (v1.x > 0 && v1.y > 0) {
            yawDeg = 270 + (90 - yawDeg)
        }

        this.headRotation = Vector3d(pitchDeg, yawDeg, this.headRotation.z)
        this.rotation = Vector3d(pitchDeg, yawDeg, this.rotation.z)
    }
}
