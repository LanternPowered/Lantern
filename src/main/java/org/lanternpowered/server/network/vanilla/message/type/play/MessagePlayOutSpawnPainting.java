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
import org.spongepowered.api.data.type.ArtType;
import org.spongepowered.api.util.Direction;

import java.util.UUID;

public final class MessagePlayOutSpawnPainting implements Message {

    private final int entityId;
    private final UUID uniqueId;
    private final ArtType art;
    private final Direction direction;
    private final int x;
    private final int y;
    private final int z;

    public MessagePlayOutSpawnPainting(int entityId, UUID uniqueId, ArtType art, int x, int y, int z, Direction direction) {
        this.entityId = entityId;
        this.uniqueId = uniqueId;
        this.direction = direction;
        this.art = art;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public ArtType getArt() {
        return this.art;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }
}
