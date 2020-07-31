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

import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.NetworkSession
import org.lanternpowered.server.network.packet.handler.Handler
import org.lanternpowered.server.network.pipeline.PacketCompressionHandler
import org.lanternpowered.server.network.protocol.ProtocolState
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginFinishPacket
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginSuccessPacket
import org.lanternpowered.server.network.vanilla.packet.type.login.SetCompressionPacket

class LoginFinishHandler : Handler<LoginFinishPacket> {

    override fun handle(context: NetworkContext, packet: LoginFinishPacket) {
        val gameProfile = packet.gameProfile
        val session = context.session
        val compressionThreshold = Lantern.getGame().globalConfig.networkCompressionThreshold
        if (compressionThreshold != -1) {
            session.sendWithFuture(SetCompressionPacket(compressionThreshold)).addListener {
                context.channel.pipeline().replace(NetworkSession.COMPRESSION, NetworkSession.COMPRESSION,
                        PacketCompressionHandler(compressionThreshold))
            }
        } else {
            // Remove the compression handler placeholder
            context.channel.pipeline().remove(NetworkSession.COMPRESSION)
        }
        val gameProfileCache = Lantern.getGame().gameProfileManager.cache
        // Store the old profile temporarily
        gameProfileCache.getById(gameProfile.uniqueId).ifPresent { profile ->
            context.channel.attr(NetworkSession.PREVIOUS_GAME_PROFILE).set(profile)
        }
        // Cache the new profile
        gameProfileCache.add(gameProfile, true, null)
        session.sendWithFuture(LoginSuccessPacket(gameProfile.uniqueId, gameProfile.name.get()))
                .addListener {
                    session.setGameProfile(gameProfile)
                    session.protocolState = ProtocolState.PLAY
                    // TODO: Send custom channel registrations
                    session.initPlayer()
                }
    }
}
