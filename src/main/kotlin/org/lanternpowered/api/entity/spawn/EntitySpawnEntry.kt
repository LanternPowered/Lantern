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
package org.lanternpowered.api.entity.spawn

import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.entity.Transform
import org.spongepowered.api.world.World
import java.util.function.Consumer

/**
 * Represents a spawning entry that can be spawned through the [EntitySpawner].
 *
 * @param entityType The entity type that will be constructed
 * @param transform The transform that should be applied to the entity
 * @param entityPopulator The populator that can be used to apply properties to the entity
 */
class EntitySpawnEntry @JvmOverloads constructor(
        internal val entityType: EntityType,
        internal val transform: Transform<World>,
        internal val entityPopulator: Entity.() -> Unit = {}
) {

    constructor(
            entityType: EntityType,
            transform: Transform<World>,
            entityConsumer: Consumer<Entity>
    ) : this(entityType, transform, entityConsumer::accept)
}
