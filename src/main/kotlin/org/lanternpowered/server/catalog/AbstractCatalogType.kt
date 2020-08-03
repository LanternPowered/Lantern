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
package org.lanternpowered.server.catalog

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.util.ToString
import org.spongepowered.api.NamedCatalogType

abstract class AbstractCatalogType : CatalogType, ToString {
    override fun toStringHelper(): ToStringHelper = this.applyCatalog(super.toStringHelper())
    override fun toString(): String = this.toStringHelper().toString()
}

private fun CatalogType.applyCatalog(helper: ToStringHelper): ToStringHelper = helper.apply {
    if (this@applyCatalog is InternalCatalogType)
        helper.addFirst("internalId", internalId)
    if (this@applyCatalog is NamedCatalogType)
        helper.addFirst("name", name)
    helper.addFirst("id", key)
}

fun CatalogType.asString(): String =
        this.applyCatalog(if (this is ToString) toStringHelper() else ToStringHelper(this)).toString()
