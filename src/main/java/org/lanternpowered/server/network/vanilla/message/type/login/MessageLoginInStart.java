package org.lanternpowered.server.network.vanilla.message.type.login;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

public final class MessageLoginInStart implements Message {

    private final String username;

    /**
     * Creates a new login start message.
     * 
     * @param username the username
     */
    public MessageLoginInStart(String username) {
        this.username = checkNotNull(username, "username");
    }

    /**
     * Gets the username of the player who wants to join.
     * 
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

}
