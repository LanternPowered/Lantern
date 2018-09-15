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
@file:Suppress("FunctionName")

package org.lanternpowered.api.entity.spawn

import org.lanternpowered.api.behavior.Behavior
import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.world.World
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.entity.Transform
import org.spongepowered.api.event.entity.ConstructEntityEvent
import org.spongepowered.api.event.entity.SpawnEntityEvent
import java.util.Collections
import java.util.function.Consumer

/**
 * A helper class to (pre) spawn entities in bulk that
 * aren't attached to a specific world.
 */
interface EntitySpawner {

    /**
     * Performs [ConstructEntityEvent]s and returns the constructed [Entity], if constructed.
     */
    fun preSpawn(entityType: EntityType, transform: Transform<World>, fn: Consumer<Entity>)
            = preSpawn(entityType, transform, fn::accept).optional()

    /**
     * Performs [ConstructEntityEvent]s and returns the constructed [Entity], if constructed.
     */
    fun preSpawn(entityType: EntityType, transform: Transform<World>, fn: (Entity) -> Unit = {}): Entity?
            = preSpawn(Collections.singleton(EntitySpawnEntry(entityType, transform, fn))).firstOrNull()

    /**
     * Performs [ConstructEntityEvent]s and returns the constructed [Entity]s.
     *
     * Note that not all the entries may result in a constructed [Entity].
     */
    fun preSpawn(entries: Iterable<EntitySpawnEntry>): MutableList<Entity>

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent].
     */
    fun spawn(entries: Iterable<EntitySpawnEntry>): MutableList<Entity>
            = spawn(entries, SpawnEventProvider(LanternEventFactory::createSpawnEntityEvent))

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent].
     */
    fun spawn(entries: Iterable<EntitySpawnEntry>, eventProvider: SpawnEventProvider): MutableList<Entity>

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun spawn(entityType: EntityType, transform: Transform<World>)
            = spawn(entityType, transform, SpawnEventProvider(LanternEventFactory::createSpawnEntityEvent)) {}.optional()

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun spawn(entityType: EntityType, transform: Transform<World>, fn: Consumer<Entity>)
            = spawn(entityType, transform, SpawnEventProvider(LanternEventFactory::createSpawnEntityEvent), fn::accept).optional()

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun spawn(entityType: EntityType, transform: Transform<World>, eventProvider: SpawnEventProvider)
            = spawn(entityType, transform, eventProvider) {}.optional()

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun spawn(entityType: EntityType, transform: Transform<World>, eventProvider: SpawnEventProvider, fn: Consumer<Entity>)
            = spawn(entityType, transform, eventProvider, fn::accept).optional()

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun spawn(entityType: EntityType, transform: Transform<World>, eventProvider: SpawnEventProvider, fn: (Entity) -> Unit = {})
            = spawn(singletonListOf(EntitySpawnEntry(entityType, transform, fn)), eventProvider).firstOrNull()

    /**
     * Finishes the [SpawnEntityEvent] by moving all the [Entity]s
     * in the event to the [World] they are supposed to go.
     */
    fun finishSpawnEvent(event: SpawnEntityEvent)
}

/**
 * A convenient constructor to allow to construct the [Behavior]
 * through a SAM like conversion.
 */
inline fun SpawnEventProvider(crossinline fn: (Cause, MutableList<Entity>) -> SpawnEntityEvent) = object : SpawnEventProvider {
    override fun invoke(cause: Cause, entities: MutableList<Entity>) = fn(cause, entities)
}

@FunctionalInterface
interface SpawnEventProvider {

    operator fun invoke(cause: Cause, entities: MutableList<Entity>): SpawnEntityEvent
}
