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

object LanternAttributeCalculator {

    fun calculateValue(base: Double, modifiers: Collection<LanternAttributeModifier>): Double {
        @Suppress("NAME_SHADOWING")
        val modifiers: List<LanternAttributeModifier> = modifiers
                .sortedBy { it.operation }

        // The last operation
        var lastOperation: LanternOperation? = null

        // The new value
        var value = base
        var value0 = 0.0

        // Add the incrementations of all the modifiers
        for (modifier in modifiers) {
            val operation = modifier.operation
            if (lastOperation == null || operation == lastOperation) {
                val value1 = operation.getIncrementation(base, modifier.value, value)
                if (operation.changeValueImmediately()) {
                    value += value1
                } else {
                    value0 += value1
                }
            } else {
                lastOperation = operation
                value += value0
                value0 = 0.0
            }
        }
        value += value0
        return value
    }
}
