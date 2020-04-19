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
public final class OpsEntry extends UserEntry {

    @Setting(value = "level")
    private int opLevel;

    protected OpsEntry() {
    }

    /**
     * Creates a new user entry for the specified profile.
     * 
     * @param gameProfile the game profile
     * @param opLevel the op level
     */
    public OpsEntry(LanternGameProfile gameProfile, int opLevel) {
        super(gameProfile);
        this.opLevel = opLevel;
    }

    /**
     * Gets the op level.
     * 
     * @return the op level
     */
    public int getOpLevel() {
        return this.opLevel;
    }

}
