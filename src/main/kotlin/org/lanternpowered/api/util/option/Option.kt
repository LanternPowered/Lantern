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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.api.util.option

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.util.type.TypeToken
import kotlin.reflect.KClass

/**
 * Represents a option.
 *
 * @constructor Constructs and registers a new [Option]
 * @property defaultValue The default option value
 * @property type The type of option value
 * @param T The option map type this option targets
 * @param V The value type of this option
 */
data class Option<T : OptionMapType, V>(
        val key: NamespacedKey,
        val type: TypeToken<V>,
        val defaultValue: V,
        val mapType: Class<T>
) {

    constructor(key: NamespacedKey, type: TypeToken<V>, defaultValue: V, mapType: KClass<T>) :
            this(key, type, defaultValue, mapType.java)

    init {
        val registry = OptionMapRegistry.of(this.mapType)
        // Each key per map type should be unique
        check(this.key !in registry) {
            "There is already a option with id '$key' registered for map type '${mapType.name}'." }
        // Register the key
        registry.byKey[this.key] = this
    }
}
