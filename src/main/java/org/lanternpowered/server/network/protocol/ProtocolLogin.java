package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.network.message.MessageRegistry;
import org.lanternpowered.server.network.vanilla.message.codec.compression.CodecOutSetCompression;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.codec.login.CodecLoginInEncryptionResponse;
import org.lanternpowered.server.network.vanilla.message.codec.login.CodecLoginOutEncryptionRequest;
import org.lanternpowered.server.network.vanilla.message.codec.login.CodecLoginOutSuccess;
import org.lanternpowered.server.network.vanilla.message.type.compression.MessageOutSetCompression;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInEncryptionResponse;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutEncryptionRequest;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSuccess;

public final class ProtocolLogin extends ProtocolBase {

    public ProtocolLogin() {
        MessageRegistry inbound = this.inbound();
        MessageRegistry outbound = this.outbound();

        // TODO: Add handlers
        inbound.register(0x00, MessageLoginOutSuccess.class, CodecLoginOutSuccess.class);
        inbound.register(0x01, MessageLoginInEncryptionResponse.class, CodecLoginInEncryptionResponse.class);

        outbound.register(0x00, MessageOutDisconnect.class, CodecOutDisconnect.class);
        outbound.register(0x01, MessageLoginOutEncryptionRequest.class, CodecLoginOutEncryptionRequest.class);
        outbound.register(0x02, MessageLoginOutSuccess.class, CodecLoginOutSuccess.class);
        outbound.register(0x03, MessageOutSetCompression.class, CodecOutSetCompression.class);
    }
}
