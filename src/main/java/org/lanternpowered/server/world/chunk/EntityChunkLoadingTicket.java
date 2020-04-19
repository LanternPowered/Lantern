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

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.chunk.ChunkTicketManager;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface EntityChunkLoadingTicket extends ChunkLoadingTicket, ChunkTicketManager.EntityLoadingTicket {

    void setEntity(@Nullable Entity entity);

    Optional<Entity> getEntity();

    void setEntityReference(@Nullable EntityReference entityReference);

    Optional<EntityReference> getEntityReference();

    /**
     * Gets the latest version of the {@link EntityReference} of the bound {@link Entity}
     * or uses the default one.
     *
     * @return the entity reference
     */
    Optional<EntityReference> getOrCreateEntityReference();
}
