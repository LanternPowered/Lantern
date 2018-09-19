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
package org.lanternpowered.api.entity.weather

import com.flowpowered.math.vector.Vector3d
import org.lanternpowered.api.Lantern
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.entity.EntityTypes
import org.lanternpowered.api.entity.Transform
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.AABB
import org.lanternpowered.api.world.chunk.Chunk
import org.lanternpowered.api.world.World

typealias Lightning = org.spongepowered.api.entity.weather.Lightning

object LightningSpawner {

    /**
     * When there are entities in this region around the lightning
     * bolt, the bolt will be redirected to the entity.
     */
    private val moveToEntityRegion = AABB(-1.5, -3.0, -1.5, 1.5, 256.0, 1.5)

    /**
     * Spawns [Lightning] randomly across all the loaded [Chunk]s in the [World].
     *
     * @param world The world to spawn lightning bolts in
     * @param chance The chance that a lightning bolt will spawn per attempt in a chunk
     * @param attemptsPerChunk The attempts to spawn a bolt per chunk
     * @param moveToEntityRegion The region to check for entities around the bolt, if found, move bolt to their location
     * @param locationFilter Advanced filtering whether the [Transform] is valid
     */
    fun spawnLightning(
            world: World,
            chance: Double = 0.0000002,
            attemptsPerChunk: Int = 2,
            moveToEntityRegion: AABB = this.moveToEntityRegion,
            locationFilter: (Transform<World>) -> Boolean = { true }
    ) {
        val chunks = world.loadedChunks
        val chanceInt = (1f / Math.max(chance, 0.000000000001)).toInt()

        for (chunk in chunks) {
            for (i in 0 until attemptsPerChunk) {
                if (random.nextInt(chanceInt) != 0) {
                    continue
                }

                val value = random.nextInt(0x10000)
                val x = chunk.x shl 4 or (value and 0xf)
                val z = chunk.z shl 4 or (value shr 4 and 0xf)

                var pos = Vector3d(x.toDouble(), world.getHighestYAt(x, z).toDouble(), z.toDouble())

                // Look for nearby entities to see if the lightning bolt should be moved
                world.getIntersectingEntities(moveToEntityRegion.offset(pos)).pickRandom()?.let {
                    pos = it.location.position
                }

                val transform = Transform(world, pos)
                if (!locationFilter(transform)) continue

                val lightningPreEvent = LanternEventFactory.createLightningEventPre(CauseStack.current().currentCause)
                Lantern.eventManager.post(lightningPreEvent)
                if (!lightningPreEvent.isCancelled) {
                    Lantern.entitySpawner.spawn(EntityTypes.LIGHTNING, Transform(world, pos))
                }
            }
        }
    }
}
