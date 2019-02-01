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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.server.state

/**
 * Transforms between state and key values.
 *
 * @property T The type of the state value
 * @property V The type of the key value
 */
interface StateKeyValueTransformer<T : Comparable<T>, V> {

    fun toKeyValue(stateValue: T): V
    fun toStateValue(keyValue: V): T

    companion object {

        @JvmStatic
        fun <T : Comparable<T>, V> identity(): StateKeyValueTransformer<T, V> {
            return IdentityStateKeyValueTransformer as StateKeyValueTransformer<T, V>
        }
    }
}

private object IdentityStateKeyValueTransformer : StateKeyValueTransformer<Comparable<Any>, Any> {

    override fun toStateValue(keyValue: Any): Comparable<Any> = keyValue as Comparable<Any>
    override fun toKeyValue(stateValue: Comparable<Any>) = stateValue
}
