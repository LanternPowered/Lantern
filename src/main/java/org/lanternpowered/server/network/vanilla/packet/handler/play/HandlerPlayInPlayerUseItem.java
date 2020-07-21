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
package org.lanternpowered.server.network.vanilla.packet.handler.play;

import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerUseItem;

public class HandlerPlayInPlayerUseItem implements Handler<PacketPlayInPlayerUseItem> {

    @Override
    public void handle(NetworkContext context, PacketPlayInPlayerUseItem packet) {
        context.getSession().getPlayer().getInteractionHandler().handleItemInteraction(packet);
    }
}
