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
