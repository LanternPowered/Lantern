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

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.packet.handler.Handler;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerLook;
import org.lanternpowered.server.util.rotation.RotationHelper;
import org.spongepowered.math.vector.Vector3d;

public class HandlerPlayInPlayerLook implements Handler<PacketPlayInPlayerLook> {

    @Override
    public void handle(NetworkContext context, PacketPlayInPlayerLook packet) {
        final LanternPlayer player = context.getSession().getPlayer();
        player.setRawRotation(toRotation(packet.getPitch(), packet.getYaw()));
        player.handleOnGroundState(packet.isOnGround());
    }

    static Vector3d toRotation(float yaw, float pitch) {
        return new Vector3d(RotationHelper.wrapDegRotation(yaw), RotationHelper.wrapDegRotation(pitch), 0);
    }
}
