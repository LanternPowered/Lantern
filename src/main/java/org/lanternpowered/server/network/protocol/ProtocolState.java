package org.lanternpowered.server.network.protocol;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public enum ProtocolState {
    /**
     * The handshake state (initial).
     */
    HANDSHAKE   (-1, new ProtocolHandshake()),
    /**
     * The normal play state.
     */
    PLAY        (0, new ProtocolPlay()),
    /**
     * The status (or ping) state.
     */
    STATUS      (1, new ProtocolStatus()),
    /**
     * The login state.
     */
    LOGIN       (2, new ProtocolLogin());

    private final static TIntObjectMap<ProtocolState> LOOKUP = new TIntObjectHashMap<ProtocolState>();

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
    public static ProtocolState fromId(int id) {
        return LOOKUP.get(id);
    }

    static {
        for (ProtocolState state : values()) {
            LOOKUP.put(state.id, state);
        }
    }
}
