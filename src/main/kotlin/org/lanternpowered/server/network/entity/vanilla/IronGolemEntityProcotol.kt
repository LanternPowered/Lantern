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

import org.lanternpowered.api.data.eq
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.entity.event.EntityEvent
import org.lanternpowered.server.entity.event.SwingHandEntityEvent
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext
import org.lanternpowered.server.network.entity.parameter.ParameterList
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityStatusPacket
import org.spongepowered.api.data.type.HandTypes

class IronGolemEntityProcotol<E : LanternEntity>(entity: E) : InsentientEntityProtocol<E>(entity) {

    companion object {
        private val TYPE = minecraftKey("iron_golem")

        private const val POPPY_ADD_STATUS = 11
        private const val POPPY_REMOVE_STATUS = 34
        private const val POPPY_RESEND_DELAY = 300
    }

    override val mobType: NamespacedKey get() = TYPE

    private var lastHoldPoppyTime = 0

    override fun spawn(parameterList: ParameterList) {
        super.spawn(parameterList)
        parameterList.add(EntityParameters.IronGolem.FLAGS, 0.toByte())
    }

    override fun spawn(context: EntityProtocolUpdateContext) {
        super.spawn(context)
        if (entity.get(LanternKeys.HOLDS_POPPY).orElse(false)) {
            context.sendToAll { EntityStatusPacket(this.rootEntityId, POPPY_ADD_STATUS) }
        }
    }

    override fun update(context: EntityProtocolUpdateContext) {
        super.update(context)
        val holdsPoppy = this.entity.get(LanternKeys.HOLDS_POPPY).orElse(false)
        if (holdsPoppy) {
            this.lastHoldPoppyTime -= this.tickRate
            if (this.lastHoldPoppyTime <= 0) {
                context.sendToAll { EntityStatusPacket(this.rootEntityId, POPPY_ADD_STATUS) }
                this.lastHoldPoppyTime = POPPY_RESEND_DELAY
            }
        } else if (this.lastHoldPoppyTime >= 0) {
            context.sendToAll { EntityStatusPacket(this.rootEntityId, POPPY_REMOVE_STATUS) }
            this.lastHoldPoppyTime = -1
        }
    }

    override fun handleEvent(context: EntityProtocolUpdateContext, event: EntityEvent) {
        if (event is SwingHandEntityEvent) {
            val handType = event.handType
            // Doesn't matter which hand type, just play the swing animation,
            // the golem will use both arms at the same time
            if (handType eq HandTypes.MAIN_HAND || handType eq HandTypes.OFF_HAND) {
                context.sendToAll { EntityStatusPacket(this.rootEntityId, 4) }
            }
        } else {
            super.handleEvent(context, event)
        }
    }
}
