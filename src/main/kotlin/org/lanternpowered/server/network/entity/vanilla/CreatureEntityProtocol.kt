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
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext
import org.lanternpowered.server.network.value.PackedAngle
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnMobPacket
import org.spongepowered.math.vector.Vector3d

abstract class CreatureEntityProtocol<E : LanternEntity>(entity: E) : LivingEntityProtocol<E>(entity) {

    /**
     * Gets the mob type.
     *
     * @return The mob type
     */
    protected abstract val mobType: NamespacedKey

    override fun spawn(context: EntityProtocolUpdateContext) {
        val rot = this.entity.rotation
        val headRot = this.entity.get(Keys.HEAD_ROTATION).orElse(null)
        val pos = this.entity.position
        val vel = this.entity.get(Keys.VELOCITY).orElse(Vector3d.ZERO)
        val yaw = rot.y
        val pitch = headRot?.x ?: rot.x
        val headYaw = headRot?.y ?: 0.0
        val entityTypeId = EntityNetworkIDs.REGISTRY.require(this.mobType)
        context.sendToAllExceptSelf {
            SpawnMobPacket(this.rootEntityId, this.entity.uniqueId, entityTypeId, pos,
                    PackedAngle.ofDegrees(yaw), PackedAngle.ofDegrees(pitch), PackedAngle.ofDegrees(headYaw), vel)
        }
        this.spawnWithMetadata(context)
        this.spawnWithEquipment(context)
    }
}
