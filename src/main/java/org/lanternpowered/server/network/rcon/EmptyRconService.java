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
package org.lanternpowered.server.network.rcon;

import org.spongepowered.api.service.rcon.RconService;

public final class EmptyRconService implements RconService {

    private final String password;

    public EmptyRconService(String password) {
        this.password = password;
    }

    @Override
    public boolean isRconEnabled() {
        return false;
    }

    @Override
    public String getRconPassword() {
        return this.password;
    }
}
