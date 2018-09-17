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
package org.lanternpowered.server.behavior.basic.place

import org.lanternpowered.api.behavior.BehaviorContext
import org.lanternpowered.api.behavior.BehaviorType
import org.lanternpowered.api.behavior.basic.PlaceBlockBehaviorBase
import org.lanternpowered.api.block.BlockSnapshotBuilder
import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.block.LanternBlockType
import org.spongepowered.api.entity.ExperienceOrb
import org.spongepowered.api.entity.Item

class PlaceCollisonDetectionBehavior(
        private val collisionEntityFilter: (Entity) -> Boolean = { entity -> entity !is Item && entity !is ExperienceOrb }
) : PlaceBlockBehaviorBase {

    override fun apply(type: BehaviorType, ctx: BehaviorContext, placed: MutableList<BlockSnapshotBuilder>): Boolean {
        for (snapshot in placed) {
            val location = snapshot.location ?: continue
            val blockState = location.block
            val collisionBoxesProvider = (blockState.type as LanternBlockType).collisionBoxesProvider
            if (collisionBoxesProvider != null) {
                val collisionBoxes = collisionBoxesProvider.get(blockState, null, null)
                for (collisionBox in collisionBoxes) {
                    if (location.extent.hasIntersectingEntities(collisionBox.offset(location.blockPosition), this.collisionEntityFilter)) {
                        return false
                    }
                }
            }
        }
        return true
    }
}
