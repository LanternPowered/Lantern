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

import org.lanternpowered.server.block.BlockProperties;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.event.RefreshAbilitiesPlayerEvent;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.packet.handler.Handler;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientFlyingStatePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityVelocity;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.math.vector.Vector3d;

public class HandlerPlayInPlayerAbilities implements Handler<ClientFlyingStatePacket> {

    @Override
    public void handle(NetworkContext context, ClientFlyingStatePacket packet) {
        final boolean flying = packet.isFlying();
        final LanternPlayer player = context.getSession().getPlayer();
        if (!flying || player.get(Keys.CAN_FLY).orElse(false)) {
            player.offer(Keys.IS_FLYING, flying);
        } else {
            // TODO: Just set velocity once it's implemented
            if (player.get(LanternKeys.SUPER_STEVE).orElse(false)) {
                context.getSession().send(new PacketPlayOutEntityVelocity(player.getNetworkId(), 0, 1.0, 0));
                player.offer(Keys.IS_ELYTRA_FLYING, true);
            } else if (player.get(LanternKeys.CAN_WALL_JUMP).orElse(false)) {
                final Location location = player.getLocation();

                // Get the horizontal direction the player is looking
                final Direction direction = player.getHorizontalDirection(Direction.Division.CARDINAL);

                // Get the block location the player may step against
                final Location location1 = location.add(direction.asOffset().mul(0.6, 0, 0.6));

                boolean solidSide = location1.getWorld().getProperty(
                        location1.getBlockPosition(), direction.getOpposite(), BlockProperties.IS_SOLID_SIDE).orElse(false);
                // Make sure that the side you step against is solid
                if (solidSide) {
                    // Push the player a bit back in the other direction,
                    // to give a more realistic feeling when pushing off
                    // against a wall
                    final Vector3d pushBack = direction.asBlockOffset().toDouble().mul(-0.1);
                    // Push the player up
                    context.getSession().send(new PacketPlayOutEntityVelocity(player.getNetworkId(), pushBack.getX(), 0.8, pushBack.getZ()));
                } else {
                    // Now we try if the player can jump away from the wall

                    // Get the block location the player may step against
                    final Location location2 = location.add(direction.asOffset().mul(-0.6, 0, -0.6));

                    solidSide = location2.getWorld().getProperty(
                            location2.getBlockPosition(), direction, BlockProperties.IS_SOLID_SIDE).orElse(false);

                    if (solidSide) {
                        // Combine the vectors in the direction of the block face
                        // and the direction the player is looking
                        final Vector3d vector = direction.asBlockOffset().toDouble()
                                .mul(0.25).mul(1, 0, 1).add(0, 0.65, 0).add(player.getDirectionVector().mul(0.4, 0.25, 0.4));

                        // Push the player forward and up
                        context.getSession().send(new PacketPlayOutEntityVelocity(
                                player.getNetworkId(), vector.getX(), vector.getY(), vector.getZ()));
                    }
                }
            }
            player.triggerEvent(RefreshAbilitiesPlayerEvent.of());
        }
    }
}
