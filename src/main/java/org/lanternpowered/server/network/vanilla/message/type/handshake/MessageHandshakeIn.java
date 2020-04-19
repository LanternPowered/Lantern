/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.network.vanilla.message.type.handshake;

import org.lanternpowered.server.network.message.Message;

public final class MessageHandshakeIn implements Message {

    private final String hostname;
    private final int port;

    private final int protocol;
    private final int state;

    /**
     * Creates a new handshake message.
     * 
     * @param state the next state
     * @param hostname the hostname
     * @param protocol the client protocol
     */
    public MessageHandshakeIn(int state, String hostname, int port, int protocol) {
        this.hostname = hostname;
        this.protocol = protocol;
        this.port = port;
        this.state = state;
    }

    /**
     * Gets the next protocol state.
     * 
     * @return the state
     */
    public int getNextState() {
        return this.state;
    }

    /**
     * Gets the host name that was used to join the server.
     * 
     * @return the host name
     */
    public String getHostname() {
        return this.hostname;
    }

    /**
     * Gets the protocol version of the client.
     * 
     * @return the version
     */
    public int getProtocolVersion() {
        return this.protocol;
    }

    public int getPort() {
        return this.port;
    }
}
