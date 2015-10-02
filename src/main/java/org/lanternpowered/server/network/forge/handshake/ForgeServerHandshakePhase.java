package org.lanternpowered.server.network.forge.handshake;

public enum ForgeServerHandshakePhase implements ForgeHandshakePhase {
    START,
    HELLO,
    WAITING_ACK,
    COMPLETE,
    DONE,
    ERROR,
}
