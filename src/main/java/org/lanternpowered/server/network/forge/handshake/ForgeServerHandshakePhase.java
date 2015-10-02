package org.lanternpowered.server.network.forge.handshake;

public enum ForgeServerHandshakePhase implements ForgeHandshakePhase {
    START,
    HELLO,
    WAITINGCACK,
    COMPLETE,
    DONE,
    ERROR,
}
