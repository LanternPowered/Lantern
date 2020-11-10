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
package org.lanternpowered.server.network.vanilla.packet.handler.login

import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.NetworkSession
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.pipeline.PacketCompressionHandler
import org.lanternpowered.server.network.protocol.ProtocolState
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginFinishPacket
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginSuccessPacket
import org.lanternpowered.server.network.vanilla.packet.type.login.SetCompressionPacket

object LoginFinishHandler : PacketHandler<LoginFinishPacket> {

    override fun handle(ctx: NetworkContext, packet: LoginFinishPacket) {
        val gameProfile = packet.gameProfile
        val session = ctx.session
        val compressionThreshold = ctx.server.config.server.networkCompressionThreshold
        if (compressionThreshold != -1) {
            session.sendWithFuture(SetCompressionPacket(compressionThreshold)).addListener {
                ctx.channel.pipeline().replace(NetworkSession.COMPRESSION, NetworkSession.COMPRESSION,
                        PacketCompressionHandler(compressionThreshold))
            }
        } else {
            // Remove the compression handler placeholder
            ctx.channel.pipeline().remove(NetworkSession.COMPRESSION)
        }
        val gameProfileCache = ctx.server.gameProfileManager.cache
        // Store the old profile temporarily
        gameProfileCache.getById(gameProfile.uniqueId).ifPresent { profile ->
            ctx.channel.attr(NetworkSession.PREVIOUS_GAME_PROFILE).set(profile)
        }
        // Cache the new profile
        gameProfileCache.add(gameProfile, true, null)
        session.sendWithFuture(LoginSuccessPacket(gameProfile.uniqueId, gameProfile.name.get()))
                .addListener {
                    session.profile = gameProfile
                    session.protocolState = ProtocolState.Play
                    // TODO: Send custom channel registrations
                    session.initPlayer()
                }
    }
}
