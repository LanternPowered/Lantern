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
package org.lanternpowered.server.entity

import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextRepresentable
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityType
import java.util.UUID

class LanternEntityType<E : Entity> internal constructor(
        key: NamespacedKey,
        private val text: Text,
        private val constructor: (EntityCreationData) -> E
) : DefaultCatalogType(key), EntityType<E>, TextRepresentable by text {

    /**
     * Constructs a new [Entity] instance with the given [UUID].
     */
    fun constructEntity(uniqueId: UUID = UUID.randomUUID()): E =
            this.constructor(EntityCreationData(uniqueId, this))
}
