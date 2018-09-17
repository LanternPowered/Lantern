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
package org.lanternpowered.api.behavior.basic.block.place

import org.lanternpowered.api.behavior.BehaviorContext
import org.lanternpowered.api.behavior.BehaviorContextKeys
import org.lanternpowered.api.behavior.BehaviorType
import org.lanternpowered.api.behavior.basic.PlaceBlockBehaviorBase
import org.lanternpowered.api.block.BlockSnapshotBuilder
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.world.BlockChangeFlags

/**
 * The block placement behavior base.
 */
open class BasicPlaceBlockBehavior : PlaceBlockBehaviorBase {

    override fun apply(type: BehaviorType, ctx: BehaviorContext, placed: MutableList<BlockSnapshotBuilder>): Boolean {
        val slot = ctx[BehaviorContextKeys.USED_SLOT]
        val stack = (ctx[BehaviorContextKeys.USED_ITEM]?.createStack() ?: slot?.peek()).orEmpty()
        // A used item or slot is expected for this behavior to work
        if (stack.isEmpty) return false
        // Add a finalizer which will actually place the blocks in the world
        ctx.addFinalizer {
            for (builder in placed) {
                val snapshot = builder.build()
                snapshot.restore(false, BlockChangeFlags.ALL)
            }
        }
        return place(type, ctx, stack, placed)
    }

    open fun place(type: BehaviorType, ctx: BehaviorContext, placedItem: ItemStack, placed: MutableList<BlockSnapshotBuilder>): Boolean {
        val blockType = !placedItem.type.block ?: return false
        val location = ctx[BehaviorContextKeys.BLOCK_LOCATION] ?: return false
        // Convert the stack into a snapshot that can be placed
        val builder = BlockSnapshotBuilder()
                .location(location)
                .blockState(blockType.defaultState)
        placedItem.values.forEach { builder.add(it) }
        placed.add(builder)
        return true
    }
}
