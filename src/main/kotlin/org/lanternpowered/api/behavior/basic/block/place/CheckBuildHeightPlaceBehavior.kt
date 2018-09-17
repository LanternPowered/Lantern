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

import org.lanternpowered.api.behavior.Behavior
import org.lanternpowered.api.behavior.BehaviorContext
import org.lanternpowered.api.behavior.BehaviorContextKeys
import org.lanternpowered.api.behavior.BehaviorType
import org.lanternpowered.api.behavior.basic.PlaceBlockBehaviorBase
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.text.chat.ChatTypes
import org.lanternpowered.server.text.translation.TranslationHelper.t

/**
 * This behavior checks whether all the placed snapshots
 * ([PlaceBlockBehaviorBase.PlacedSnapshots]) are located
 * without the build limits of the world.
 *
 * This behavior returns the result of the backing [behavior].
 *
 * @property behavior The behavior for which the build height will be checked
 */
class CheckBuildHeightPlaceBehavior(val behavior: Behavior) : Behavior {

    override fun apply(type: BehaviorType, ctx: BehaviorContext): Boolean {
        val ctxSnapshot = ctx.createSnapshot()
        if (!this.behavior.tryApply(type, ctx)) return false
        val snapshots = ctx[PlaceBlockBehaviorBase.PlacedSnapshots] ?: return true
        for (snapshot in snapshots) {
            val location = snapshot.location
            val buildHeight = location.extent.dimension.buildHeight
            if (location.y >= buildHeight) {
                ctx.restoreSnapshot(ctxSnapshot)
                ctx.addFinalizer {
                    ctx[BehaviorContextKeys.PLAYER]?.sendMessage(ChatTypes.ACTION_BAR, t("build.tooHigh", buildHeight))
                }
                break
            }
        }
        return true
    }
}
