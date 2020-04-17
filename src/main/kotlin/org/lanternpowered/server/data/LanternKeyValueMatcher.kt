/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
