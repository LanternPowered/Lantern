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
package org.lanternpowered.server.config.user;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.lanternpowered.server.profile.LanternGameProfile;

@ConfigSerializable
public class UserEntry {

    @Setting(value = "profile")
    private LanternGameProfile gameProfile;

    protected UserEntry() {
    }

    /**
     * Creates a new user entry for the specified profile.
     * 
     * @param gameProfile the game profile
     */
    public UserEntry(LanternGameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }

    /**
     * Gets the game profile.
     * 
     * @return the profile
     */
    public LanternGameProfile getProfile() {
        return this.gameProfile;
    }

}
