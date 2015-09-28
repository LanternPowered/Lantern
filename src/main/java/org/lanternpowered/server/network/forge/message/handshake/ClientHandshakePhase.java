package org.lanternpowered.server.network.forge.message.handshake;

public enum ClientHandshakePhase implements HandshakePhase {
    START,
    HELLO,
    WAITINGSERVERDATA,
    WAITINGSERVERCOMPLETE,
    PENDINGCOMPLETE,
    COMPLETE,
    DONE,
    ERROR,
}
