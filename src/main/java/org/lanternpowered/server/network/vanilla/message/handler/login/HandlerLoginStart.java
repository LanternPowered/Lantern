package org.lanternpowered.server.network.vanilla.message.handler.login;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.lanternpowered.server.game.LanternGameProfile;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.handshake.MessageHandshakeIn.ProxyData;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInStart;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutEncryptionRequest;
import org.lanternpowered.server.util.SecurityHelper;

public final class HandlerLoginStart implements Handler<MessageLoginInStart> {

    @Override
    public void handle(Session session, MessageLoginInStart message) {
        String username = message.getUsername();

        if (session.getServer().getOnlineMode()) {
            byte[] publicKey = SecurityHelper.generateX509Key(session.getServer().getKeyPair()
                    .getPublic()).getEncoded(); // Convert to X509 format
            byte[] verifyToken = SecurityHelper.generateVerifyToken();

            // Set verify data on session for use in the response handler
            session.setVerifyToken(verifyToken);
            session.setVerifyUsername(username);

            // Send created request message and wait for the response
            session.send(new MessageLoginOutEncryptionRequest(publicKey, verifyToken));
        } else {
            ProxyData proxy = session.getProxyData();
            if (proxy == null) {
                UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
                session.setPlayer(new LanternGameProfile(uuid, username));
            } else {
                session.setPlayer(new LanternGameProfile(proxy.getUniqueId(), username, proxy.getProperties()));
            }
        }
    }
}
