/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
