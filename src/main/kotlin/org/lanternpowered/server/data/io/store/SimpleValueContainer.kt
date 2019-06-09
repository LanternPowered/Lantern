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
package org.lanternpowered.server.data.io.store

import org.lanternpowered.api.ext.*
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.Optional

class SimpleValueContainer(val values: MutableMap<Key<*>, Any>) {

    /**
     * Gets the value for the specified [Key] if present.
     *
     * @param key The key
     * @return The value if present
     */
    operator fun <E : Any> get(key: Key<out Value<E>>): Optional<E> {
        return this.values[key].uncheckedCast<E?>().optional()
    }

    /**
     * Sets the value for the specified [Key] and
     * returns the old value if it was present.
     *
     * @param key The key
     * @return The value if present
     */
    operator fun <E : Any> set(key: Key<out Value<E>>, value: E): Optional<E> {
        return this.values.put(key, value).uncheckedCast<E?>().optional()
    }

    /**
     * Removes the value for the specified [Key] and
     * returns the old value if it was present.
     *
     * @param key The key
     * @return The value if present
     */
    fun <E : Any> remove(key: Key<out Value<E>>): Optional<E> {
        return this.values.remove(key).uncheckedCast<E?>().optional()
    }
}
