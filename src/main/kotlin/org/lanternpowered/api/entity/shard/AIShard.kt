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
package org.lanternpowered.api.entity.shard

import org.lanternpowered.api.shard.Shard
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.ai.Goal
import org.spongepowered.api.entity.ai.GoalType
import org.spongepowered.api.entity.ai.task.AITask
import org.spongepowered.api.entity.living.Agent

import java.util.Optional

/**
 * The [AIShard] shard type.
 */
abstract class AIShard : Shard<AIShard>() {

    /**
     * The current target, usually according to the various
     * [AITask]s that are acting on this agent.
     * <p>Setting is usually used to bypass what the [AITask]s are
     * deciding to be the target.
     */
    abstract var target: Entity?

    /**
     * Gets a [Goal] based on the [GoalType].
     *
     * @param type GoalType to lookup
     * @param <T> Inferred agent type
     * @return The goal or [Optional.empty] if not found.
     */
    abstract fun <T : Agent> getGoal(type: GoalType): Goal<T>?
}
