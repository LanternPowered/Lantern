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
import org.lanternpowered.api.attribute.AttributeOperation
import java.util.UUID

data class LanternAttributeModifier(
        private val uniqueId: UUID,
        override val operation: AttributeOperation,
        override val value: Double
) : AttributeModifier {
    override fun getUniqueId(): UUID = this.uniqueId
}
