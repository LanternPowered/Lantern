package org.lanternpowered.server.network.forge.handshake;

public enum ForgeClientHandshakePhase implements ForgeHandshakePhase {
    START,
    HELLO,
    WAITING_SERVER_DATA,
    WAITING_SERVER_COMPLETE,
    PENDING_COMPLETE,
    COMPLETE,
    DONE,
    ERROR,
}
