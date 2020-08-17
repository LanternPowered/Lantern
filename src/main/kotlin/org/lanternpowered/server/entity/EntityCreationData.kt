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

import org.spongepowered.api.entity.EntityType
import java.util.UUID

/**
 * Represents the information that is used to construct
 * an entity instance.
 */
data class EntityCreationData(
        val uniqueId: UUID,
        val type: EntityType<*>
)
