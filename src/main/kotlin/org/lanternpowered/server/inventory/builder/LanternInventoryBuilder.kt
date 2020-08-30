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
package org.lanternpowered.server.inventory.builder

import org.lanternpowered.api.item.inventory.builder.ColumnBuilder
import org.lanternpowered.api.item.inventory.builder.GridBuilder
import org.lanternpowered.api.item.inventory.builder.InventoryBuilder
import org.lanternpowered.api.item.inventory.builder.RowBuilder
import org.lanternpowered.api.item.inventory.builder.SlotBuilder
import org.spongepowered.api.item.inventory.ContainerType
import java.util.UUID

class LanternInventoryBuilder<I, S, R, C> : InventoryBuilder<I, S, R, C> {
    override fun tag(tag: String) {
        TODO("Not yet implemented")
    }

    override fun uniqueId(uniqueId: UUID) {
        TODO("Not yet implemented")
    }

    override fun inventory(inventory: I) {
        TODO("Not yet implemented")
    }

    override fun group(fn: InventoryBuilder<I, S, R, C>.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun slot(fn: SlotBuilder<I>.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun slot() {
        TODO("Not yet implemented")
    }

    override fun column(height: Int, fn: ColumnBuilder<I, S, C>.(y: Int) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun column(fn: ColumnBuilder<I, S, C>.(y: Int) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun row(width: Int, fn: RowBuilder<I, S, R>.(x: Int) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun row(fn: RowBuilder<I, S, R>.(x: Int) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun grid(width: Int, height: Int, fn: GridBuilder<I, S, R, C>.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun grid(width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    override fun containerType(containerType: ContainerType) {
        TODO("Not yet implemented")
    }

    override fun grid(fn: GridBuilder<I, S, R, C>.() -> Unit) {
        TODO("Not yet implemented")
    }

}
