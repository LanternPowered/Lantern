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
package org.lanternpowered.api.item.inventory.query

import org.lanternpowered.api.data.Key
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.registry.factoryOf
import org.spongepowered.api.data.value.Value
import java.util.function.Supplier

typealias InventoryFilterBuilderFunction<I> = InventoryFilterBuilder<I>.() -> InventoryFilter<I>

/**
 * Builds an [InventoryFilter] from the builder function.
 */
fun <I : Inventory> InventoryFilterBuilderFunction<I>.build(): InventoryFilter<I> =
        this.invoke(factoryOf<InventoryFilterBuilder.Factory>().of())

/**
 * A filter for inventories.
 */
typealias InventoryFilter<I> = (inventory: I) -> Boolean

interface InventoryFilterBuilder<I : Inventory> {

    interface Factory {

        fun <I : Inventory> of(): InventoryFilterBuilder<I>
    }

    infix fun <V : Any> Key<out Value<V>>.eq(value: V?): InventoryFilter<I>

    infix fun <V : Any> Key<out Value<V>>.neq(value: V?): InventoryFilter<I>

    infix fun <V : Any> Key<out Value<V>>.greater(value: V?): InventoryFilter<I>

    infix fun <V : Any> Key<out Value<V>>.greaterEq(value: V?): InventoryFilter<I>

    infix fun <V : Any> Key<out Value<V>>.less(value: V?): InventoryFilter<I>

    infix fun <V : Any> Key<out Value<V>>.lessEq(value: V?): InventoryFilter<I>

    infix fun <V : Any> Supplier<out Key<out Value<V>>>.eq(value: V?): InventoryFilter<I> =
            this.get().eq(value)

    infix fun <V : Any> Supplier<out Key<out Value<V>>>.neq(value: V?): InventoryFilter<I> =
            this.get().neq(value)

    infix fun <V : Any> Supplier<out Key<out Value<V>>>.greater(value: V?): InventoryFilter<I> =
            this.get().greater(value)

    infix fun <V : Any> Supplier<out Key<out Value<V>>>.greaterEq(value: V?): InventoryFilter<I> =
            this.get().greaterEq(value)

    infix fun <V : Any> Supplier<out Key<out Value<V>>>.less(value: V?): InventoryFilter<I> =
            this.get().less(value)

    infix fun <V : Any> Supplier<out Key<out Value<V>>>.lessEq(value: V?): InventoryFilter<I> =
            this.get().lessEq(value)

    infix fun <V : Any> Supplier<out Key<out Value<V>>>.eq(value: Supplier<out V?>): InventoryFilter<I> =
            this.get().eq(value.get())

    infix fun <V : Any> Supplier<out Key<out Value<V>>>.neq(value: Supplier<out V?>): InventoryFilter<I> =
            this.get().neq(value.get())

    infix fun <V : Any> Supplier<out Key<out Value<V>>>.greater(value: Supplier<out V?>): InventoryFilter<I> =
            this.get().greater(value.get())

    infix fun <V : Any> Supplier<out Key<out Value<V>>>.greaterEq(value: Supplier<out V?>): InventoryFilter<I> =
            this.get().greaterEq(value.get())

    infix fun <V : Any> Supplier<out Key<out Value<V>>>.less(value: Supplier<out V?>): InventoryFilter<I> =
            this.get().less(value.get())

    infix fun <V : Any> Supplier<out Key<out Value<V>>>.lessEq(value: Supplier<out V?>): InventoryFilter<I> =
            this.get().lessEq(value.get())

    infix fun <V : Any> Key<out Value<V>>.eq(value: Supplier<out V?>): InventoryFilter<I> =
            this.eq(value.get())

    infix fun <V : Any> Key<out Value<V>>.neq(value: Supplier<out V?>): InventoryFilter<I> =
            this.neq(value.get())

    infix fun <V : Any> Key<out Value<V>>.greater(value: Supplier<out V?>): InventoryFilter<I> =
            this.greater(value.get())

    infix fun <V : Any> Key<out Value<V>>.greaterEq(value: Supplier<out V?>): InventoryFilter<I> =
            this.greaterEq(value.get())

    infix fun <V : Any> Key<out Value<V>>.less(value: Supplier<out V?>): InventoryFilter<I> =
            this.less(value.get())

    infix fun <V : Any> Key<out Value<V>>.lessEq(value: Supplier<out V?>): InventoryFilter<I> =
            this.lessEq(value.get())

    infix fun InventoryFilter<I>.and(filter: InventoryFilter<I>): InventoryFilter<I>

    infix fun InventoryFilter<I>.or(filter: InventoryFilter<I>): InventoryFilter<I>
}
