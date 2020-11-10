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

import org.lanternpowered.api.data.Keys
import org.lanternpowered.api.data.eq
import org.lanternpowered.api.data.neq
import org.lanternpowered.api.item.ItemTypes
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.entity.event.EntityEvent
import org.lanternpowered.server.entity.event.RefreshAbilitiesPlayerEvent
import org.lanternpowered.server.entity.event.SpectateEntityEvent
import org.lanternpowered.server.entity.player.LanternPlayer
import org.lanternpowered.server.inventory.LanternItemStack
import org.lanternpowered.server.network.entity.EntityProtocolInitContext
import org.lanternpowered.server.network.entity.EntityProtocolManager
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext
import org.lanternpowered.server.network.entity.parameter.MutableParameterList
import org.lanternpowered.server.network.entity.parameter.ParameterList
import org.lanternpowered.server.network.value.PackedAngle
import org.lanternpowered.server.network.vanilla.packet.type.play.DestroyEntitiesPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityMetadataPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerAbilitiesPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerHealthPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SetCameraPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SetGameModePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnObjectPacket
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.math.vector.Vector3d
import java.util.UUID
import kotlin.math.min

class PlayerEntityProtocol(entity: LanternPlayer) : HumanoidEntityProtocol<LanternPlayer>(entity) {

    private var lastHasNoGravity = false
    private var lastGameMode = GameModes.NOT_SET.get()
    private var elytraRocketId = EntityProtocolManager.INVALID_ENTITY_ID
    private var lastElytraFlying = false
    private var lastElytraSpeedBoost = false
    private var lastCanFly = false
    private var lastFlySpeed = 0f
    private var lastFlying = false
    private var lastFieldOfView = 0f
    private var lastAbsorption = 0f
    private var lastHealth = 0f
    private var lastFood = 0
    private var lastHungry = true

    init {
        this.tickRate = 1
    }

    override fun init(context: EntityProtocolInitContext) {
        super.init(context)
        this.elytraRocketId = context.acquire()
    }

    override fun remove(context: EntityProtocolInitContext) {
        super.remove(context)
        context.release(this.elytraRocketId)
        this.elytraRocketId = EntityProtocolManager.INVALID_ENTITY_ID
    }

    override fun spawn(context: EntityProtocolUpdateContext) {
        super.spawn(context)
        context.sendToSelf { EntityMetadataPacket(rootEntityId, this.fillSpawnParameters()) }
        val gameMode = this.entity.require(Keys.GAME_MODE)
        context.sendToSelf { SetGameModePacket(gameMode) }
        context.sendToSelf {
            PlayerAbilitiesPacket(entity.get(Keys.IS_FLYING).orElse(false),
                    this.canFly(), false, gameMode eq GameModes.CREATIVE, this.flySpeed, this.fovModifier)
        }
    }

    override fun update(context: EntityProtocolUpdateContext) {
        val gameMode = this.entity.require(Keys.GAME_MODE)
        val canFly = this.canFly()
        val flySpeed = this.flySpeed
        val fieldOfView = this.fovModifier
        val flying = this.entity.get(Keys.IS_FLYING).orElse(false)
        if (gameMode neq this.lastGameMode || canFly != this.lastCanFly || flySpeed != this.lastFlySpeed ||
                fieldOfView != this.lastFieldOfView || flying != this.lastFlying) {
            if (gameMode neq this.lastGameMode)
                context.sendToSelf { SetGameModePacket(gameMode) }
            context.sendToSelf {
                PlayerAbilitiesPacket(flying, canFly, false, gameMode eq GameModes.CREATIVE, flySpeed, fieldOfView)
            }
            this.lastGameMode = gameMode
            this.lastCanFly = canFly
            this.lastFlySpeed = flySpeed
            this.lastFieldOfView = fieldOfView
            this.lastFlying = flying
        }
        val health = this.entity.require(Keys.HEALTH).toFloat()
        val foodLevel = this.entity.require(Keys.FOOD).toInt()
        val saturation = this.entity.require(Keys.SATURATION).toFloat()
        if (health != this.lastHealth || foodLevel != this.lastFood || saturation == 0.0f != this.lastHungry) {
            context.sendToSelf { PlayerHealthPacket(health, foodLevel.toFloat(), saturation) }
            this.lastHealth = health
            this.lastFood = foodLevel
            this.lastHungry = saturation == 0.0f
        }
        super.update(context)
        // Some 1.11.2 magic, ultra secret stuff...
        val elytraFlying = this.entity.get(Keys.IS_ELYTRA_FLYING).orElse(false)
        val elytraSpeedBoost = this.entity.get(LanternKeys.ELYTRA_SPEED_BOOST).orElse(false)
        if (this.lastElytraFlying != elytraFlying || this.lastElytraSpeedBoost != elytraSpeedBoost) {
            if (this.lastElytraFlying && this.lastElytraSpeedBoost) {
                context.sendToAll { DestroyEntitiesPacket(this.elytraRocketId) }
            } else if (elytraFlying && elytraSpeedBoost) {
                // Create the fireworks data item
                val itemStack = LanternItemStack(ItemTypes.FIREWORK_ROCKET.get())

                // Write the item to a parameter list
                val parameterList = MutableParameterList()
                parameterList.add(EntityParameters.Fireworks.ITEM, itemStack)
                parameterList.add(EntityParameters.Fireworks.ELYTRA_BOOST_PLAYER, this.entity.networkId)
                context.sendToAll {
                    SpawnObjectPacket(this.elytraRocketId, UUID.randomUUID(), 76, 0,
                            this.entity.position, PackedAngle.Zero, PackedAngle.Zero, Vector3d.ZERO)
                }
                context.sendToAll { EntityMetadataPacket(elytraRocketId, parameterList) }
            }
            this.lastElytraSpeedBoost = elytraSpeedBoost
            this.lastElytraFlying = elytraFlying
        }
    }

    override fun handleEvent(context: EntityProtocolUpdateContext, event: EntityEvent) {
        if (event is SpectateEntityEvent) {
            val entity = event.spectatedEntity.orElse(null)
            if (entity == null) {
                context.sendToSelf { SetCameraPacket(this.rootEntityId) }
            } else {
                context.getId(entity).ifPresent { id -> context.sendToSelf { SetCameraPacket(id) } }
            }
        } else if (event is RefreshAbilitiesPlayerEvent) {
            val gameMode = this.entity.require(Keys.GAME_MODE)
            val flySpeed = this.flySpeed
            val fov = this.fovModifier
            context.sendToSelf {
                PlayerAbilitiesPacket(this.entity.get(Keys.IS_FLYING).orElse(false),
                        this.canFly(), false, gameMode eq GameModes.CREATIVE, flySpeed, fov)
            }
        } else {
            super.handleEvent(context, event)
        }
    }

    override val isSprinting: Boolean
        get() = this.entity.get(Keys.IS_SPRINTING).orElse(false)

    private val fovModifier: Float
        get() {
            // client = (walkSpeed / fovModifier + 1) / 2
            // fovModifier = walkSpeed / (client * 2 - 1)
            val walkSpeed = this.entity.get(Keys.WALKING_SPEED).orElse(0.1).toFloat()
            val client = this.entity.get(LanternKeys.FIELD_OF_VIEW_MODIFIER).orElse(1.0).toFloat()
            return walkSpeed / (client * 2f - 1f)
        }

    private val flySpeed: Float
        get() {
            return when {
                this.entity.get(Keys.IS_ELYTRA_FLYING).orElse(false) -> {
                    this.entity.get(LanternKeys.ELYTRA_GLIDE_SPEED).orElse(0.1).toFloat()
                }
                this.entity.get(Keys.CAN_FLY).orElse(false) -> {
                    this.entity.get(Keys.FLYING_SPEED).orElse(0.1).toFloat()
                }
                else -> 0f
            }
        }

    private fun canFly(): Boolean {
        // TODO: Double jump?
        return this.entity.get(Keys.CAN_FLY).orElse(false) || this.entity.get(LanternKeys.CAN_WALL_JUMP).orElse(false) ||
                this.entity.get(LanternKeys.SUPER_STEVE).orElse(false) && !this.entity.get(Keys.IS_ELYTRA_FLYING).orElse(false)
    }

    private val absorption: Float
        get() = this.entity.require(Keys.ABSORPTION).toFloat()

    override fun spawn(parameterList: ParameterList) {
        super.spawn(parameterList)
        parameterList.add(EntityParameters.Humanoid.SCORE, this.entity.get(LanternKeys.SCORE).orElse(0))
        parameterList.add(EntityParameters.Humanoid.ADDITIONAL_HEARTS, this.absorption)
    }

    override fun update(parameterList: ParameterList) {
        super.update(parameterList)
        val hasNoGravity = this.hasNoGravity()
        if (hasNoGravity != this.lastHasNoGravity) {
            parameterList.add(EntityParameters.Base.NO_GRAVITY, hasNoGravity)
            this.lastHasNoGravity = hasNoGravity
        }
        val absorption = this.absorption
        if (absorption != this.lastAbsorption) {
            parameterList.add(EntityParameters.Humanoid.ADDITIONAL_HEARTS, absorption)
            this.lastAbsorption = absorption
        }
    }

    override fun hasNoGravity(): Boolean = !this.entity.get(Keys.IS_GRAVITY_AFFECTED).orElse(true)

    override val airLevel: Int
        get() {
            val max = this.entity.get(Keys.MAX_AIR).orElse(300).toDouble()
            val air = this.entity.get(Keys.REMAINING_AIR).orElse(300).toDouble()
            return (min(1.0, air / max) * 300.0).toInt()
        }
}
