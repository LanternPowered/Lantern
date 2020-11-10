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
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.text.toPlain
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.EmptyEntityUpdateContext
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListPacket
import org.lanternpowered.server.profile.LanternGameProfile
import org.spongepowered.api.entity.living.player.gamemode.GameModes

class HumanEntityProtocol(entity: LanternEntity) : HumanoidEntityProtocol<LanternEntity>(entity) {

    private var lastName: String? = null

    private val name: String
        get() = this.entity.get(Keys.DISPLAY_NAME).orElseGet { textOf("Human") }.toPlain()

    override fun spawn(context: EntityProtocolUpdateContext) {
        this.spawn(context, this.name)
    }

    private fun spawn(context: EntityProtocolUpdateContext, name: String) {
        val gameProfile = LanternGameProfile(this.entity.uniqueId, name)
        val addEntry = TabListPacket.Entry.Add(gameProfile, GameModes.SURVIVAL.get(), null, 0)
        context.sendToAllExceptSelf { TabListPacket(setOf(addEntry)) }
        super.spawn(context)
        val removeEntry = TabListPacket.Entry.Remove(gameProfile)
        context.sendToAllExceptSelf { TabListPacket(setOf(removeEntry)) }
    }

    override fun update(context: EntityProtocolUpdateContext) {
        val name = this.name
        if (this.lastName != name) {
            this.spawn(context, name)
            super.update(EmptyEntityUpdateContext)
            this.lastName = name
        } else {
            super.update(context)
        }
    }
}
