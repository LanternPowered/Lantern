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

/**
 * Constructs a new [AttributeOperationFunction].
 */
inline fun attributeOperationFunction(
        crossinline fn: (base: Double, modifier: Double, currentValue: Double) -> Double
): AttributeOperationFunction = object : AttributeOperationFunction {
    override fun getIncrementation(base: Double, modifier: Double, currentValue: Double): Double = fn(base, modifier, currentValue)
}

/**
 * Represents a function used by an [AttributeModifier] to modify the
 * value of an [Attribute].
 */
interface AttributeOperationFunction {

    /**
     * Gets the amount the [Attribute] should be incremented when this
     * operation function is applied to it.
     *
     * @param base The base value of the Attribute
     * @param modifier The modifier to modify the Attribute with
     * @param currentValue The current value of the Attribute
     * @return The amount the Attribute should be incremented when this modifier
     * is applied to it
     */
    fun getIncrementation(base: Double, modifier: Double, currentValue: Double): Double
}
