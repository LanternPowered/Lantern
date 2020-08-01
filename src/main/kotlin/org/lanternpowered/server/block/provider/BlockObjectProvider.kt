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
@file:Suppress("FunctionName")

package org.lanternpowered.server.block.provider

import org.lanternpowered.api.block.BlockState
import org.lanternpowered.api.block.BlockType
import org.lanternpowered.api.util.Direction
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.api.world.Location
import org.lanternpowered.server.state.IState

interface BlockObjectProvider<T> {

    fun get(state: BlockState, location: Location?, face: Direction?): T
}

class ConstantBlockObjectProvider<T>(private val value: T) : SimpleBlockObjectProvider<T> {
    override fun get(state: BlockState): T = this.value
}

inline fun <T> SimpleBlockObjectProvider(crossinline fn: (state: BlockState) -> T): SimpleBlockObjectProvider<T> =
        object : SimpleBlockObjectProvider<T> {
            override fun get(state: BlockState): T = fn(state)
        }

interface SimpleBlockObjectProvider<T> : BlockObjectProvider<T> {

    fun get(state: BlockState): T

    override fun get(state: BlockState, location: Location?, face: Direction?): T = this.get(state)
}

class CachedSimpleBlockObjectProvider<T>(
        private val blockType: BlockType, fn: (state: BlockState) -> T
) : SimpleBlockObjectProvider<T> {

    private var initializer: ((state: BlockState) -> T)? = fn
    private var lock = Any()

    @Volatile private var cached: Array<Any?>? = null

    private fun getCache(): Array<Any?> {
        var cached = this.cached
        if (cached != null)
            return cached
        synchronized(this.lock) {
            cached = this.cached
            if (cached != null)
                return cached!!
            val initializer = this.initializer!!
            val states = this.blockType.validStates
            cached = Array(states.size) { null }
            for (state in states)
                cached!![(state as IState<*>).index] = initializer(state)
            this.cached = cached
            this.initializer = null
            return cached!!
        }
    }

    /**
     * A list with all the values in this cache.
     */
    val values: List<T>
        get() = this.getCache().asList().uncheckedCast()

    override fun get(state: BlockState): T = this.getCache()[(state as IState<*>).index].uncheckedCast()
}
