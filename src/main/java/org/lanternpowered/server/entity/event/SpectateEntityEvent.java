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
package org.lanternpowered.server.entity.event;

import org.spongepowered.api.entity.Entity;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class SpectateEntityEvent implements EntityEvent {

    @Nullable private final Entity spectatedEntity;

    public SpectateEntityEvent(@Nullable Entity spectatedEntity) {
        this.spectatedEntity = spectatedEntity;
    }

    public Optional<Entity> getSpectatedEntity() {
        return Optional.ofNullable(this.spectatedEntity);
    }

    @Override
    public EntityEventType type() {
        return EntityEventType.ALIVE;
    }
}
