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
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.EmptyEntityUpdateContext
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext
import org.lanternpowered.server.network.vanilla.packet.type.play.DestroyEntitiesPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnExperienceOrbPacket

class ExperienceOrbEntityProtocol<E : LanternEntity>(entity: E) : EntityProtocol<E>(entity) {

    private var lastQuantity = 0

    override fun spawn(context: EntityProtocolUpdateContext) {
        this.spawn(context, this.entity.get(Keys.EXPERIENCE).orElse(1))
    }

    private fun spawn(context: EntityProtocolUpdateContext, quantity: Int) {
        if (quantity == 0) {
            context.sendToAll { DestroyEntitiesPacket(this.rootEntityId) }
        } else {
            context.sendToAll { SpawnExperienceOrbPacket(this.rootEntityId, quantity, this.entity.position) }
        }
    }

    override fun update(context: EntityProtocolUpdateContext) {
        val quantity: Int = this.entity.get(Keys.EXPERIENCE).orElse(1)
        if (this.lastQuantity != quantity) {
            this.spawn(context, quantity)
            super.update(EmptyEntityUpdateContext)
            this.lastQuantity = quantity
        } else {
            super.update(context)
        }
    }
}
