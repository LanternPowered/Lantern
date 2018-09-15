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
package org.lanternpowered.api.ext

import org.lanternpowered.api.entity.Transform
import org.lanternpowered.api.entity.spawn.EntitySpawnEntry
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.world.Location
import org.lanternpowered.api.world.World
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityTypes

/**
 * The default dropped item despawn delay.
 */
const val DROPPED_ITEM_DESPAWN_DELAY = 40

/**
 * Will return {@code null} if the stack is empty.
 */
operator fun ItemStack.not(): ItemStack? = if (isEmpty) null else this

/**
 * Executes the given function if the stack isn't empty.
 */
inline fun ItemStack.ifNotEmpty(fn: (ItemStack) -> Unit) {
    if (!isEmpty) fn(this)
}

fun ItemStackSnapshot.toDroppedItemSpawnEntry(location: Location<World>, fn: Entity.() -> Unit = {})
        = toDroppedItemSpawnEntry(Transform(location), fn)

fun ItemStackSnapshot.toDroppedItemSpawnEntry(transform: Transform<World>, fn: Entity.() -> Unit = {}): EntitySpawnEntry {
    val snapshot = this
    return EntitySpawnEntry(EntityTypes.ITEM, transform) {
        offer(Keys.REPRESENTED_ITEM, snapshot)
        offer(Keys.DESPAWN_DELAY, DROPPED_ITEM_DESPAWN_DELAY)
        fn(this)
    }
}
