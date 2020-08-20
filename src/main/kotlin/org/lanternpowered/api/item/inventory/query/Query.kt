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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.item.inventory.query

import org.lanternpowered.api.item.inventory.ExtendedInventory
import org.lanternpowered.api.item.inventory.Inventory
import java.util.function.Supplier

typealias Query = org.spongepowered.api.item.inventory.query.Query
typealias QueryType = org.spongepowered.api.item.inventory.query.QueryType
typealias NoParamQueryType = org.spongepowered.api.item.inventory.query.QueryType.NoParam
typealias OneParamQueryType<T> = org.spongepowered.api.item.inventory.query.QueryType.OneParam<T>
typealias TwoParamQueryType<T1, T2> = org.spongepowered.api.item.inventory.query.QueryType.TwoParam<T1, T2>

/**
 * Returns the query for the given parameter.
 *
 * @param param The parameter
 * @return The new query
 */
fun <T> Supplier<OneParamQueryType<T>>.of(param: T): Query =
        this.get().of(param)

/**
 * Returns the query for the given parameters.
 *
 * @param param1 The first parameter
 * @param param2 The second parameter
 * @return The new query
 */
fun <T1, T2> Supplier<TwoParamQueryType<T1, T2>>.of(param1: T1, param2: T2): Query =
        this.get().of(param1, param2)

/**
 * Gets the normal hotbar as an extended hotbar.
 */
inline fun Query.fix(): ExtendedQuery<ExtendedInventory> {
    kotlin.contracts.contract { returns() implies (this@fix is ExtendedQuery<*>) }
    @Suppress("UNCHECKED_CAST")
    return this as ExtendedQuery<ExtendedInventory>
}

/**
 * Gets the normal hotbar as an extended hotbar.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun <I : ExtendedInventory> ExtendedQuery<I>.fix(): ExtendedQuery<I> = this

/**
 * An extended version of [Query].
 */
interface ExtendedQuery<I : Inventory> : Query {

    override fun execute(inventory: Inventory): I
}
