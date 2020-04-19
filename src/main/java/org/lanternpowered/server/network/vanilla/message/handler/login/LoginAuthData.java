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
package org.lanternpowered.server.network.vanilla.message.handler.login;

class LoginAuthData {

    private final String username;
    private final String sessionId;
    private final byte[] verifyToken;

    public LoginAuthData(String username, String sessionId, byte[] verifyToken) {
        this.verifyToken = verifyToken;
        this.sessionId = sessionId;
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public byte[] getVerifyToken() {
        return this.verifyToken;
    }
}
