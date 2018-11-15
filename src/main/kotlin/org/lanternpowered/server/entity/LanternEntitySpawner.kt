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
package org.lanternpowered.server.entity

import org.lanternpowered.api.Lantern
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.entity.spawn.EntitySpawner
import org.lanternpowered.api.entity.spawn.EntitySpawnEntry
import org.lanternpowered.api.entity.spawn.SpawnEventProvider
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.collect.NonNullArrayList
import org.lanternpowered.server.world.LanternWorld
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.event.entity.SpawnEntityEvent

object LanternEntitySpawner : EntitySpawner {

    private fun performPreSpawning(causeStack: CauseStack, entries: Iterable<EntitySpawnEntry>): MutableList<Entity> {
        val entities = NonNullArrayList<Entity>()
        val cause = causeStack.currentCause
        for (entry in entries) {
            // Call the pre construction event
            val preConstructEvent = LanternEventFactory.createConstructEntityEventPre(
                    cause, entry.entityType, entry.transform)
            Lantern.eventManager.post(preConstructEvent)
            if (!preConstructEvent.isCancelled) {
                val transform = entry.transform
                // Calls the post construction event
                entities.add(transform.extent.createEntity(entry.entityType, transform.position) {
                    this.rotation = transform.rotation
                    this.scale = transform.scale
                    entry.entityPopulator(this)
                })
            }
        }
        return entities
    }

    override fun preSpawn(entries: Iterable<EntitySpawnEntry>) = performPreSpawning(CauseStack.current(), entries)

    override fun spawn(entries: Iterable<EntitySpawnEntry>, eventProvider: SpawnEventProvider): MutableList<Entity> {
        val causeStack = CauseStack.current()
        val entities = performPreSpawning(causeStack, entries)
        if (entities.isEmpty()) {
            return entities
        }
        val spawnEvent = eventProvider(causeStack.currentCause, entities)
        // Post the spawn event
        Lantern.eventManager.post(spawnEvent)
        // Spawn the entities in the world
        finishSpawnEvent(spawnEvent)
        return entities
    }

    override fun finishSpawnEvent(event: SpawnEntityEvent) {
        LanternWorld.finishSpawnEntityEvent(event)
    }
}
