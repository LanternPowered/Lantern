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

public final class MessagePlayOutEntityAnimation implements Message {

    private final int entityId;
    private final int animation;

    public MessagePlayOutEntityAnimation(int entityId, int animation) {
        this.entityId = entityId;
        this.animation = animation;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getAnimation() {
        return this.animation;
    }
}
