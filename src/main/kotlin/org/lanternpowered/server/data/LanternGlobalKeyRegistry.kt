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
package org.lanternpowered.server.data

import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value

internal class LanternGlobalKeyRegistry : GlobalKeyRegistry {

    private val map = mutableMapOf<Key<*>, GlobalKeyRegistration<*, *>>()

    override val registrations = this.map.values.asUnmodifiableCollection()
    override val keys = this.map.keys.asUnmodifiableCollection()

    override fun <V : Value<E>, E : Any> get(key: Key<V>): GlobalKeyRegistration<V, E>? = this.map[key].uncheckedCast()

    private fun checkRegistration(key: Key<*>) {
        check(key !in this.map) { "The key ${key.key} is already registered." }
    }

    override fun <V : Value<E>, E : Any> getOrRegister(key: Key<V>): GlobalKeyRegistration<V, E> {
        return get(key) ?: register(key)
    }

    override fun <V : Value<E>, E : Any> register(key: Key<V>): GlobalKeyRegistration<V, E> {
        checkRegistration(key)
        val registration = LanternGlobalKeyRegistration(key)
        this.map[key] = registration
        return registration
    }
}
