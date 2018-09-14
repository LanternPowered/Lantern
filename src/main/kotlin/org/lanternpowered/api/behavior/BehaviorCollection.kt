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
package org.lanternpowered.api.behavior

/**
 * Represents a collection of [Behavior]s
 * assigned to [BehaviorType]s.
 */
interface BehaviorCollection {

    /**
     * Assigns the given [Behavior] to the all
     * the annotation bound behavior types.
     *
     * @param behavior The behavior to assign
     * @return This for chaining
     */
    fun assign(behavior: Behavior): BehaviorCollection

    /**
     * Assigns the given [Behavior] to the [BehaviorType].
     *
     * @param type The behavior type the behavior should be assigned to
     * @param behavior The behavior to assign
     * @return This for chaining
     */
    fun assign(type: BehaviorType, behavior: Behavior): BehaviorCollection

    /**
     * Modifies the [Behavior] that's currently registered and
     * returns the new behavior. If no behavior is registered, then a empty
     * one which always returns `true` will be provided instead.
     *
     * @param type The behavior type of which its behavior should be modified
     * @param function The function to modify the registered behavior
     * @return This for chaining
     */
    fun modify(type: BehaviorType, function: (Behavior) -> Behavior): BehaviorCollection

    /**
     * Gets the [Behavior] for the given [BehaviorType], if present.
     *
     * @param type The behavior type
     * @return The behavior, if found
     */
    operator fun get(type: BehaviorType): Behavior?

    /**
     * Gets the [Behavior] for the given [BehaviorType] if present. When it's
     * not present then will an empty one which always returns `true` be provided.
     *
     * @param type The behavior type
     * @return The behavior
     */
    fun getOrEmpty(type: BehaviorType): Behavior
}
