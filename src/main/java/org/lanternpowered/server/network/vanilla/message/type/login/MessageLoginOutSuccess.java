package org.lanternpowered.server.network.vanilla.message.type.login;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

import org.lanternpowered.server.network.message.Message;

public final class MessageLoginOutSuccess implements Message {

    private final UUID uuid;
    private final String username;

    /**
     * Creates a new login success message.
     * 
     * @param uuid the unique id
     * @param username the username
     */
    public MessageLoginOutSuccess(UUID uuid, String username) {
        this.username = checkNotNull(username, "username");
        this.uuid = checkNotNull(uuid, "uuid");
    }

    /**
     * Gets the unique id of the player.
     * 
     * @return the unique id
     */
    public UUID getUniqueId() {
        return this.uuid;
    }

    /**
     * Gets the username of the player.
     * 
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

}
