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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.entity.spawn

import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.util.Transform
import java.util.function.Consumer

/**
 * Represents a spawning entry that can be spawned through the [EntitySpawner].
 *
 * @param entityType The entity type that will be constructed
 * @param transform The transform that should be applied to the entity
 * @param populator The populator that can be used to apply properties to the entity
 */
class EntitySpawnEntry<T : Entity> @JvmOverloads constructor(
        val entityType: EntityType<T>,
        val transform: Transform,
        val populator: T.() -> Unit = {}
) {

    constructor(
            entityType: EntityType<T>,
            transform: Transform,
            entityConsumer: Consumer<T>
    ) : this(entityType, transform, entityConsumer::accept)
}
