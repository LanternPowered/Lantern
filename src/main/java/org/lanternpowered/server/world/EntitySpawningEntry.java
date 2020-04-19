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
package org.lanternpowered.server.world;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.util.Transform;

import java.util.function.Consumer;

public final class EntitySpawningEntry {

    final EntityType entityType;
    final Transform transform;
    final Consumer<Entity> entityConsumer;

    /**
     * Constructs a new {@link EntitySpawningEntry}.
     *
     * @param entityType The entity type that will be constructed
     * @param transform The transform that should be applied to the entity
     */
    public EntitySpawningEntry(EntityType entityType, Transform transform) {
        this(entityType, transform, entity -> {});
    }

    /**
     * Constructs a new {@link EntitySpawningEntry}.
     *
     * @param entityType The entity type that will be constructed
     * @param transform The transform that should be applied to the entity
     * @param entityConsumer The consumer that can be used to apply properties to the entity
     */
    public EntitySpawningEntry(EntityType entityType, Transform transform, Consumer<Entity> entityConsumer) {
        this.entityConsumer = entityConsumer;
        this.entityType = entityType;
        this.transform = transform;
    }
}
