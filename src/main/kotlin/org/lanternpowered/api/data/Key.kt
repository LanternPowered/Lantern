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
package org.lanternpowered.api.data

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.util.type.typeTokenOf
import org.lanternpowered.api.registry.builderOf
import org.lanternpowered.api.registry.CatalogBuilder
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.Value

typealias Key<V> = org.spongepowered.api.data.Key<V>

/**
 * Constructs a new [Key] with the given [ResourceKey] and value [TypeToken].
 */
fun <V : Value<*>> valueKeyOf(key: ResourceKey, valueType: TypeToken<V>, fn: KeyBuilder<V>.() -> Unit = {}): Key<V> =
        builderOf<KeyBuilder<V>>().key(key).type(valueType).requireExplicitRegistration().apply(fn).build()

/**
 * Constructs a new [Key] with the given [ResourceKey] and value type [V].
 */
inline fun <reified V : Value<*>> valueKeyOf(key: ResourceKey, fn: KeyBuilder<V>.() -> Unit = {}): Key<V> =
        builderOf<KeyBuilder<V>>().key(key).type(typeTokenOf<V>()).requireExplicitRegistration().apply(fn).build()

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@DslMarker
annotation class KeyBuilderDsl

/**
 * A builder class for [Key]s.
 */
@KeyBuilderDsl
interface KeyBuilder<V : Value<*>> : CatalogBuilder<Key<V>, KeyBuilder<V>> {

    /**
     * Starter method for the builder. This defines the generics for the
     * builder itself to provide the properly generified [Key].
     *
     * @param token The type token, preferably an anonymous
     * @param <T> The element type of the Key
     * @param <B> The base value type of the key
     * @return This builder, generified
     */
    fun <N : Value<*>> type(token: TypeToken<N>): KeyBuilder<N>

    /**
     * Sets the value range of the bounded value [Key].
     */
    fun <V : BoundedValue<E>, E : Comparable<E>> KeyBuilder<V>.range(range: ClosedRange<E>): KeyBuilder<V>

    /**
     * Sets the minimum value of the bounded value [Key].
     */
    fun <V : BoundedValue<E>, E : Any> KeyBuilder<V>.minimum(value: E): KeyBuilder<V>

    /**
     * Sets the minimum value supplier of the bounded value [Key].
     */
    fun <V : BoundedValue<E>, E : Any> KeyBuilder<V>.minimum(supplier: @KeyBuilderDsl () -> E): KeyBuilder<V>

    /**
     * Sets the maximum value of the bounded value [Key].
     */
    fun <V : BoundedValue<E>, E : Any> KeyBuilder<V>.maximum(value: E): KeyBuilder<V>

    /**
     * Sets the maximum value supplier of the bounded value [Key].
     */
    fun <V : BoundedValue<E>, E : Any> KeyBuilder<V>.maximum(supplier: @KeyBuilderDsl () -> E): KeyBuilder<V>

    /**
     * Sets the comparator of the bounded value [Key].
     */
    fun <V : Value<E>, E : Any> KeyBuilder<V>.comparator(
            comparator: @KeyBuilderDsl Comparator<in E>): KeyBuilder<V>

    /**
     * Sets the comparator of the bounded value [Key].
     */
    fun <V : Value<E>, E : Any> KeyBuilder<V>.includesTester(
            tester: @KeyBuilderDsl (E, E) -> Boolean): KeyBuilder<V>

    /**
     * Enables the requirement that the key is registered explicitly on a value collection.
     *
     * @return This builder, for chaining
     */
    fun requireExplicitRegistration(): KeyBuilder<V>
}
