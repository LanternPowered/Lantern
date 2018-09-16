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
package org.lanternpowered.api.behavior

import org.lanternpowered.api.catalog.CatalogKeys
import org.lanternpowered.api.cause.CauseContextKey
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.world.Location
import org.lanternpowered.api.world.World
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.Slot

/**
 * A collection of [CauseContextKey]s related to [BehaviorContext]s.
 */
object BehaviorContextKeys {

    val Player = CauseContextKey<Player>(CatalogKeys.minecraft("player"))

    val BlockLocation = CauseContextKey<Location<World>>(CatalogKeys.minecraft("block_location"))

    /**
     * The [ItemStackSnapshot] that was interacted with.
     */
    val UsedItem = CauseContextKey<ItemStackSnapshot>(CatalogKeys.minecraft("interacted_item"))

    /**
     * The [Slot] from which its item was being interacted with.
     */
    val UsedSlot = CauseContextKey<Slot>(CatalogKeys.minecraft("interacted_slot"))
}
