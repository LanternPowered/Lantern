/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
