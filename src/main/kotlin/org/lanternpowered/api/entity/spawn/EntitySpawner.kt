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
package org.lanternpowered.api.entity.spawn

import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.world.ExtendedWorld
import org.lanternpowered.api.world.World
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.event.entity.ConstructEntityEvent
import org.spongepowered.api.event.entity.SpawnEntityEvent
import org.spongepowered.api.util.Transform
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * A spawner class that handles spawning behavior of
 * entities in a specific [World].
 */
interface EntitySpawner {

    /**
     * The world this spawner is attached to.
     */
    val world: ExtendedWorld

    /**
     * Performs [ConstructEntityEvent]s and returns the constructed [Entity], if constructed.
     */
    fun <T : Entity> preSpawn(entityType: EntityType<T>, transform: Transform, fn: Consumer<T>): T? =
            this.preSpawn(entityType, transform, fn::accept)

    /**
     * Performs [ConstructEntityEvent]s and returns the constructed [Entity], if constructed.
     */
    fun <T : Entity> preSpawn(entityType: EntityType<T>, transform: Transform, fn: (T) -> Unit = {}): T?

    /**
     * Performs [ConstructEntityEvent]s and returns the constructed [Entity]s.
     *
     * Note that not all the entries may result in a constructed [Entity].
     */
    fun preSpawn(entries: Iterable<EntitySpawnEntry<*>>): MutableList<Entity>

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent].
     */
    fun spawn(entries: Iterable<EntitySpawnEntry<*>>): MutableList<Entity> =
            this.spawn(entries, SpawnEventProvider(LanternEventFactory::createSpawnEntityEvent))

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent].
     */
    fun spawn(entries: Iterable<EntitySpawnEntry<*>>, eventProvider: SpawnEventProvider): MutableList<Entity>

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun <T : Entity> spawn(entityType: Supplier<out EntityType<T>>, transform: Transform): T? =
            this.spawn(entityType.get(), transform)

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun <T : Entity> spawn(entityType: EntityType<T>, transform: Transform): T? =
            this.spawn(entityType, transform, SpawnEventProvider(LanternEventFactory::createSpawnEntityEvent)) {}

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun <T : Entity> spawn(entityType: Supplier<out EntityType<T>>, transform: Transform, fn: Consumer<T>): T? =
            this.spawn(entityType.get(), transform, fn)

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun <T : Entity> spawn(entityType: EntityType<T>, transform: Transform, fn: Consumer<T>): T? =
            this.spawn(entityType, transform, SpawnEventProvider(LanternEventFactory::createSpawnEntityEvent), fn::accept)

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun <T : Entity> spawn(entityType: Supplier<out EntityType<T>>, transform: Transform, fn: (T) -> Unit = {}): T? =
            this.spawn(entityType.get(), transform, fn)

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun <T : Entity> spawn(entityType: EntityType<T>, transform: Transform, fn: (T) -> Unit = {}): T? =
            this.spawn(entityType, transform, SpawnEventProvider(LanternEventFactory::createSpawnEntityEvent), fn)

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun <T : Entity> spawn(entityType: Supplier<out EntityType<T>>, transform: Transform, eventProvider: SpawnEventProvider): T? =
            this.spawn(entityType.get(), transform, eventProvider)

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun <T : Entity> spawn(entityType: EntityType<T>, transform: Transform, eventProvider: SpawnEventProvider): T? =
            this.spawn(entityType, transform, eventProvider) {}

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun <T : Entity> spawn(entityType: Supplier<out EntityType<T>>, transform: Transform, eventProvider: SpawnEventProvider, fn: Consumer<T>): T? =
            this.spawn(entityType.get(), transform, eventProvider, fn)

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun <T : Entity> spawn(entityType: EntityType<T>, transform: Transform, eventProvider: SpawnEventProvider, fn: Consumer<T>): T? =
            this.spawn(entityType, transform, eventProvider, fn::accept)

    /**
     * Performs [ConstructEntityEvent]s followed by a bulk [SpawnEntityEvent] and returns the constructed [Entity], if constructed.
     */
    fun <T : Entity> spawn(entityType: EntityType<T>, transform: Transform, eventProvider: SpawnEventProvider, fn: (entity: T) -> Unit = {}): T?

    /**
     * Finishes the [SpawnEntityEvent] by moving all the [Entity]s
     * in the event to the [World] they are supposed to go.
     */
    fun finishSpawnEvent(event: SpawnEntityEvent)
}

/**
 * A convenient constructor to allow to construct the [SpawnEventProvider]
 * through a SAM like conversion.
 */
inline fun SpawnEventProvider(crossinline fn: (Cause, MutableList<Entity>) -> SpawnEntityEvent) = object : SpawnEventProvider {
    override fun invoke(cause: Cause, entities: MutableList<Entity>) = fn(cause, entities)
}

@FunctionalInterface
interface SpawnEventProvider {

    operator fun invoke(cause: Cause, entities: MutableList<Entity>): SpawnEntityEvent
}
