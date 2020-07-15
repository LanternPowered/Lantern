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
package org.lanternpowered.server.attribute

import org.lanternpowered.api.attribute.AttributeType
import org.lanternpowered.api.ResourceKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.text.Text

class LanternAttributeType(
        key: ResourceKey,
        override val name: Text,
        override val valueRange: ClosedFloatingPointRange<Double>,
        override val defaultValue: Double,
        val supports: (DataHolder) -> Boolean
) : DefaultCatalogType(key), AttributeType {

    override fun supports(dataHolder: DataHolder): Boolean = this.supports.invoke(dataHolder)

    override fun toStringHelper() = super.toStringHelper()
            .add("name", this.name)
            .add("valueRange", this.valueRange)
            .add("defaultValue", this.defaultValue)
}
