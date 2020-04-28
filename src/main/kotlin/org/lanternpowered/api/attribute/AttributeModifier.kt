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

import org.lanternpowered.api.registry.BaseBuilder
import org.lanternpowered.api.util.Identifiable
import java.util.UUID

/**
 * Represents a modifier of an attribute.
 */
interface AttributeModifier : Identifiable {

    /**
     * The operation of the modifier.
     */
    val operation: AttributeOperation

    /**
     * The value of the modifier.
     */
    val value: Double
}

/**
 * A builder for [AttributeModifier]s.
 */
interface AttributeModifierBuilder : BaseBuilder<AttributeModifier, AttributeModifierBuilder> {

    /**
     * Sets the unique id.
     *
     * Defaults to a randomly generated [UUID].
     *
     * @param uniqueId The unique id
     * @return This builder, for chaining
     */
    fun uniqueId(uniqueId: UUID): AttributeModifierBuilder

    /**
     * Sets the attribute operation.
     *
     * @param operation The operation
     * @return This builder, for chaining
     */
    fun operation(operation: AttributeOperation): AttributeModifierBuilder

    /**
     * Sets the value.
     *
     * @param value The value
     * @return This builder, for chaining
     */
    fun value(value: Double): AttributeModifierBuilder

    /**
     * Builds the attribute modifier.
     *
     * @return The attribute modifier
     */
    fun build(): AttributeModifier
}
