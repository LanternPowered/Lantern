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
package org.lanternpowered.api.behavior.basic.block

import org.lanternpowered.api.Lantern
import org.lanternpowered.api.behavior.Behavior
import org.lanternpowered.api.behavior.BehaviorContext
import org.lanternpowered.api.behavior.BehaviorContextKeys
import org.lanternpowered.api.behavior.BehaviorType
import org.lanternpowered.api.behavior.BehaviorTypes
import org.lanternpowered.api.behavior.basic.DropsCollectionBehavior
import org.lanternpowered.api.entity.spawn.SpawnEventProvider
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.collect.NonNullArrayList

/**
 * A behavior to spawn drops for a block.
 */
class SpawnBlockDropsBehavior : Behavior {

    override fun apply(type: BehaviorType, ctx: BehaviorContext): Boolean {
        // Add the drops collection to the context, just in case
        ctx.addContextIfAbsent(DropsCollectionBehavior.DropsCollectionKey) { NonNullArrayList() }
        // Process the drop collecting behavior
        val behavior = ctx.behaviorCollection.getOrEmpty(BehaviorTypes.Generic.CollectDrops)
        behavior.apply(ctx)
        // Add a finalizer which will handle the drop events and spawn dropped items in the world
        ctx.addFinalizer {
            val drops = ctx[DropsCollectionBehavior.DropsCollectionKey] ?: NonNullArrayList()
            // Throw events
            val preDropEvent = LanternEventFactory.createDropItemEventPre(ctx.currentCause, drops.toImmutableList(), drops)
            Lantern.eventManager.post(preDropEvent)
            if (!preDropEvent.isCancelled) {
                // The event was successful, now create entities for all the dropped items
                val transform = ctx.requireContext(BehaviorContextKeys.BlockLocation).add(0.5, 0.5, 0.5).toTransform()
                // Drop entities
                Lantern.entitySpawner.spawn(drops.map { it.toDroppedItemSpawnEntry(transform) },
                        SpawnEventProvider(LanternEventFactory::createDropItemEventDestruct))
            }
        }
        return true
    }
}
