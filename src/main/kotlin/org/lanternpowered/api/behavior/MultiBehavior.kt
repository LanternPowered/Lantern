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

import com.google.common.collect.LinkedListMultimap

/**
 * Represents a [Behavior] which allows multiple [Behavior]s to be defined.
 */
abstract class MultiBehavior protected constructor() : Behavior {

    private val behaviors = LinkedListMultimap.create<BehaviorType, Behavior>()

    /**
     * Binds the behavior to the given [BehaviorType]. Multiple
     * behaviors can be defined per type.
     */
    @JvmOverloads
    protected fun bind(type: BehaviorType, optional: Boolean = false, fn: (BehaviorType, BehaviorContext) -> Boolean) {
        this.behaviors.put(type, Behavior { _, ctx -> fn(type, ctx) || optional })
    }

    /**
     * Binds the behavior to the given [BehaviorType]. Multiple
     * behaviors can be defined per type.
     */
    @JvmOverloads
    protected fun bind(type: BehaviorType, optional: Boolean = false, fn: (BehaviorContext) -> Boolean) {
        bind(type, optional) { _, ctx -> fn(ctx) }
    }

    override fun apply(type: BehaviorType, ctx: BehaviorContext): Boolean {
        var success = true
        this.behaviors[type].forEach {
            success = it.tryApply(type, ctx)
        }
        return success
    }
}
