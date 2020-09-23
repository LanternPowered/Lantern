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
package org.lanternpowered.server.network.entity.parameter

import java.util.ArrayList

class ParameterTypeCollection private constructor(
        private val parameterTypes: MutableList<ParameterType<*>>
) {

    constructor() : this(ArrayList())

    /**
     * Copies this [ParameterTypeCollection].
     */
    fun copy(): ParameterTypeCollection = ParameterTypeCollection(ArrayList(this.parameterTypes))

    /**
     * Creates a new [ParameterType].
     *
     * @param valueType The parameter value type
     * @param T The value type
     * @return The parameter type
     */
    fun <T> create(valueType: ParameterValueType<T>): ParameterType<T> =
            ParameterType(this.parameterTypes.size, valueType).also { this.parameterTypes.add(it) }
}
