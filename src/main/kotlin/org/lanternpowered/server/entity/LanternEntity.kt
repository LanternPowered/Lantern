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

import org.lanternpowered.api.audience.emptyAudience
import org.lanternpowered.api.cause.CauseContextKeys
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.causeOf
import org.lanternpowered.api.cause.entity.health.source.HealingSource
import org.lanternpowered.api.cause.withFrame
import org.lanternpowered.api.data.holder.transform
import org.lanternpowered.api.effect.sound.SoundCategory
import org.lanternpowered.api.effect.sound.soundEffectOf
import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.text.emptyText
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.util.AABB
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.api.world.Location
import org.lanternpowered.api.world.World
import org.lanternpowered.server.data.DataHelper
import org.lanternpowered.server.data.DataQueries
import org.lanternpowered.server.data.LocalKeyRegistry
import org.lanternpowered.server.data.SerializableLocalMutableDataHolder
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.effect.entity.EntityEffectCollection
import org.lanternpowered.server.effect.entity.EntityEffectTypes
import org.lanternpowered.server.entity.event.EntityEvent
import org.lanternpowered.server.entity.living.player.LanternPlayer
import org.lanternpowered.server.event.LanternEventContextKeys
import org.lanternpowered.server.event.message.sendMessage
import org.lanternpowered.server.network.entity.EntityProtocolType
import org.lanternpowered.server.util.LanternTransform
import org.lanternpowered.server.util.Quaternions
import org.lanternpowered.server.world.LanternLocation
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.effect.sound.SoundType
import org.spongepowered.api.entity.EntityArchetype
import org.spongepowered.api.entity.EntitySnapshot
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.event.cause.entity.damage.DamageFunction
import org.spongepowered.api.event.cause.entity.damage.DamageModifier
import org.spongepowered.api.event.cause.entity.damage.DamageModifierTypes
import org.spongepowered.api.event.cause.entity.damage.DamageTypes
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource
import org.spongepowered.api.event.entity.DamageEntityEvent
import org.spongepowered.api.util.Direction
import org.spongepowered.api.util.Direction.Division
import org.spongepowered.api.util.RelativePositions
import org.spongepowered.api.util.Transform
import org.spongepowered.api.world.ServerLocation
import org.spongepowered.math.imaginary.Quaterniond
import org.spongepowered.math.vector.Vector3d
import java.util.EnumSet
import java.util.Optional
import java.util.Random
import java.util.UUID
import java.util.function.DoubleUnaryOperator
import java.util.function.Supplier
import kotlin.time.Duration
import kotlin.time.milliseconds
import kotlin.time.seconds

abstract class LanternEntity(creationData: EntityCreationData) : SerializableLocalMutableDataHolder, Entity {

    private val random = Random()

    private val uniqueId = creationData.uniqueId
    private val entityType = creationData.entityType

    override fun getUniqueId(): UUID = this.uniqueId
    override fun getType(): EntityType<*> = this.entityType

    override val keyRegistry: LocalKeyRegistry<out LanternEntity> = LocalKeyRegistry.of()

    private var _world: World? = null
    private var scale = Vector3d.ONE
    private var position = Vector3d.ZERO
    private var rotation = Vector3d.ZERO

    private var _protocolType: EntityProtocolType<*>? = null

    private var _boundingBoxExtent: AABB? = null
    private var _boundingBox: AABB? = null

    private var unloadState: UnloadState? = null

    /**
     * Whether the entity is currently on the ground.
     */
    var onGround: Boolean = true

    /**
     * The effect collection of this entity.
     */
    var effectCollection: EntityEffectCollection = EntityEffectCollection.of()

    init {
        this.registerKeys()
    }

    open fun registerKeys() {
        keyRegistry {
            register(Keys.DISPLAY_NAME, emptyText())
            register(Keys.IS_CUSTOM_NAME_VISIBLE, true)
            register(Keys.SCOREBOARD_TAGS, emptySet())
            register(Keys.VELOCITY, Vector3d.ZERO)
            register(Keys.FIRE_TICKS, 0)
            register(Keys.FALL_DISTANCE, 0.0)
            register(Keys.IS_GLOWING, false)
            register(Keys.IS_INVISIBLE, false)
            register(Keys.INVULNERABLE, false)
            register(Keys.IS_GRAVITY_AFFECTED, true)
            register(Keys.CREATOR)
            register(Keys.NOTIFIER)
            register(LanternKeys.PORTAL_COOLDOWN_TICKS, 0)

            registerProvider(Keys.ON_GROUND) {
                get { this.onGround }
            }
        }
        this.registerPassengerKeys()
        this.registerDamageKeys()
    }

    override fun createSnapshot(): EntitySnapshot {
        TODO("Not yet implemented")
    }

    override fun createArchetype(): EntityArchetype {
        TODO("Not yet implemented")
    }

    override fun copy(): Entity {
        TODO("Not yet implemented")
    }

    /**
     * The protocol type this entity should use, if any.
     */
    var protocolType: EntityProtocolType<*>?
        get() = this._protocolType
        set(value) {
            if (value != null)
                check(value.entityType.isInstance(this)) {
                    "The protocol type $value is not applicable to this entity." }
            this._protocolType = value
        }

    @Deprecated(message = "Prefer the kotlin Random.")
    final override fun getRandom(): Random = this.random

    override fun getBoundingBox(): Optional<AABB> {
        var boundingBox = this._boundingBox
        if (boundingBox != null)
            return boundingBox.asOptional()
        val extent = this._boundingBoxExtent ?: return emptyOptional()
        boundingBox = extent.offset(this.position)
        this._boundingBox = boundingBox
        return boundingBox.asOptional()
    }

    var boundingBoxExtent: AABB?
        get() = this._boundingBoxExtent
        set(value) {
            this._boundingBoxExtent = value
            this._boundingBox = null
        }

    val nullableWorld: World?
        get() = this._world

    override fun getWorld(): World =
            this._world ?: error("This entity is currently not present in a world.")

    override fun getLocation(): Location {
        return LanternLocation(this.world, this.position)
    }

    override fun getTransform(): Transform =
            LanternTransform(this.position, this.rotation, this.scale)

    override fun getPosition(): Vector3d = this.position

    override fun setPosition(position: Vector3d): Boolean {
        this.setRawPosition(position)
        return true
    }

    override fun getRotation(): Vector3d = this.rotation
    override fun setRotation(rotation: Vector3d) = this.setRawRotation(rotation)

    final override fun transferToWorld(world: World, position: Vector3d): Boolean =
            this.setLocation(ServerLocation.of(world, position))

    final override fun setTransform(transform: Transform): Boolean {
        val success = this.setPosition(transform.position)
        if (!success)
            return false
        this.setRotation(transform.rotation)
        this.setScale(transform.scale)
        return true
    }

    override fun setLocationAndRotation(location: Location, rotation: Vector3d): Boolean {
        val success = this.setLocation(location)
        if (!success)
            return false
        this.setRotation(rotation)
        return true
    }

    override fun setLocationAndRotation(location: Location, rotation: Vector3d, relativePositions: EnumSet<RelativePositions>): Boolean {
        val world = location.world
        val pos = location.position

        var x = pos.x
        var y = pos.y
        var z = pos.z
        var pitch = rotation.x
        var yaw = rotation.y
        val roll = rotation.z

        if (relativePositions.contains(RelativePositions.X))
            x += this.position.x
        if (relativePositions.contains(RelativePositions.Y))
            y += this.position.y
        if (relativePositions.contains(RelativePositions.Z))
            z += this.position.z
        if (relativePositions.contains(RelativePositions.PITCH))
            pitch += this.rotation.x
        if (relativePositions.contains(RelativePositions.YAW))
            yaw += this.rotation.y

        val success = this.setLocation(Location.of(world, Vector3d(x, y, z)))
        if (!success)
            return false
        this.setRotation(Vector3d(pitch, yaw, roll))
        return true
    }

    override fun setLocation(location: ServerLocation): Boolean {
        // TODO: Transfer entity instance to the other world?
        this.setRawWorld(location.world)
        this.setRawPosition(location.position)
        return true
    }

    fun setRawWorld(world: World?) {
        this._world = world
    }

    fun setRawPosition(position: Vector3d) {
        this.position = position
    }

    fun setRawRotation(rotation: Vector3d) {
        this.rotation = rotation
    }

    override fun getScale(): Vector3d = this.scale
    override fun setScale(scale: Vector3d) { this.scale = scale }

    enum class UnloadState {

        /**
         * The entity was destroyed through the [remove]
         * method (or when it reached zero health). Will not be
         * respawned in any case.
         */
        REMOVED,

        /**
         * The entity was removed due chunk unloading. It will appear
         * as "removed", but it is basically just unloaded.
         */
        CHUNK_UNLOAD
    }

    override fun isLoaded(): Boolean = this.unloadState == null
    override fun isRemoved(): Boolean = this.unloadState == UnloadState.REMOVED

    override fun remove() {
        this.unload(UnloadState.REMOVED)
    }

    fun unload(state: UnloadState) {
        if (this.isRemoved)
            return
        this.unloadState = state
        if (state == UnloadState.REMOVED) {
            this.vehicle = null
            this.passengers = emptyList()
            this.postRemoveEvent()
        }
    }

    protected open fun postRemoveEvent() {
        val causeStack = CauseStack.current()

        val audience = this.nullableWorld ?: emptyAudience()
        val message = textOf("Entity ($uniqueId) got removed.")

        val event = LanternEventFactory.createDestructEntityEvent(causeStack.currentCause,
                audience, audience, message, message, this, true)
        EventManager.post(event)

        event.sendMessage()
    }

    /**
     * Triggers the [EntityEvent] for this entity.
     *
     * @param event The event
     */
    open fun triggerEvent(event: EntityEvent) {
        // TODO: Send protocol event
        // getWorld().entityProtocolManager.triggerEvent(this, event)
    }


    // region Update

    private var lastUpdateTime = -1L

    fun update() {
        val time = System.currentTimeMillis()
        val deltaMillis: Long = if (this.lastUpdateTime == -1L) 1 else time - this.lastUpdateTime
        if (deltaMillis > 0) {
            this.update(deltaMillis.milliseconds)
            this.lastUpdateTime = time
        }
    }

    /**
     * Pulses the entity.
     *
     * @param deltaTime The amount of ticks that passed since the last pulse
     */
    protected open fun update(deltaTime: Duration) {
        val vehicle = this.vehicle
        if (vehicle != null) {
            this.position = vehicle.position
        }
        this.updateDamage(deltaTime)
    }

    // endregion

    // region Data

    override fun validateRawData(dataView: DataView): Boolean =
            dataView.contains(DataQueries.POSITION, DataQueries.ROTATION)

    override fun setRawData(dataView: DataView) {
        setPosition(dataView.getObject(DataQueries.POSITION, Vector3d::class.java).get())
        setRotation(dataView.getObject(DataQueries.ROTATION, Vector3d::class.java).get())
        DataHelper.deserializeRawData(dataView, this)
    }

    override fun getContentVersion(): Int = 1

    override fun toContainer(): DataContainer {
        val dataContainer = DataContainer.createNew()
                .set(DataQueries.ENTITY_TYPE, type)
                .set(DataQueries.POSITION, this.position)
                .set(DataQueries.ROTATION, this.rotation)
        DataHelper.serializeRawData(dataContainer, this)
        return dataContainer
    }

    // endregion

    // region Passengers

    private val _passengers = mutableListOf<LanternEntity>()
    private val _vehicle: LanternEntity? = null

    private fun registerPassengerKeys() {
        keyRegistry {
            registerProvider(Keys.BASE_VEHICLE) {
                get { this.baseVehicle }
            }
            registerProvider(Keys.PASSENGERS) {
                get { this.passengers }
                set { value -> this.passengers = value.uncheckedCast() }
                remove { this.passengers = emptyList() }
            }
            registerProvider(Keys.VEHICLE) {
                get { this.vehicle }
                set { value -> this.vehicle = value.uncheckedCast() }
                remove { this.vehicle = null }
            }
        }
    }

    /**
     * All the passengers of this entity.
     */
    var passengers: List<LanternEntity>
        get() = this._passengers.toList()
        set(value) {
            // Remove old passengers
            for (passenger in this.passengers)
                passenger.vehicle = null
            // Add new passengers
            for (passenger in value)
                passenger.vehicle = this
        }

    /**
     * The vehicle entity this entity is currently riding.
     */
    var vehicle: LanternEntity?
        get() = this._vehicle
        set(value) {
            trySetVehicle(value)
        }

    /**
     * The vehicle entity that is not a passenger of any entity. Meaning
     * it's at the beginning of the passenger stack.
     */
    val baseVehicle: LanternEntity?
        get() {
            // Find the first vehicle in the stack
            var lastEntity: LanternEntity? = this
            while (lastEntity != null)
                lastEntity = lastEntity.vehicle
            return lastEntity
        }

    /**
     * Tries to set the vehicle and gets whether it was successful.
     */
    fun trySetVehicle(vehicle: Entity?): Boolean {
        vehicle as LanternEntity?
        val currentVehicle = this.vehicle
        if (currentVehicle == vehicle)
            return false
        currentVehicle?._passengers?.remove(vehicle)
        this.vehicle = vehicle
        if (vehicle != null) {
            var index = this._passengers.indexOfLast { it is LanternPlayer }
            // If there's no player, insert at index 0, otherwise after the player
            index++
            this._passengers.add(index, vehicle)
        }
        return true
    }

    fun hasPassenger(entity: Entity): Boolean = this._passengers.contains(entity)

    fun addPassenger(entity: Entity): Boolean = (entity as LanternEntity).trySetVehicle(this)

    fun removePassenger(passenger: Entity) {
        passenger as LanternEntity
        if (passenger.vehicle === this)
            passenger.vehicle = null
    }

    // endregion

    // region Damage

    private var voidDamageTimer = Duration.ZERO
    private var lastDamage: Double? = null
    private var lastAttacker: Entity? = null

    private fun updateDamage(deltaTime: Duration) {
        // Don't keep references to removed entities
        if (this.lastAttacker?.isRemoved == true)
            this.lastAttacker = null

        this.updateVoidDamage(deltaTime)
    }

    private fun updateVoidDamage(deltaTime: Duration) {
        // Deal some void damage
        if (this.position.y < -64.0) {
            val voidDamageInterval = 0.5.seconds
            this.voidDamageTimer += deltaTime
            while (this.voidDamageTimer >= voidDamageInterval) {
                damage(4.0, DamageSources.VOID)
                this.voidDamageTimer -= voidDamageInterval
            }
        } else {
            this.voidDamageTimer = Duration.ZERO
        }
    }

    private fun registerDamageKeys() {
        keyRegistry {
            registerProvider(Keys.LAST_DAMAGE_RECEIVED) {
                supportedBy { this.supports(Keys.HEALTH) }
                get { this.lastDamage }
                set { damage -> this.lastDamage = damage }
            }
            registerProvider(Keys.LAST_ATTACKER) {
                supportedBy { this.supports(Keys.HEALTH) }
                get { this.lastAttacker }
                set { entity -> this.lastAttacker = entity }
            }
        }
    }

    override fun damage(damage: Double, damageSource: DamageSource): Boolean {
        if (!this.supports(Keys.HEALTH)) {
            // A special case, make void damage always pass through for
            // entities without health, instantly destroying them.
            if (damageSource.type == DamageTypes.VOID.get()) {
                this.unload(UnloadState.REMOVED)
                return true
            }
            return false
        }
        // Always throw the event. Plugins may want to override default checking behavior.
        var cancelled = false
        // Check if the damage affects creative mode, and check if the player is in creative mode.
        if (!damageSource.doesAffectCreative() && this.get(Keys.GAME_MODE).orElse(null) == GameModes.CREATIVE.get())
            cancelled = true
        val damageFunctions = mutableListOf<Pair<DamageFunction, (DamageEntityEvent) -> Unit>>()
        // Only collect damage modifiers if the event isn't cancelled.
        if (!cancelled)
            this.collectDamageFunctions(damageFunctions)
        val causeStack = CauseStack.current()
        causeStack.withFrame { frame ->
            frame.pushCause(damageSource)
            frame.addContext(CauseContextKeys.DAMAGE_TYPE, damageSource.type)

            val event = LanternEventFactory.createDamageEntityEvent(frame.currentCause, this,
                    damageFunctions.map { damageFunction -> damageFunction.first }, damage)
            event.isCancelled = cancelled
            EventManager.post(event)
            if (event.isCancelled)
                return false

            for ((_, finalizer) in damageFunctions)
                finalizer(event)
            val finalDamage = event.finalDamage
            var health = this.get(Keys.HEALTH).orElse(0.0)
            if (finalDamage > 0) {
                health = (health - finalDamage).coerceAtLeast(0.0)
                this.offer(Keys.HEALTH, health)
            }

            this.lastDamage = finalDamage
            if (damageSource is IndirectEntityDamageSource) {
                this.lastAttacker = damageSource.indirectSource
            } else if (damageSource is EntityDamageSource) {
                this.lastAttacker = damageSource.source
            }

            this.transform(Keys.EXHAUSTION) { value -> value + damageSource.exhaustion }

            // Add some values to the context that may be useful in the handleDamage method,
            // for example the base damage value is used by the falling sound effect
            frame.addContext(LanternEventContextKeys.BASE_DAMAGE_VALUE, finalDamage)
            frame.addContext(LanternEventContextKeys.ORIGINAL_DAMAGE_VALUE, event.originalDamage)
            frame.addContext(LanternEventContextKeys.FINAL_DAMAGE_VALUE, event.finalDamage)

            // Let subclasses do things
            this.handleDamage(causeStack, damageSource, finalDamage, health)
        }
        return true
    }

    protected open fun handleDamage(causeStack: CauseStack, damageSource: DamageSource, damage: Double, newHealth: Double) {
    }

    protected open fun collectDamageFunctions(damageFunctions: MutableList<Pair<DamageFunction, (DamageEntityEvent) -> Unit>>) {
        // TODO: Damage modifiers, etc.
        this.collectAbsorptionFunction(damageFunctions)
    }

    private fun collectAbsorptionFunction(damageFunctions: MutableList<Pair<DamageFunction, (DamageEntityEvent) -> Unit>>) {
        val absorption = this.get(Keys.ABSORPTION).orNull() ?: return
        if (absorption < 0)
            return
        // Absorption health modifier
        val function = DoubleUnaryOperator { damage -> -(damage - (damage - absorption).coerceAtLeast(0.0)).coerceAtLeast(0.0) }
        val modifier = DamageModifier.builder()
                .cause(causeOf(this))
                .type(DamageModifierTypes.ABSORPTION)
                .build()
        val completeFunction: (DamageEntityEvent) -> Unit = { event ->
            val mod = event.getDamage(modifier)
            this.offer(Keys.ABSORPTION, this.get(Keys.ABSORPTION).orElse(0.0) + mod)
        }
        damageFunctions.add(DamageFunction(modifier, function) to completeFunction)
    }

    /**
     * Heals the entity for the specified amount.
     *
     * Will not heal them if they are dead and will not set
     * them above their maximum health.
     *
     * @param amount The amount to heal for
     * @param source The healing source
     */
    open fun heal(amount: Double, source: HealingSource): Boolean {
        if (!this.supports(Keys.HEALTH))
            return false
        val health = this.get(Keys.HEALTH).orElse(0.0)
        if (health == 0.0) // Is already dead
            return false
        val causeStack = CauseStack.current()
        causeStack.withFrame { frame ->
            frame.pushCause(source)
            frame.addContext(LanternEventContextKeys.HEALING_TYPE, source.healingType)

            /*
            final HealEntityEvent event = SpongeEventFactory.createHealEntityEvent(
                    frame.getCurrentCause(), this, new ArrayList<>(), amount);
            Sponge.getEventManager().post(event);
            if (event.isCancelled()) {
                return false;
            }
            amount = event.getFinalHealAmount();
            */

            if (amount > 0)
                this.offer(Keys.HEALTH, health + amount)
        }
        return true
    }

    // endregion

    // region Sounds

    /**
     * The sound category this entity will use to produce sound effects.
     */
    var soundCategory: SoundCategory = SoundCategory.MASTER

    /**
     * Plays a sound which is caused by this [LanternEntity].
     *
     * @param soundType The sound type
     * @param relativeSoundPosition The relative position to the entity to play the sound at
     * @param volume The volume
     * @param pitch The pitch value
     */
    fun playSound(soundType: Supplier<out SoundType>, relativeSoundPosition: Vector3d = Vector3d.ZERO, volume: Double = 1.0, pitch: Double = 1.0) {
        this.playSound(soundType.get(), relativeSoundPosition, volume, pitch)
    }

    /**
     * Plays a sound which is caused by this [LanternEntity].
     *
     * @param soundType The sound type
     * @param relativeSoundPosition The relative position to the entity to play the sound at
     * @param volume The volume
     * @param pitch The pitch value
     */
    fun playSound(soundType: SoundType, relativeSoundPosition: Vector3d = Vector3d.ZERO, volume: Double = 1.0, pitch: Double = 1.0) {
        // Silent entities don't make any sounds
        if (this.get(Keys.IS_SILENT).orElse(false))
            return
        val position = this.position.add(relativeSoundPosition)
        this.world.playSound(soundEffectOf(soundType, this.soundCategory, volume, pitch), position)
    }

    /**
     * Plays a sound which is caused by this [LanternEntity]. Playing a sound oriented around
     * the entity will rotate the relative position based on the orientation of the entity
     * (the y axis in most cases).
     *
     * @param soundType The sound type
     * @param relativeSoundPosition The relative position to the entity to play the sound at
     * @param volume The volume
     * @param pitch The pitch value
     */
    fun playOrientedSound(soundType: Supplier<out SoundType>, relativeSoundPosition: Vector3d, volume: Double = 1.0, pitch: Double = 1.0) {
        this.playOrientedSound(soundType.get(), relativeSoundPosition, volume, pitch)
    }

    /**
     * Plays a sound which is caused by this [LanternEntity]. Playing a sound oriented around
     * the entity will rotate the relative position based on the orientation of the entity
     * (the y axis in most cases).
     *
     * @param soundType The sound type
     * @param relativeSoundPosition The relative position to the entity to play the sound at
     * @param volume The volume
     * @param pitch The pitch value
     */
    fun playOrientedSound(soundType: SoundType, relativeSoundPosition: Vector3d, volume: Double = 1.0, pitch: Double = 1.0) {
        this.playSound(soundType, this.orientRelativePosition(relativeSoundPosition), volume, pitch)
    }

    /**
     * Orients the relative position around the entity based on it's orientation. The
     * relative position should always assume that the entity has no rotation, since
     * it will be rotated by this method.
     *
     * @param relativePosition The relative position
     * @return The oriented relative position
     */
    protected open fun orientRelativePosition(relativePosition: Vector3d): Vector3d {
        val rot = Quaterniond.fromAxesAnglesDeg(0.0, this.rotation.y, 0.0)
        return rot.rotate(relativePosition)
    }

    // endregion

    /**
     * Gets the [Direction] that the entity is looking.
     *
     * @param division The division
     * @return The direction
     */
    fun getDirection(division: Division): Direction =
            Direction.getClosest(this.getDirectionVector(), division)

    fun getDirectionVector(): Vector3d {
        val rotation = this.get(Keys.HEAD_ROTATION).orElse(this.rotation)
        // Invert the x direction because west and east are swapped
        return Quaternions.fromAxesAnglesDeg(rotation.mul(1f, -1f, 1f)).direction
    }

    fun getHorizontalDirectionVector(): Vector3d {
        val rotation = this.get(Keys.HEAD_ROTATION).orElse(this.rotation)
        // Invert the x direction because west and east are swapped
        return Quaternions.fromAxesAnglesDeg(rotation.mul(0f, 1f, 0f)).direction.mul(-1f, 1f, 1f)
    }

    /**
     * Gets the [Direction] that the entity is looking in the horizontal plane.
     *
     * @param division The division
     * @return The direction
     */
    fun getHorizontalDirection(division: Division): Direction =
            Direction.getClosest(this.getHorizontalDirectionVector(), division)
}
