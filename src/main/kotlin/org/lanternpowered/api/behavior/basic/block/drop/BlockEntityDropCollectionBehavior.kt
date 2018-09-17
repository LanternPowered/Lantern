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
package org.lanternpowered.api.behavior.basic.block.drop

import org.lanternpowered.api.behavior.BehaviorContext
import org.lanternpowered.api.behavior.BehaviorContextKeys
import org.lanternpowered.api.behavior.BehaviorType
import org.lanternpowered.api.behavior.basic.DropsCollectionBehavior
import org.lanternpowered.api.block.entity.BlockEntity
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.crafting.CraftingOutput

/**
 * Collects drops from the [BlockEntity] if it's a [Carrier].
 */
class BlockEntityDropCollectionBehavior : DropsCollectionBehavior {

    override fun apply(type: BehaviorType, ctx: BehaviorContext, itemDrops: MutableList<ItemStackSnapshot>) {
        val blockEntity = ctx[BehaviorContextKeys.BLOCK_LOCATION]?.blockEntity
        if (blockEntity is Carrier) {
            val inventory = blockEntity.inventory
            inventory.slots().stream()
                    .filter { it !is CraftingOutput }
                    .forEach { slot -> slot.peek().ifNotEmpty { itemDrops.add(it.createSnapshot()) } }
        }
    }
}
