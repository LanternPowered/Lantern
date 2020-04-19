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
package org.lanternpowered.server.data

import org.lanternpowered.server.data.key.ValueKey
import org.spongepowered.api.data.KeyValueMatcher
import org.spongepowered.api.data.value.Value
import java.util.Optional

data class LanternKeyValueMatcher<V : Any>(
        private val key: ValueKey<out Value<V>, V>,
        private val operator: KeyValueMatcher.Operator,
        private val value: V?
) : AbstractDataSerializable(), KeyValueMatcher<V> {

    override fun getKey() = this.key
    override fun getOperator() = this.operator
    override fun getValue() = Optional.ofNullable(this.value)

    override fun getContentVersion() = 1

    override fun matches(value: V?): Boolean {
        return when (this.operator) {
            KeyValueMatcher.Operator.EQUAL -> compare(value) == 0
            KeyValueMatcher.Operator.NOT_EQUAL -> compare(value) != 0
            KeyValueMatcher.Operator.GREATER -> compare(value) > 0
            KeyValueMatcher.Operator.GREATER_OR_EQUAL -> compare(value) >= 0
            KeyValueMatcher.Operator.LESS -> compare(value) < 0
            KeyValueMatcher.Operator.LESS_OR_EQUAL -> compare(value) <= 0
            KeyValueMatcher.Operator.INCLUDES -> includes(value)
            KeyValueMatcher.Operator.EXCLUDES -> !includes(value)
            else -> throw IllegalStateException("Unknown operator: $operator")
        }
    }

    private fun includes(value: V?) = if (this.value == null || value == null) false else this.key.elementIncludesTester.test(this.value, value)

    private fun compare(value: V?): Int {
        return when {
            this.value == null && value == null -> 0
            this.value != null && value == null -> 1
            this.value == null -> -1
            else -> -this.key.elementComparator.compare(this.value, value)
        }
    }
}
