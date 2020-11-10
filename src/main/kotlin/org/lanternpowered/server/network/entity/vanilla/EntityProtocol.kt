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
package org.lanternpowered.server.network.entity.vanilla

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import it.unimi.dsi.fastutil.ints.IntSets
import org.lanternpowered.api.data.Keys
import org.lanternpowered.api.item.inventory.Carrier
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.fix
import org.lanternpowered.api.item.inventory.stack.isSimilarTo
import org.lanternpowered.api.item.inventory.where
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.data.SpongeKeys
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.entity.LanternLiving
import org.lanternpowered.server.entity.Pose
import org.lanternpowered.server.entity.event.CollectEntityEvent
import org.lanternpowered.server.entity.event.EntityEvent
import org.lanternpowered.server.network.entity.AbstractEntityProtocol
import org.lanternpowered.server.network.entity.EmptyEntityUpdateContext
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext
import org.lanternpowered.server.network.entity.parameter.EmptyParameterList
import org.lanternpowered.server.network.entity.parameter.MutableParameterList
import org.lanternpowered.server.network.entity.parameter.ParameterList
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.value.PackedAngle
import org.lanternpowered.server.network.vanilla.packet.type.play.DestroyEntitiesPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityCollectItemPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityEquipmentPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityHeadLookPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityLookAndRelativeMovePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityLookPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityMetadataPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityRelativeMovePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityTeleportPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityVelocityPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SetEntityPassengersPacket
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes
import org.spongepowered.math.vector.Vector3d
import java.util.function.Supplier
import kotlin.math.abs

abstract class EntityProtocol<E : LanternEntity>(entity: E) : AbstractEntityProtocol<E>(entity) {

    private object Holder {

        val EQUIPMENT_TYPES = arrayOf(
                EquipmentTypes.MAIN_HAND.get(),
                EquipmentTypes.OFF_HAND.get(),
                EquipmentTypes.FEET.get(),
                EquipmentTypes.LEGS.get(),
                EquipmentTypes.CHEST.get(),
                EquipmentTypes.HEAD.get()
        )
    }

    private var lastX: Long = 0
    private var lastY: Long = 0
    private var lastZ: Long = 0
    protected var lastYaw = PackedAngle.Zero
    protected var lastPitch = PackedAngle.Zero
    protected var lastHeadYaw = PackedAngle.Zero
    private var lastVelX = 0.0
    private var lastVelY = 0.0
    private var lastVelZ = 0.0
    protected var lastFlags: Byte = 0
    private var lastSilent = false
    private var lastAirLevel = 0
    private var lastCustomNameVisible = false
    private var lastCustomName: Text? = null
    private var lastPose: Pose? = null
    protected var lastPassengers: IntSet = IntSets.EMPTY_SET
    private val lastEquipment = Int2ObjectOpenHashMap<ItemStack>()

    override fun destroy(context: EntityProtocolUpdateContext) {
        context.sendToAllExceptSelf(DestroyEntitiesPacket(this.rootEntityId))
    }

    protected fun spawnWithMetadata(context: EntityProtocolUpdateContext) {
        val parameterList = MutableParameterList()
        this.spawn(parameterList)
        if (!parameterList.isEmpty)
            context.sendToAll { EntityMetadataPacket(this.rootEntityId, parameterList) }
    }

    protected fun spawnWithEquipment(context: EntityProtocolUpdateContext) {
        if (this.entity.onGround)
            context.sendToAllExceptSelf { EntityRelativeMovePacket(this.rootEntityId, 0, 0, 0, true) }
        if (this.hasEquipment() && this.entity is Carrier) {
            val inventory = this.entity.inventory.fix()
            val equipment = lazy { Int2ObjectOpenHashMap<ItemStack>() }
            for (index in Holder.EQUIPMENT_TYPES.indices) {
                val type = Holder.EQUIPMENT_TYPES[index]
                val slot = inventory.slots().where { Keys.EQUIPMENT_TYPE eq type }.firstOrNull()
                if (slot != null) {
                    val item = slot.peek()
                    equipment.value[index] = item
                }
            }
            if (equipment.isInitialized())
                context.sendToAllExceptSelf { EntityEquipmentPacket(this.rootEntityId, equipment.value) }
        }
    }

    override fun update(context: EntityProtocolUpdateContext) {
        val rot = this.entity.rotation
        val headRot = this.entity.get(Keys.HEAD_ROTATION).orElse(null)
        val pos = this.entity.position
        val xu = (pos.x * 4096).toLong()
        val yu = (pos.y * 4096).toLong()
        val zu = (pos.z * 4096).toLong()
        val yaw = PackedAngle.ofDegrees(rot.y)
        // All living entities have a head rotation and changing the pitch
        // would only affect the head pitch.
        val pitch = PackedAngle.ofDegrees((headRot ?: rot).x)
        val dirtyPos = xu != this.lastX || yu != this.lastY || zu != this.lastZ
        var dirtyRot = yaw != this.lastYaw || pitch != this.lastPitch

        // TODO: On ground state
        val onGround = this.entity.onGround
        val entityId = this.rootEntityId
        val passenger = this.entity.vehicle != null
        if (dirtyRot) {
            this.lastYaw = yaw
            this.lastPitch = pitch
        }
        if (dirtyPos) {
            val dxu = xu - this.lastX
            val dyu = yu - this.lastY
            val dzu = zu - this.lastZ
            this.lastX = xu
            this.lastY = yu
            this.lastZ = zu

            // Don't send movement messages if the entity
            // is a passengers, otherwise glitches will
            // rule the world.
            if (!passenger) {
                if (abs(dxu) <= Short.MAX_VALUE && abs(dyu) <= Short.MAX_VALUE && abs(dzu) <= Short.MAX_VALUE) {
                    if (dirtyRot) {
                        context.sendToAllExceptSelf(EntityLookAndRelativeMovePacket(entityId,
                                dxu.toInt(), dyu.toInt(), dzu.toInt(), yaw, pitch, onGround))
                        // The rotation is already send
                        dirtyRot = false
                    } else {
                        context.sendToAllExceptSelf(EntityRelativeMovePacket(entityId,
                                dxu.toInt(), dyu.toInt(), dzu.toInt(), onGround))
                    }
                } else {
                    context.sendToAllExceptSelf(EntityTeleportPacket(entityId,
                            pos, yaw, pitch, onGround))
                    // The rotation is already send
                    dirtyRot = false
                }
            }
        }
        if (dirtyRot) {
            context.sendToAllExceptSelf { EntityLookPacket(entityId, yaw, pitch, onGround) }
        } else if (!passenger) {
            if (headRot != null) {
                val headYaw = PackedAngle.ofDegrees(headRot.y)
                if (headYaw != this.lastHeadYaw) {
                    context.sendToAllExceptSelf { EntityHeadLookPacket(entityId, headYaw) }
                    this.lastHeadYaw = headYaw
                }
            }
        }
        val velocity = entity.get(SpongeKeys.VELOCITY).orElse(Vector3d.ZERO)
        val vx = velocity.x
        val vy = velocity.y
        val vz = velocity.z
        if (vx != lastVelX || vy != lastVelY || vz != lastVelZ) {
            context.sendToAll(Supplier<Packet> { EntityVelocityPacket(entityId, vx, vy, vz) })
            lastVelX = vx
            lastVelY = vy
            lastVelZ = vz
        }
        val parameterList = if (context == EmptyEntityUpdateContext) EmptyParameterList else MutableParameterList()
        this.update(parameterList)
        // There were parameters applied
        if (!parameterList.isEmpty)
            context.sendToAll { EntityMetadataPacket(entityId, parameterList) }
        if (this.hasEquipment() && this.entity is Carrier) {
            val inventory = this.entity.inventory.fix()
            val equipment = lazy { Int2ObjectOpenHashMap<ItemStack>() }
            for (index in Holder.EQUIPMENT_TYPES.indices) {
                val type = Holder.EQUIPMENT_TYPES[index]
                val slot = inventory.slots().where { Keys.EQUIPMENT_TYPE eq type }.firstOrNull()
                if (slot != null) {
                    val item = slot.peek()
                    val last = this.lastEquipment[index]
                    if (!item.isSimilarTo(last)) {
                        this.lastEquipment[index] = item
                        equipment.value[index] = item
                    }
                }
            }
            if (equipment.isInitialized())
                context.sendToAllExceptSelf { EntityEquipmentPacket(this.rootEntityId, equipment.value) }
        }
        // TODO: Update attributes
    }

    override fun updateTranslations(context: EntityProtocolUpdateContext) {
        val parameterList = MutableParameterList()
        this.updateTranslations(parameterList)
        // There were parameters applied
        if (!parameterList.isEmpty)
            context.sendToAll { EntityMetadataPacket(this.rootEntityId, parameterList) }
    }

    /**
     * Gets whether the entity can hold equipment
     * on the client side.
     *
     * @return Has equipment
     */
    protected open fun hasEquipment(): Boolean {
        return false
    }

    override fun handleEvent(context: EntityProtocolUpdateContext, event: EntityEvent) {
        if (event is CollectEntityEvent) {
            val collector = event.collector as LanternLiving
            context.getId(collector).ifPresent { id: Int ->
                val count = event.collectedItemsCount
                context.sendToAll(Supplier<Packet> { EntityCollectItemPacket(id, rootEntityId, count) })
            }
        } else {
            super.handleEvent(context, event)
        }
    }

    override fun postUpdate(context: EntityProtocolUpdateContext) {
        val passengers = getPassengerIds(context)
        if (passengers != lastPassengers) {
            lastPassengers = passengers
            context.sendToAll(SetEntityPassengersPacket(rootEntityId, passengers.toIntArray()))
        }
    }

    public override fun postSpawn(context: EntityProtocolUpdateContext) {
        context.sendToAll(SetEntityPassengersPacket(rootEntityId, getPassengerIds(context).toIntArray()))
    }

    protected open fun getPassengerIds(context: EntityProtocolUpdateContext): IntSet {
        val passengerIds: IntSet = IntOpenHashSet()
        for (passenger in this.entity.passengers) {
            val passengerId = context.getId(passenger)
            if (passengerId.isPresent)
                passengerIds.add(passengerId.asInt)
        }
        return passengerIds
    }

    /**
     * Fills a [ParameterList] with parameters to spawn the [Entity].
     *
     * @return The byte buffer
     */
    fun fillSpawnParameters(): ParameterList {
        val parameterList = MutableParameterList()
        this.spawn(parameterList)
        return parameterList
    }

    protected open val isSprinting: Boolean
        get() = false

    /**
     * Fills the [ParameterList] with parameters to spawn the [Entity] on
     * the client.
     *
     * @param parameterList The parameter list to fill
     */
    protected open fun spawn(parameterList: ParameterList) {
        parameterList.add(EntityParameters.Base.FLAGS, packFlags())
        parameterList.add(EntityParameters.Base.AIR_LEVEL, this.airLevel)
        parameterList.add(EntityParameters.Base.CUSTOM_NAME, this.customName)
        parameterList.add(EntityParameters.Base.CUSTOM_NAME_VISIBLE, this.isCustomNameVisible)
        parameterList.add(EntityParameters.Base.IS_SILENT, this.isSilent)
        parameterList.add(EntityParameters.Base.NO_GRAVITY, this.hasNoGravity())
        parameterList.add(EntityParameters.Base.POSE, this.pose)
    }

    val pose: Pose
        get() = this.entity.get(LanternKeys.POSE).orElse(Pose.STANDING)

    open val isCustomNameVisible: Boolean
        get() = this.entity.get(Keys.IS_CUSTOM_NAME_VISIBLE).orElse(true)

    open val customName: Text?
        get() = this.entity.get(Keys.DISPLAY_NAME).orNull()

    // Always silent for regular entities, we will handle our own sounds
    open val isSilent: Boolean
        get() = true

    open fun hasNoGravity(): Boolean {
        // Always disable gravity for regular entities, we will handle our own physics
        return true
    }

    private fun packFlags(): Byte {
        var flags = 0
        if (this.entity.get(Keys.FIRE_TICKS).orElse(0) > 0)
            flags += EntityParameters.Base.Flags.IS_ON_FIRE
        val pose = this.pose
        if (pose == Pose.SNEAKING)
            flags += EntityParameters.Base.Flags.IS_SNEAKING
        if (this.isSprinting)
            flags += EntityParameters.Base.Flags.IS_SPRINTING
        if (pose == Pose.SWIMMING)
            flags += EntityParameters.Base.Flags.IS_SWIMMING
        if (this.entity.get(Keys.IS_INVISIBLE).orElse(false))
            flags += EntityParameters.Base.Flags.IS_INVISIBLE
        if (this.entity.get(Keys.IS_GLOWING).orElse(false))
            flags += EntityParameters.Base.Flags.IS_GLOWING
        if (this.entity.get(Keys.IS_ELYTRA_FLYING).orElse(false))
            flags += EntityParameters.Base.Flags.IS_ELYTRA_FlYING
        return flags.toByte()
    }

    /**
     * Fills the [ParameterList] with parameters to update the [Entity] on
     * the client.
     *
     * @param parameterList The parameter list to fill
     */
    protected open fun update(parameterList: ParameterList) {
        val flags = packFlags()
        if (flags != this.lastFlags) {
            parameterList.add(EntityParameters.Base.FLAGS, flags)
            this.lastFlags = flags
        }
        val silent = this.isSilent
        if (silent != this.lastSilent) {
            parameterList.add(EntityParameters.Base.IS_SILENT, silent)
            this.lastSilent = silent
        }
        val customNameVisible = this.isCustomNameVisible
        if (customNameVisible != this.lastCustomNameVisible) {
            parameterList.add(EntityParameters.Base.CUSTOM_NAME_VISIBLE, customNameVisible)
            this.lastCustomNameVisible = customNameVisible
        }
        val customName = this.customName
        if (customName != this.lastCustomName) {
            parameterList.add(EntityParameters.Base.CUSTOM_NAME, customName)
            this.lastCustomName = customName
        }
        val airLevel = this.airLevel
        if (airLevel != this.lastAirLevel) {
            parameterList.add(EntityParameters.Base.AIR_LEVEL, airLevel)
            this.lastAirLevel = airLevel
        }
        val pose = this.pose
        if (pose != this.lastPose) {
            parameterList.add(EntityParameters.Base.POSE, pose)
            this.lastPose = pose
        }
    }

    /**
     * Fills the [ParameterList] with parameters to update the [Entity] on
     * the client related to localized [Text].
     *
     * @param parameterList The parameter list to fill
     */
    protected fun updateTranslations(parameterList: ParameterList) {
        val customName = this.customName
        if (customName != null)
            parameterList.add(EntityParameters.Base.CUSTOM_NAME, customName)
    }

    /**
     * Gets the air level of the entity.
     *
     * The air is by default 300 because the entities don't even use the air level,
     * except for the players. This method can be overridden if needed.
     *
     * @return The air level
     */
    protected open val airLevel: Int
        get() = 300
}
