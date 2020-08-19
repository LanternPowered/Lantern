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
package org.lanternpowered.server.inventory.query

import org.lanternpowered.api.item.inventory.EmptyInventory
import org.lanternpowered.api.item.inventory.ExtendedInventory
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.fix
import org.lanternpowered.api.item.inventory.query.NoParamQueryType
import org.lanternpowered.api.item.inventory.query.OneParamQueryType
import org.lanternpowered.api.item.inventory.query.Query
import org.lanternpowered.api.item.inventory.query.QueryBuilder
import org.lanternpowered.api.item.inventory.query.QueryType
import org.lanternpowered.api.item.inventory.query.TwoParamQueryType
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.inventory.AbstractInventory

class AndQuery(private val queries: List<Query>) : Query {
    override fun execute(inventory: Inventory): Inventory =
            this.queries.fold(inventory, Inventory::query)
}

class OrQuery(private val queries: List<Query>) : Query {
    override fun execute(inventory: Inventory): Inventory {
        for (query in this.queries) {
            val result = inventory.query(query)
            if (result !is EmptyInventory)
                return result
        }
        return (inventory as AbstractInventory).empty()
    }
}

class LanternQueryBuilder : QueryBuilder {

    private var query: Query = EmptyQuery

    override fun and(vararg queries: Query): QueryBuilder = this.apply {
        this.query = AndQuery(queries.toImmutableList())
    }

    override fun or(vararg queries: Query): QueryBuilder = this.apply {
        this.query = OrQuery(queries.toImmutableList())
    }

    override fun build(): Query = this.query

    override fun reset(): QueryBuilder = this.apply {
        this.query = EmptyQuery
    }
}

object EmptyQuery : Query {
    override fun execute(inventory: Inventory): Inventory =
            (inventory as AbstractInventory).empty()
}

abstract class LanternQueryType(key: NamespacedKey) : DefaultCatalogType(key), QueryType

class LanternTwoParamQueryType<T1, T2>(
        key: NamespacedKey, private val fn: (inventory: ExtendedInventory, param1: T1, param2: T2) -> Inventory
) : LanternQueryType(key), TwoParamQueryType<T1, T2> {
    override fun of(param1: T1, param2: T2): Query = Query { inventory -> this.fn(inventory.fix(), param1, param2) }
}

class LanternOneParamQueryType<T>(
        key: NamespacedKey, private val fn: (inventory: ExtendedInventory, param: T) -> Inventory
) : LanternQueryType(key), OneParamQueryType<T> {
    override fun of(param: T): Query = Query { inventory -> this.fn(inventory.fix(), param) }
}

class LanternNoParamQueryType(
        key: NamespacedKey, private val fn: (inventory: ExtendedInventory) -> Inventory
) : LanternQueryType(key), NoParamQueryType, Query {
    override fun toQuery(): Query = this
    override fun execute(inventory: Inventory): Inventory = this.fn(inventory.fix())
}
