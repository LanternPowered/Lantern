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
}

inline fun <T : Comparable<T>, V> stateKeyValueTransformer(
        crossinline toKeyValue: (T) -> V,
        crossinline toStateValue: (V) -> T
) = object : StateKeyValueTransformer<T, V> {

    override fun toKeyValue(stateValue: T): V = toKeyValue(stateValue)
    override fun toStateValue(keyValue: V): T = toStateValue(keyValue)
}

fun <T : Comparable<T>> identityStateKeyValueTransformer(): StateKeyValueTransformer<T, T>
        = IdentityStateKeyValueTransformer as StateKeyValueTransformer<T, T>

private object IdentityStateKeyValueTransformer : StateKeyValueTransformer<Comparable<Any>, Any> {

    override fun toStateValue(keyValue: Any): Comparable<Any> = keyValue as Comparable<Any>
    override fun toKeyValue(stateValue: Comparable<Any>) = stateValue
}
