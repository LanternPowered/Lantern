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
package org.lanternpowered.api.entity.event

import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.entity.living.player.LanternPlayer
import org.lanternpowered.api.shard.event.Shardevent

abstract class TrackerChangeShardevent internal constructor() : Shardevent {

    /**
     * The [LanternPlayer]s.
     */
    abstract val players: List<LanternPlayer>

    /**
     * Is thrown when one or more [LanternPlayer]
     * started tracking a [LanternEntity].
     */
    data class Add(override val players: List<LanternPlayer>) : TrackerChangeShardevent()

    /**
     * Is thrown when one or more [LanternPlayer]
     * stopped tracking a [LanternEntity].
     */
    data class Remove(override val players: List<LanternPlayer>) : TrackerChangeShardevent()
}
