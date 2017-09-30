/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.world;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;

import java.util.function.Consumer;

public final class EntitySpawningEntry {

    final EntityType entityType;
    final Transform<World> transform;
    final Consumer<Entity> entityConsumer;

    /**
     * Constructs a new {@link EntitySpawningEntry}.
     *
     * @param entityType The entity type that will be constructed
     * @param transform The transform that should be applied to the entity
     */
    public EntitySpawningEntry(EntityType entityType, Transform<World> transform) {
        this(entityType, transform, entity -> {});
    }

    /**
     * Constructs a new {@link EntitySpawningEntry}.
     *
     * @param entityType The entity type that will be constructed
     * @param transform The transform that should be applied to the entity
     * @param entityConsumer The consumer that can be used to apply properties to the entity
     */
    public EntitySpawningEntry(EntityType entityType, Transform<World> transform, Consumer<Entity> entityConsumer) {
        this.entityConsumer = entityConsumer;
        this.entityType = entityType;
        this.transform = transform;
    }
}
