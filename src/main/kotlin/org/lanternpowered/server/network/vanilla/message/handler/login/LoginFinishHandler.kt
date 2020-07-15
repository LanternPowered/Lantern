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
package org.lanternpowered.server.network.vanilla.message.handler.login

import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.NetworkSession
import org.lanternpowered.server.network.message.handler.Handler
import org.lanternpowered.server.network.pipeline.MessageCompressionHandler
import org.lanternpowered.server.network.protocol.ProtocolState
import org.lanternpowered.server.network.vanilla.message.type.login.LoginFinishMessage
import org.lanternpowered.server.network.vanilla.message.type.login.LoginSuccessMessage
import org.lanternpowered.server.network.vanilla.message.type.login.SetCompressionMessage
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.Platform
import org.spongepowered.api.Sponge
import java.util.stream.Collectors

class LoginFinishHandler : Handler<LoginFinishMessage> {

    override fun handle(context: NetworkContext, message: LoginFinishMessage) {
        val gameProfile = message.gameProfile
        val session = context.session
        val compressionThreshold = Lantern.getGame().globalConfig.networkCompressionThreshold
        if (compressionThreshold != -1) {
            session.sendWithFuture(SetCompressionMessage(compressionThreshold)).addListener {
                context.channel.pipeline().replace(NetworkSession.COMPRESSION, NetworkSession.COMPRESSION,
                        MessageCompressionHandler(compressionThreshold))
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
        session.sendWithFuture(LoginSuccessMessage(gameProfile.uniqueId, gameProfile.name.get()))
                .addListener {
                    session.setGameProfile(gameProfile)
                    session.protocolState = ProtocolState.PLAY
                    val channels = Sponge.getChannelRegistrar().getRegisteredChannels(Platform.Type.SERVER)
                            .stream().map { obj: ResourceKey -> obj.toString() }.collect(Collectors.toSet())
                    if (channels.isNotEmpty())
                        session.send(MessagePlayInOutRegisterChannels(channels))
                    session.initPlayer()
                }
    }
}
