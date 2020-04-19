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
    LOGIN               (2, new ProtocolLogin());

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
