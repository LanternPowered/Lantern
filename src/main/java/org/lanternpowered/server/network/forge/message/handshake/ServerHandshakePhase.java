package org.lanternpowered.server.network.forge.message.handshake;

public enum ServerHandshakePhase implements HandshakePhase {
    START,
    HELLO,
    WAITINGCACK,
    COMPLETE,
    DONE,
    ERROR,
}
