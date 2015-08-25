package org.lanternpowered.server.network.vanilla.message.type.handshake;

import org.lanternpowered.server.network.message.MessageAsync;

public class MessageHandshakeIn implements MessageAsync {

    private final String address;

    private final short port;
    private final int protocol;
    private final int state;

    /**
     * Creates a new handshake message.
     * 
     * @param state the next state
     * @param address the address
     * @param port the port
     * @param protocol the client protocol
     */
    public MessageHandshakeIn(int state, String address, short port, int protocol) {
        this.protocol = protocol;
        this.address = address;
        this.state = state;
        this.port = port;
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
     * Gets the server address.
     * 
     * @return the address
     */
    public String getServerAddress() {
        return this.address;
    }

    /**
     * Gets the server port.
     * 
     * @return the port
     */
    public short getServerPort() {
        return this.port;
    }

    /**
     * Gets the protocol version of the client.
     * 
     * @return the version
     */
    public int getProtocolVersion() {
        return this.protocol;
    }

}
