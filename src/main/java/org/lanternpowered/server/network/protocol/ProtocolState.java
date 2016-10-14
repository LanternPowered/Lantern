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
package org.lanternpowered.server.network.protocol;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Optional;

public enum ProtocolState {
    /**
     * The handshake state (initial).
     */
    HANDSHAKE           (-1, new ProtocolHandshake()),
    /**
     * The normal play state.
     */
    PLAY                (0, new ProtocolPlay()),
    /**
     * The status (or ping) state.
     */
    STATUS              (1, new ProtocolStatus()),
    /**
     * The login state.
     */
    LOGIN               (2, new ProtocolLogin()),
    /**
     * The handshake phase of forge. This may be skipped and
     * is only activated by the server.
     */
    FORGE_HANDSHAKE     (999, new ProtocolForgeHandshake());

    private final static Int2ObjectMap<ProtocolState> lookup = new Int2ObjectOpenHashMap<>();

    private final int id;
    private final Protocol protocol;

    ProtocolState(int id, Protocol protocol) {
        this.protocol = protocol;
        this.id = id;
    }

    /**
     * Gets the id of the protocol state.
     * 
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the protocol that is attached to the protocol state. (Message
     * registry.)
     * 
     * @return the protocol
     */
    public Protocol getProtocol() {
        return this.protocol;
    }

    /**
     * Gets the protocol state using it's id.
     * 
     * @param id the id
     * @return the protocol state
     */
    public static Optional<ProtocolState> getFromId(int id) {
        return Optional.ofNullable(lookup.get(id));
    }

    static {
        for (ProtocolState state : values()) {
            lookup.put(state.id, state);
        }
    }

    public static void init() {
    }
}
