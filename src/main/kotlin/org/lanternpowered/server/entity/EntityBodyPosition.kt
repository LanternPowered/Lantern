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
package org.lanternpowered.server.entity

/**
 * Represents a specific position type of an entity. Will fallback
 * to [CENTER] if the position isn't supported by the entity.
 */
enum class EntityBodyPosition {
    /**
     * The head position.
     */
    HEAD,

    /**
     * The bottom or feet position, this is the
     * lowest possible position of the entity.
     */
    BOTTOM,

    /**
     * The center position (of the collision box)
     * of the entity.
     */
    CENTER
}
