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
package org.lanternpowered.server.network.vanilla.message.handler.play;

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSwingArm;

public class HandlerPlayInPlayerSwingArm implements Handler<MessagePlayInPlayerSwingArm> {

    @Override
    public void handle(NetworkContext context, MessagePlayInPlayerSwingArm message) {
        final LanternPlayer player = context.getSession().getPlayer();
        player.resetIdleTimeoutCounter();
        player.resetOpenedSignPosition();
        player.getInteractionHandler().handleSwingArm(message);
    }
}
