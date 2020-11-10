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
package org.lanternpowered.server.network.vanilla.packet.handler.play

import org.lanternpowered.api.data.Keys
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.entity.event.RefreshAbilitiesPlayerEvent
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientFlyingStatePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityVelocityPacket
import org.spongepowered.api.util.Direction

object ClientFlyingStateHandler : PacketHandler<ClientFlyingStatePacket> {

    override fun handle(ctx: NetworkContext, packet: ClientFlyingStatePacket) {
        val flying = packet.isFlying
        val player = ctx.session.player
        if (!flying || player.get(Keys.CAN_FLY).orElse(false)) {
            player.offer(Keys.IS_FLYING, flying)
        } else {
            // TODO: Just set velocity once it's implemented
            if (player.get(LanternKeys.SUPER_STEVE).orElse(false)) {
                ctx.session.send(EntityVelocityPacket(player.networkId, 0.0, 1.0, 0.0))
                player.offer(Keys.IS_ELYTRA_FLYING, true)
            } else if (player.get(LanternKeys.CAN_WALL_JUMP).orElse(false)) {
                val location = player.location

                // Get the horizontal direction the player is looking
                val direction = player.getHorizontalDirection(Direction.Division.CARDINAL)

                // Get the block location the player may step against
                var stepLocation = location.add(direction.asOffset().mul(0.6, 0.0, 0.6))
                var solidSide = stepLocation.get(direction.opposite, Keys.IS_SOLID).orElse(false)
                // Make sure that the side you step against is solid
                if (solidSide) {
                    // Push the player a bit back in the other direction,
                    // to give a more realistic feeling when pushing off
                    // against a wall
                    val pushBack = direction.asBlockOffset().toDouble().mul(-0.1)
                    // Push the player up
                    ctx.session.send(EntityVelocityPacket(player.networkId, pushBack.x, 0.8, pushBack.z))
                } else {
                    // Now we try if the player can jump away from the wall

                    // Get the block location the player may step against
                    stepLocation = location.add(direction.asOffset().mul(-0.6, 0.0, -0.6))
                    solidSide = stepLocation.get(direction.opposite, Keys.IS_SOLID).orElse(false)
                    if (solidSide) {
                        // Combine the vectors in the direction of the block face
                        // and the direction the player is looking
                        val vector = direction.asBlockOffset().toDouble()
                                .mul(0.25)
                                .mul(1f, 0f, 1f)
                                .add(0.0, 0.65, 0.0)
                                .add(player.getDirectionVector().mul(0.4, 0.25, 0.4))
                                .mul(20.0, 20.0, 20.0) // Per second to per meter // TODO: Adjust previous values to remove this

                        // Push the player forward and up
                        ctx.session.send(EntityVelocityPacket(
                                player.networkId, vector.x, vector.y, vector.z))
                    }
                }
            }
            player.triggerEvent(RefreshAbilitiesPlayerEvent.of())
        }
    }
}
