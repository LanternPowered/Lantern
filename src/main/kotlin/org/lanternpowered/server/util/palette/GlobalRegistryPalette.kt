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
package org.lanternpowered.server.util.palette

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.util.palette.GlobalPalette
import org.lanternpowered.server.registry.InternalCatalogTypeRegistry
import java.util.function.Supplier

/**
 * Gets this internal catalog type registry as a [GlobalPalette].
 */
fun <T : CatalogType> InternalCatalogTypeRegistry<T>.asPalette(default: () -> T): GlobalPalette<T> =
        asPalette(Supplier(default))

/**
 * Gets this internal catalog type registry as a [GlobalPalette].
 */
fun <T : CatalogType> InternalCatalogTypeRegistry<T>.asPalette(default: Supplier<out T>): GlobalPalette<T> =
        GlobalRegistryPalette(this, default)

private class GlobalRegistryPalette<T : CatalogType>(
        private val registry: InternalCatalogTypeRegistry<T>,
        private val defaultSupplier: Supplier<out T>
) : GlobalPalette<T> {

    override fun getIdOrAssign(obj: T): Int = this.registry.getId(obj)
    override fun getId(obj: T): Int = this.registry.getId(obj)
    override fun get(id: Int): T? = this.registry.get(id)

    override val default: T
        get() = this.defaultSupplier.get()

    override val entries: Collection<T>
        get() = this.registry.all

    override val size: Int
        get() = this.registry.all.size
}
