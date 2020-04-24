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
package org.lanternpowered.server.network.vanilla.message.handler.play

import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.message.handler.Handler
import org.lanternpowered.server.network.vanilla.message.type.play.ChangeAdvancementTreeMessage
import org.lanternpowered.server.registry.type.advancement.AdvancementTreeRegistry
import org.spongepowered.api.CatalogKey

class ClientAdvancementTreeHandler : Handler<ChangeAdvancementTreeMessage> {

    override fun handle(context: NetworkContext, message: ChangeAdvancementTreeMessage) {
        if (message is ChangeAdvancementTreeMessage.Open) {
            val id = message.id
            context.session.player.offer(LanternKeys.OPEN_ADVANCEMENT_TREE,
                    AdvancementTreeRegistry.require(CatalogKey.resolve(id)))
        } else {
            // Do we need the close event?
        }
    }
}
