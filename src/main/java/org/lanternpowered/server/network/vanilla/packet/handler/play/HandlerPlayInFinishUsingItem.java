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
import org.lanternpowered.server.network.packet.handler.Handler;
import org.lanternpowered.server.network.vanilla.packet.type.play.FinishUsingItemPacket;

public class HandlerPlayInFinishUsingItem implements Handler<FinishUsingItemPacket> {

    @Override
    public void handle(NetworkContext context, FinishUsingItemPacket packet) {
        context.getSession().getPlayer().getInteractionHandler().handleFinishItemInteraction(packet);
    }
}