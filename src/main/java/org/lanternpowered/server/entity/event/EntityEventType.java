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

public enum EntityEventType {
    /**
     * The {@link EntityEvent} will occur when
     * the {@link Entity} is alive.
     */
    ALIVE,
    /**
     * The {@link EntityEvent} will occur when
     * the {@link Entity} is alive or dying.
     */
    DEATH_OR_ALIVE,
}
