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
