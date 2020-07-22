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
package org.lanternpowered.api.attribute

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.catalog.CatalogedBy
import org.lanternpowered.api.registry.CatalogBuilder
import org.lanternpowered.api.registry.builderOf
import org.lanternpowered.api.text.Text
import org.spongepowered.api.data.DataHolder

/**
 * Constructs a new [AttributeType].
 */
fun attributeTypeOf(key: NamespacedKey, fn: AttributeTypeBuilder.() -> Unit): AttributeType =
        builderOf<AttributeTypeBuilder>().key(key).apply(fn).build()

/**
 * Represents an attribute.
 */
@CatalogedBy(AttributeTypes::class)
interface AttributeType : CatalogType {

    /**
     * The name of the attribute.
     */
    val name: Text

    /**
     * The range of valid values.
     */
    val valueRange: ClosedFloatingPointRange<Double>

    /**
     * The default value of the attribute.
     */
    val defaultValue: Double

    /**
     * Checks whether data holder is supported by this attribute.
     */
    fun supports(dataHolder: DataHolder): Boolean
}

interface AttributeTypeBuilder : CatalogBuilder<AttributeType, AttributeTypeBuilder> {

    /**
     * Sets the name of the attribute.
     *
     * @param name The name
     * @return This builder, for chaining
     */
    fun name(name: Text): AttributeTypeBuilder

    /**
     * Sets the range of valid values.
     *
     * @param range The value range
     * @return This builder, for chaining
     */
    fun valueRange(range: ClosedFloatingPointRange<Double>): AttributeTypeBuilder

    /**
     * Sets the default value of the attribute.
     *
     * @param default The default value
     * @return This builder, for chaining
     */
    fun defaultValue(default: Double): AttributeTypeBuilder

    /**
     * Sets data holder support checker.
     *
     * By default is every data holder supported.
     *
     * @param supports The supports checker
     * @return This builder, for chaining
     */
    fun supports(supports: (DataHolder) -> Boolean): AttributeTypeBuilder
}
