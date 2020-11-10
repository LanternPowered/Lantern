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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.server.data.SpongeKeys
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.EmptyEntityUpdateContext
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext
import org.lanternpowered.server.network.value.PackedAngle
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnObjectPacket
import org.spongepowered.math.vector.Vector3d

abstract class ObjectEntityProtocol<E : LanternEntity>(entity: E) : EntityProtocol<E>(entity) {

    private var lastObjectData = 0

    protected abstract val objectType: NamespacedKey
    protected abstract val objectData: Int

    override fun spawn(context: EntityProtocolUpdateContext) {
        val entityId = this.rootEntityId
        val rot = this.entity.rotation
        val pos = this.entity.position
        val velocity = this.entity.get(SpongeKeys.VELOCITY).orElse(Vector3d.ZERO)
        val yaw = rot.y
        val pitch = rot.x
        context.sendToAllExceptSelf {
            val internalType = EntityNetworkIDs.REGISTRY.require(this.objectType)
            SpawnObjectPacket(entityId, entity.uniqueId, internalType, this.objectData, pos,
                    PackedAngle.ofDegrees(yaw), PackedAngle.ofDegrees(pitch), velocity)
        }
        this.spawnWithMetadata(context)
    }

    override fun update(context: EntityProtocolUpdateContext) {
        val objectData = this.objectData
        if (this.lastObjectData != objectData) {
            this.spawn(context)
            super.update(EmptyEntityUpdateContext)
            this.lastObjectData = objectData
        } else {
            super.update(context)
        }
    }
}
