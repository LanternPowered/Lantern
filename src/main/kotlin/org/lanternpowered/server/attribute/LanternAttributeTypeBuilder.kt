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
import org.lanternpowered.api.attribute.AttributeTypeBuilder
import org.lanternpowered.api.text.Text
import org.lanternpowered.server.catalog.AbstractCatalogBuilder
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.data.DataHolder

class LanternAttributeTypeBuilder : AbstractCatalogBuilder<AttributeType, AttributeTypeBuilder>(), AttributeTypeBuilder {

    private var name: Text? = null
    private var valueRange: ClosedFloatingPointRange<Double>? = null
    private var defaultValue: Double? = null
    private var supports: ((DataHolder) -> Boolean)? = null

    override fun defaultValue(default: Double) = apply { this.defaultValue = default }
    override fun supports(supports: (DataHolder) -> Boolean) = apply { this.supports = supports }
    override fun name(name: Text) = apply { this.name = name }
    override fun valueRange(range: ClosedFloatingPointRange<Double>) = apply { this.valueRange = range }

    override fun build(key: ResourceKey): AttributeType {
        val supports = this.supports ?: { true }
        val name = checkNotNull(this.name) { "The name must be set" }
        val valueRange = checkNotNull(this.valueRange) { "The value range must be set" }
        val defaultValue = checkNotNull(this.defaultValue) { "The default value must be set" }
        return LanternAttributeType(key, name, valueRange, defaultValue, supports)
    }

    override fun reset() = apply {
        this.supports = null
        this.name = null
        this.valueRange = null
        this.defaultValue = null
    }
}
