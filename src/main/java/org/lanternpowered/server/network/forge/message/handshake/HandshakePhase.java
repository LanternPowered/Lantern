package org.lanternpowered.server.network.forge.message.handshake;

public enum HandshakePhase {
    START,
    HELLO,
    WAITINGCACK,
    COMPLETE,
    DONE,
    ERROR,
}
