package org.lanternpowered.server.network.forge.handshake;

public enum ForgeClientHandshakePhase implements ForgeHandshakePhase {
    START,
    HELLO,
    WAITINGSERVERDATA,
    WAITINGSERVERCOMPLETE,
    PENDINGCOMPLETE,
    COMPLETE,
    DONE,
    ERROR,
}
