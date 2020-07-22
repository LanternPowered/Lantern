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
import org.lanternpowered.server.text.translation.TranslationHelper.tr
import org.lanternpowered.api.namespace.NamespacedKey
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.text.translation.Translation
import java.util.UUID

class LanternEntityType<E : Entity> internal constructor(
        key: NamespacedKey,
        private val translation: Translation,
        private val constructor: (UUID) -> E
) : DefaultCatalogType(key), EntityType<E> {

    internal constructor(key: NamespacedKey, translation: String, entityConstructor: (UUID) -> E) :
            this(key, tr(translation), entityConstructor)

    fun constructEntity(uniqueId: UUID) = this.constructor(uniqueId)

    override fun getTranslation() = this.translation
}
