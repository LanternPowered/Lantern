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
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayOutWorldSky implements Message {

    private float rain;
    private float darkness;

    /**
     * Creates a new world sky message.
     * 
     * @param rain the rain strength
     * @param darkness the darkness
     */
    public MessagePlayOutWorldSky(float rain, float darkness) {
        this.darkness = darkness;
        this.rain = rain;
    }

    /**
     * Gets the rain strength.
     * 
     * @return The rain strength
     */
    public float getRainStrength() {
        return this.rain;
    }

    /**
     * Gets the darkness.
     * 
     * @return The darkness
     */
    public float getDarkness() {
        return this.darkness;
    }

}
