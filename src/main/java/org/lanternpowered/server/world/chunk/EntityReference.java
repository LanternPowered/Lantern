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
package org.lanternpowered.server.world.chunk;

import org.spongepowered.math.vector.Vector2i;

import java.util.UUID;

/**
 * A reference where the entity is stored in the world,
 * if it's not already loaded.
 */
public final class EntityReference {

    private final Vector2i chunkCoords;
    private final UUID uniqueId;

    public EntityReference(Vector2i chunkCoords, UUID uniqueId) {
        this.chunkCoords = chunkCoords;
        this.uniqueId = uniqueId;
    }

    public Vector2i getChunkCoords() {
        return this.chunkCoords;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }
}
