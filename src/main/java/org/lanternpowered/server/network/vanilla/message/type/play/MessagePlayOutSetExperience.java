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

public final class MessagePlayOutSetExperience implements Message {

    private final float exp;
    private final int level;
    private final int total;

    public MessagePlayOutSetExperience(float exp, int level, int total) {
        this.level = level;
        this.total = total;
        this.exp = exp;
    }

    public int getLevel() {
        return this.level;
    }

    public float getExp() {
        return this.exp;
    }

    public int getTotal() {
        return this.total;
    }
}
