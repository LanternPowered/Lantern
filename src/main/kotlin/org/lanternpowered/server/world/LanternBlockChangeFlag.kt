/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.world

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.world.BlockChangeFlag
import org.spongepowered.api.world.BlockChangeFlag.Factory as BlockChangeFlagFactory
import java.util.Objects

class LanternBlockChangeFlag private constructor(private val flags: Int) : BlockChangeFlag {

    companion object Factory : BlockChangeFlagFactory {

        private const val MASK_NEIGHBOR = 0x1
        private const val MASK_PHYSICS = 0x2
        private const val MASK_OBSERVER = 0x4
        private const val MASK_ALL = MASK_NEIGHBOR or MASK_PHYSICS or MASK_OBSERVER

        private val FLAGS: Array<LanternBlockChangeFlag>

        init {
            val flags = mutableListOf<LanternBlockChangeFlag>()
            for (i in 0..MASK_ALL)
                flags += LanternBlockChangeFlag(i)
            FLAGS = flags.toTypedArray()
        }

        override fun empty(): BlockChangeFlag = FLAGS[0]
    }

    override fun updateNeighbors(): Boolean = this.flags and MASK_NEIGHBOR != 0
    override fun performBlockPhysics(): Boolean = this.flags and MASK_PHYSICS != 0
    override fun notifyObservers(): Boolean = this.flags and MASK_OBSERVER != 0

    override fun withUpdateNeighbors(updateNeighbors: Boolean): LanternBlockChangeFlag =
            if (updateNeighbors) andFlag(MASK_NEIGHBOR) else andNotFlag(MASK_NEIGHBOR)

    override fun withPhysics(performBlockPhysics: Boolean): LanternBlockChangeFlag =
            if (performBlockPhysics) andFlag(MASK_PHYSICS) else andNotFlag(MASK_PHYSICS)

    override fun withNotifyObservers(notifyObservers: Boolean): LanternBlockChangeFlag =
            if (notifyObservers) andFlag(MASK_OBSERVER) else andNotFlag(MASK_OBSERVER)

    override fun inverse(): LanternBlockChangeFlag = FLAGS[this.flags.inv() and MASK_ALL]

    override fun andFlag(flag: BlockChangeFlag): LanternBlockChangeFlag = andFlag((flag as LanternBlockChangeFlag).flags)
    override fun andNotFlag(flag: BlockChangeFlag): LanternBlockChangeFlag = andNotFlag((flag as LanternBlockChangeFlag).flags)

    private fun andFlag(flags: Int): LanternBlockChangeFlag = FLAGS[this.flags or flags]
    private fun andNotFlag(flags: Int): LanternBlockChangeFlag = FLAGS[this.flags and flags.inv()]

    override fun toString(): String = ToStringHelper(this)
            .add("updateNeighbors", updateNeighbors())
            .add("performBlockPhysics", performBlockPhysics())
            .add("notifyObservers", notifyObservers())
            .toString()

    override fun equals(other: Any?): Boolean = other is LanternBlockChangeFlag && other.flags == flags
    override fun hashCode(): Int = Objects.hash(this.flags)
}
