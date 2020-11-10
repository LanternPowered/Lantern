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

import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.entity.event.EntityEvent
import org.lanternpowered.server.entity.event.LoveModeEntityEvent
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityStatusPacket

abstract class AnimalEntityProtocol<E : LanternEntity> protected constructor(entity: E) : AgeableEntityProtocol<E>(entity) {

    override fun handleEvent(context: EntityProtocolUpdateContext, event: EntityEvent) {
        if (event is LoveModeEntityEvent) {
            context.sendToAll { EntityStatusPacket(this.rootEntityId, 18) }
        } else {
            super.handleEvent(context, event)
        }
    }
}
