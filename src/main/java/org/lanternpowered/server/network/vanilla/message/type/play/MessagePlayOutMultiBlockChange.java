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

import java.util.Collection;

public final class MessagePlayOutMultiBlockChange implements Message {

    private final int chunkX;
    private final int chunkZ;

    private final Collection<MessagePlayOutBlockChange> changes;

    public MessagePlayOutMultiBlockChange(int chunkX, int chunkZ, Collection<MessagePlayOutBlockChange> changes) {
        this.changes = changes;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public Collection<MessagePlayOutBlockChange> getChanges() {
        return this.changes;
    }
}
