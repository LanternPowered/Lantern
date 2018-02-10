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
package org.lanternpowered.server.entity.shards;

import org.lanternpowered.server.shards.Shard;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.ai.Goal;
import org.spongepowered.api.entity.ai.GoalType;
import org.spongepowered.api.entity.ai.task.AITask;
import org.spongepowered.api.entity.living.Agent;

import java.util.Optional;

import javax.annotation.Nullable;

public abstract class AIShard extends Shard {

    /**
     * Gets the current target, usually according to the various
     * {@link AITask}s that are acting on this agent.
     *
     * @return The target entity, if available
     */
    public abstract Optional<Entity> getTarget();

    /**
     * Sets the current target, usually to bypass what the {@link AITask}s are
     * deciding to be the target.
     *
     * @param target The target entity, or null
     */
    public abstract void setTarget(@Nullable Entity target);

    /**
     * Gets a {@link Goal} based on the {@link GoalType}.
     *
     * @param type GoalType to lookup
     * @param <T> Inferred agent type
     * @return The goal or {@link Optional#empty()} if not found.
     */
    public abstract <T extends Agent> Optional<Goal<T>> getGoal(GoalType type);
}
