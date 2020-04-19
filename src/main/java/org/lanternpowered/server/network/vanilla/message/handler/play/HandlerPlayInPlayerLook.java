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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerLook;
import org.lanternpowered.server.util.rotation.RotationHelper;
import org.spongepowered.math.vector.Vector3d;

public class HandlerPlayInPlayerLook implements Handler<MessagePlayInPlayerLook> {

    @Override
    public void handle(NetworkContext context, MessagePlayInPlayerLook message) {
        final LanternPlayer player = context.getSession().getPlayer();
        player.setRawRotation(toRotation(message.getPitch(), message.getYaw()));
        player.handleOnGroundState(message.isOnGround());
    }

    static Vector3d toRotation(float yaw, float pitch) {
        return new Vector3d(RotationHelper.wrapDegRotation(yaw), RotationHelper.wrapDegRotation(pitch), 0);
    }
}
