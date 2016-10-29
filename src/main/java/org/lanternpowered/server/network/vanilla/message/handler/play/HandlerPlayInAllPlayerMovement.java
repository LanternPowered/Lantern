/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.message.handler.play;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerMovement;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerMovementAndLook;

public final class HandlerPlayInAllPlayerMovement {

    public class HandlerPlayInPlayerMovement implements Handler<MessagePlayInPlayerMovement> {

        @Override
        public void handle(NetworkContext context, MessagePlayInPlayerMovement message) {
            final LanternPlayer player = context.getSession().getPlayer();
            player.setRawPosition(new Vector3d(message.getX(), message.getY(), message.getZ()));
        }
    }

    public class HandlerPlayInPlayerMovementAndLook implements Handler<MessagePlayInPlayerMovementAndLook> {

        @Override
        public void handle(NetworkContext context, MessagePlayInPlayerMovementAndLook message) {
            final LanternPlayer player = context.getSession().getPlayer();
            player.setRawPosition(new Vector3d(message.getX(), message.getY(), message.getZ()));
            player.setRawRotation(toRotation(message.getPitch(), message.getYaw()));
        }
    }

    public class HandlerPlayInPlayerLook implements Handler<MessagePlayInPlayerLook> {

        @Override
        public void handle(NetworkContext context, MessagePlayInPlayerLook message) {
            final LanternPlayer player = context.getSession().getPlayer();
            player.setRawRotation(toRotation(message.getPitch(), message.getYaw()));
        }
    }

    private Vector3d toRotation(float yaw, float pitch) {
        yaw %= 360.0f;
        pitch %= 360.0f;
        if (yaw < 180.0f) {
            yaw += 360.0f;
        } else if (yaw > 180.0f) {
            yaw -= 360.0f;
        }
        if (pitch < 0) {
            pitch += 360.0f;
        }
        return new Vector3d(yaw, pitch, 0);
    }
}
