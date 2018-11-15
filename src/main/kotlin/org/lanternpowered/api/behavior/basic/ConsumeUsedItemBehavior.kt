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
package org.lanternpowered.api.behavior.basic

import org.lanternpowered.api.behavior.Behavior
import org.lanternpowered.api.behavior.BehaviorContext
import org.lanternpowered.api.behavior.BehaviorContextKeys
import org.lanternpowered.api.behavior.BehaviorType
import org.lanternpowered.api.ext.*
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.gamemode.GameModes

/**
 * Consumes one item from the used item stack. Ignored when
 * used by a player in creative.
 *
 * Returns whether the quantity was successfully removed.
 */
class ConsumeUsedItemBehavior(
        private val quantityToDecrease: Int = 1,
        private val ignoreCreative: Boolean = true
) : Behavior {

    override fun apply(type: BehaviorType, ctx: BehaviorContext): Boolean {
        ctx[BehaviorContextKeys.PLAYER]?.let {
            if (this.ignoreCreative && it.require(Keys.GAME_MODE) == GameModes.CREATIVE) return true
        }
        val usedItem = ctx[BehaviorContextKeys.USED_ITEM]?.createStack()
        if (usedItem != null) {
            val newQuantity = usedItem.quantity - this.quantityToDecrease
            if (newQuantity < 0) return false
            usedItem.quantity = newQuantity
            ctx[BehaviorContextKeys.USED_ITEM] = usedItem.createSnapshot()
        }
        val slot = ctx[BehaviorContextKeys.USED_SLOT]
        if (slot != null) {
            if (usedItem == null && slot.stackSize < this.quantityToDecrease) return false
            ctx.addFinalizer {
                // Apply changes to the slot if the behavior is accepted
                if (usedItem != null) {
                    slot.set(usedItem)
                } else {
                    slot.poll(this.quantityToDecrease)
                }
            }
        }
        return true
    }
}
