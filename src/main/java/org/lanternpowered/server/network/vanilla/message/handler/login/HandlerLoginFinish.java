/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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

import static org.lanternpowered.server.network.session.Session.COMPRESSION;

import org.lanternpowered.server.config.user.ban.BanConfig;
import org.lanternpowered.server.config.user.ban.BanEntry;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInStart;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.pipeline.MessageCompressionHandler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInFinish;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSetCompression;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSuccess;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ban.Ban;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public final class HandlerLoginFinish implements Handler<MessageLoginInFinish> {

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' HH:mm:ss z");

    @Override
    public void handle(NetworkContext context, MessageLoginInFinish message) {
        final LanternGameProfile gameProfile = message.getGameProfile();
        final Session session = context.getSession();
        final BanConfig banConfig = Lantern.getGame().getBanConfig();
        // Check whether the player is banned and kick if necessary
        Optional<BanEntry> optBanEntry = banConfig.getEntryByProfile(gameProfile);
        if (!optBanEntry.isPresent()) {
            SocketAddress address = context.getChannel().remoteAddress();
            if (address instanceof InetSocketAddress) {
                optBanEntry = banConfig.getEntryByIp(((InetSocketAddress) address).getAddress());
            }
        }
        if (optBanEntry.isPresent()) {
            BanEntry banEntry = optBanEntry.get();
            Optional<Instant> optExpirationDate = banEntry.getExpirationDate();
            Optional<Text> optReason = banEntry.getReason();

            // Generate the kick message
            Text.Builder builder = Text.builder();
            if (banEntry instanceof Ban.Profile) {
                builder.append(Text.of("You are banned from this server!"));
            } else {
                builder.append(Text.of("Your IP address is banned from this server!"));
            }
            // There is optionally a reason
            optReason.ifPresent(reason -> builder.append(Text.of("\nReason: ", reason)));
            // And a expiration date if present
            optExpirationDate.ifPresent(expirationDate ->
                    builder.append(Text.of("\nYour ban will be removed on ", this.timeFormatter.format(expirationDate))));

            session.disconnect(builder.build());
            return;
        }
        // Check for whitelist
        if (Lantern.getGame().getGlobalConfig().isWhitelistEnabled() && !Lantern.getGame().getWhitelistConfig().isWhitelisted(gameProfile)
                && !Lantern.getGame().getOpsConfig().getEntryByProfile(gameProfile).isPresent()) {
            session.disconnect(Text.of("You are not white-listed on this server!"));
            return;
        }
        int compressionThreshold = Lantern.getGame().getGlobalConfig().getNetworkCompressionThreshold();
        if (compressionThreshold != -1) {
            session.send(new MessageLoginOutSetCompression(compressionThreshold)).addListener(future ->
                    context.getChannel().pipeline().replace(COMPRESSION, COMPRESSION, new MessageCompressionHandler(compressionThreshold)));
        }
        session.send(new MessageLoginOutSuccess(gameProfile.getUniqueId(), gameProfile.getName().get()))
                .addListener(future -> {
                    session.setPlayer(gameProfile);
                    session.setProtocolState(ProtocolState.FORGE_HANDSHAKE);
                    session.messageReceived(new MessageForgeHandshakeInStart());
                });
    }
}
