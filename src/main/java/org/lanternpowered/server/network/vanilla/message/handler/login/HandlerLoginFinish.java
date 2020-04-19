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
package org.lanternpowered.server.network.vanilla.message.handler.login;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.pipeline.MessageCompressionHandler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInFinish;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSetCompression;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSuccess;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfileCache;

import java.util.Set;
import java.util.stream.Collectors;

public final class HandlerLoginFinish implements Handler<MessageLoginInFinish> {

    @Override
    public void handle(NetworkContext context, MessageLoginInFinish message) {
        final LanternGameProfile gameProfile = message.getGameProfile();
        final NetworkSession session = context.getSession();
        int compressionThreshold = Lantern.getGame().getGlobalConfig().getNetworkCompressionThreshold();
        if (compressionThreshold != -1) {
            session.sendWithFuture(new MessageLoginOutSetCompression(compressionThreshold)).addListener(future ->
                    context.getChannel().pipeline().replace(NetworkSession.COMPRESSION, NetworkSession.COMPRESSION,
                            new MessageCompressionHandler(compressionThreshold)));
        } else {
            // Remove the compression handler placeholder
            context.getChannel().pipeline().remove(NetworkSession.COMPRESSION);
        }
        final GameProfileCache gameProfileCache = Lantern.getGame().getGameProfileManager().getCache();
        // Store the old profile temporarily
        gameProfileCache.getById(gameProfile.getUniqueId()).ifPresent(
                profile -> context.getChannel().attr(NetworkSession.PREVIOUS_GAME_PROFILE).set(profile));
        // Cache the new profile
        gameProfileCache.add(gameProfile, true, null);
        session.sendWithFuture(new MessageLoginOutSuccess(gameProfile.getUniqueId(), gameProfile.getName().get()))
                .addListener(future -> {
                    session.setGameProfile(gameProfile);
                    session.setProtocolState(ProtocolState.PLAY);
                    final Set<String> channels = Sponge.getChannelRegistrar().getRegisteredChannels(Platform.Type.SERVER)
                            .stream().map(CatalogKey::toString).collect(Collectors.toSet());
                    if (!channels.isEmpty()) {
                        session.send(new MessagePlayInOutRegisterChannels(channels));
                    }
                    session.initPlayer();
                });
    }
}
