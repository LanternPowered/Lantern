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
package org.lanternpowered.server.data.io.store

import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.Optional
import java.util.function.Supplier

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
     * Gets the value for the specified [Key] if present.
     *
     * @param key The key
     * @return The value if present
     */
    operator fun <E : Any> get(key: Supplier<out Key<out Value<E>>>): Optional<E> = get(key.get())

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
     * Sets the value for the specified [Key] and
     * returns the old value if it was present.
     *
     * @param key The key
     * @return The value if present
     */
    operator fun <E : Any> set(key: Supplier<out Key<out Value<E>>>, value: E): Optional<E> = set(key.get(), value)

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

    /**
     * Removes the value for the specified [Key] and
     * returns the old value if it was present.
     *
     * @param key The key
     * @return The value if present
     */
    fun <E : Any> remove(key: Supplier<out Key<out Value<E>>>): Optional<E> = remove(key.get())
}
