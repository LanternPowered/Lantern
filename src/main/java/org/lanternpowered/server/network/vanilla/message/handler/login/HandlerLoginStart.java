package org.lanternpowered.server.network.vanilla.message.handler.login;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.lanternpowered.server.game.LanternGameProfile;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInStart;
import org.lanternpowered.server.network.message.Async;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.handshake.MessageHandshakeIn.ProxyData;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInStart;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutEncryptionRequest;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSuccess;
import org.lanternpowered.server.util.SecurityHelper;

@Async
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
            LanternGameProfile profile;
            if (proxy == null) {
                UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
                profile = new LanternGameProfile(uuid, username);
            } else {
                profile = new LanternGameProfile(proxy.getUniqueId(), username, proxy.getProperties());
            }
            session.setPlayer(profile);
            session.send(new MessageLoginOutSuccess(profile.getUniqueId(), username));
            session.setProtocolState(ProtocolState.FORGE_HANDSHAKE);
            session.messageReceived(new MessageForgeHandshakeInStart());
        }
    }
}
