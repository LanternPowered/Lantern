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
package org.lanternpowered.api.behavior.default

import org.lanternpowered.api.behavior.Behavior
import org.lanternpowered.api.behavior.BehaviorContext
import org.lanternpowered.api.behavior.BehaviorContextKeys
import org.lanternpowered.api.behavior.BehaviorException
import org.lanternpowered.api.behavior.BehaviorType
import org.lanternpowered.api.ext.*

/**
 * This behavior opens a sign at a target location to a player.
 *
 * Returns true when the sign was successfully for a
 * player, if a player is present in the context.
 */
class OpenSignBehavior : Behavior {

    override fun apply(type: BehaviorType, ctx: BehaviorContext): Boolean {
        ctx[BehaviorContextKeys.Player]?.let {
            val location = ctx[BehaviorContextKeys.BlockLocation]
            if (location == null) {
                return false
            } else if (location.extent != it.world) {
                throw BehaviorException("World mismatch between player " +
                        "(${it.world.name}) and block location (${location.extent.name}).")
            }
            return it.openSignAt(location.blockPosition)
        }
        return false
    }
}
