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

public final class MessagePlayOutEntityRelativeMove implements Message {

    private final int entityId;
    private final boolean onGround;
    private final int deltaX;
    private final int deltaY;
    private final int deltaZ;

    public MessagePlayOutEntityRelativeMove(int entityId, int deltaX, int deltaY, int deltaZ, boolean onGround) {
        this.onGround = onGround;
        this.entityId = entityId;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaZ = deltaZ;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public int getDeltaX() {
        return this.deltaX;
    }

    public int getDeltaY() {
        return this.deltaY;
    }

    public int getDeltaZ() {
        return this.deltaZ;
    }
}
