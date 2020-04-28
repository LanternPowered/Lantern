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

import org.lanternpowered.api.attribute.AttributeModifier
import org.lanternpowered.api.attribute.AttributeModifierBuilder
import org.lanternpowered.api.attribute.AttributeOperation
import java.util.UUID

class LanternAttributeModifierBuilder : AttributeModifierBuilder {

    private var value: Double? = null
    private var operation: AttributeOperation? = null
    private var uniqueId: UUID? = null

    override fun value(value: Double): AttributeModifierBuilder = apply { this.value = value }
    override fun uniqueId(uniqueId: UUID): AttributeModifierBuilder = apply { this.uniqueId = uniqueId }
    override fun operation(operation: AttributeOperation): AttributeModifierBuilder = apply { this.operation = operation }

    override fun reset(): LanternAttributeModifierBuilder = apply {
        this.value = null
        this.operation = null
        this.uniqueId = null
    }

    override fun build(): AttributeModifier {
        val operation = checkNotNull(this.operation) { "The operation is not set" }
        val value = checkNotNull(this.value) { "The value is not set" }
        val uniqueId = this.uniqueId ?: UUID.randomUUID()
        return LanternAttributeModifier(uniqueId, operation, value)
    }
}
