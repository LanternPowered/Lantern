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
package org.lanternpowered.api.world.scheduler

import org.lanternpowered.api.world.BlockPosition
import org.lanternpowered.api.world.World

/**
 * Represents a scheduled update in a world.
 */
interface ScheduledUpdate<T : Any> : org.spongepowered.api.scheduler.ScheduledUpdate<T> {

    @JvmDefault
    override fun getState(): ScheduledUpdateState

    @JvmDefault
    override fun getWorld(): World

    /**
     * The block position at which the
     * update is occurring.
     */
    val position: BlockPosition
}
