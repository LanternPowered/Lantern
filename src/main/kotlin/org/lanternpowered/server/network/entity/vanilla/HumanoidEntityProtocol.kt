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
import org.lanternpowered.api.text.Text
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext
import org.lanternpowered.server.network.entity.parameter.ParameterList
import org.lanternpowered.server.network.value.PackedAngle
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityHeadLookPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityVelocityPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnPlayerPacket
import org.lanternpowered.server.registry.type.data.SkinPartRegistry
import org.spongepowered.api.data.type.HandPreferences
import org.spongepowered.math.vector.Vector3d

abstract class HumanoidEntityProtocol<E : LanternEntity>(entity: E) : LivingEntityProtocol<E>(entity) {

    private var lastDominantHand: Byte? = null
    private var lastSkinParts: Byte? = null

    private val dominantHand: Byte
        get() = (if (this.entity.get(Keys.DOMINANT_HAND).orElseGet(HandPreferences.RIGHT) eq HandPreferences.RIGHT) 1 else 0).toByte()

    private val skinParts: Byte
        get() = this.entity.get(Keys.DISPLAYED_SKIN_PARTS).map { SkinPartRegistry.toBitPattern(it).toByte() }.orElse(0)

    override fun spawn(context: EntityProtocolUpdateContext) {
        val entityId = this.rootEntityId
        val rot = this.entity.rotation
        val headRot = entity.get(Keys.HEAD_ROTATION).orElse(null)
        val pos = entity.position
        val vel = entity.get(Keys.VELOCITY).orElse(Vector3d.ZERO)
        val yaw = rot.y
        val pitch = headRot?.x ?: rot.x
        context.sendToAllExceptSelf {
            SpawnPlayerPacket(entityId, entity.uniqueId, pos, PackedAngle.ofDegrees(yaw), PackedAngle.ofDegrees(pitch))
        }
        if (headRot != null)
            context.sendToAllExceptSelf { EntityHeadLookPacket(entityId, PackedAngle.ofDegrees(headRot.y)) }
        if (vel != Vector3d.ZERO)
            context.sendToAllExceptSelf { EntityVelocityPacket(entityId, vel.x, vel.y, vel.z) }
        this.spawnWithMetadata(context)
        this.spawnWithEquipment(context)
    }

    override fun spawn(parameterList: ParameterList) {
        super.spawn(parameterList)
        // Ignore the NoAI tag, isn't used on the client
        parameterList.add(EntityParameters.Humanoid.MAIN_HAND, this.dominantHand)
        parameterList.add(EntityParameters.Humanoid.SKIN_PARTS, this.skinParts)
    }

    override fun update(parameterList: ParameterList) {
        super.update(parameterList)
        val dominantHand = this.dominantHand
        if (dominantHand != this.lastDominantHand) {
            parameterList.add(EntityParameters.Humanoid.MAIN_HAND, dominantHand)
            this.lastDominantHand = dominantHand
        }
        val skinParts = this.skinParts
        if (skinParts != this.lastSkinParts) {
            parameterList.add(EntityParameters.Humanoid.SKIN_PARTS, skinParts)
            this.lastSkinParts = skinParts
        }
    }

    override fun hasEquipment(): Boolean = true

    // The display doesn't have to be updated through
    // the parameters for humanoids
    override val isCustomNameVisible: Boolean
        get() = false

    // The display doesn't have to be updated through
    // the parameters for humanoids
    override val customName: Text?
        get() = null
}
