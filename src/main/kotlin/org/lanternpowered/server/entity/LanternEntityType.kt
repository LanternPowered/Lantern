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
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.text.translation.Translation
import java.util.UUID

class LanternEntityType<E : Entity> internal constructor(
        key: CatalogKey,
        private val translation: Translation,
        private val entityClass: Class<E>,
        private val entityConstructor: (UUID) -> E
) : DefaultCatalogType(key), EntityType<E> {

    internal constructor(key: CatalogKey, translation: String, entityClass: Class<E>, entityConstructor: (UUID) -> E) :
            this(key, tr(translation), entityClass, entityConstructor)

    fun constructEntity(uniqueId: UUID) = this.entityConstructor(uniqueId)

    override fun getEntityClass() = this.entityClass
    override fun toStringHelper() = super.toStringHelper().add("entityClass", this.entityClass)
    override fun getTranslation() = this.translation
}
