/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
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

import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.profile.LanternGameProfile.Property;

import com.google.common.collect.ImmutableList;

public final class MessageHandshakeIn implements Message {

    private final SocketAddress address;
    private final String hostname;

    @Nullable
    private final ProxyData proxyData;

    private final int protocol;
    private final int state;

    private final boolean fmlMarker;

    /**
     * Creates a new handshake message.
     * 
     * @param state the next state
     * @param address the address
     * @param port the port
     * @param protocol the client protocol
     */
    public MessageHandshakeIn(int state, String hostname, SocketAddress address,
            int protocol, @Nullable ProxyData proxyData, boolean fmlMarker) {
        this.fmlMarker = fmlMarker;
        this.proxyData = proxyData;
        this.hostname = hostname;
        this.protocol = protocol;
        this.address = address;
        this.state = state;
    }

    /**
     * Whether the handshake contains the fml marker.
     * 
     * @return has fml marker
     */
    public boolean hasFMLMarker() {
        return this.fmlMarker;
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
     * Gets the socket address that was used to join the server.
     * 
     * @return the socket address
     */
    public SocketAddress getAddress() {
        return this.address;
    }

    /**
     * Gets the protocol version of the client.
     * 
     * @return the version
     */
    public int getProtocolVersion() {
        return this.protocol;
    }

    public ProxyData getProxyData() {
        return this.proxyData;
    }

    public static class ProxyData {

        private final UUID uniqueId;
        private final List<Property> properties;

        public ProxyData(UUID uniqueId, List<Property> properties) {
            this.properties = ImmutableList.copyOf(properties);
            this.uniqueId = uniqueId;
        }

        public UUID getUniqueId() {
            return this.uniqueId;
        }

        public List<Property> getProperties() {
            return this.properties;
        }
    }
}
