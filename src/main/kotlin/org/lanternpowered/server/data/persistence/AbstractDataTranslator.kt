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
package org.lanternpowered.server.data.persistence

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.data.persistence.DataTranslator

abstract class AbstractDataTranslator<T>(key: NamespacedKey, private val typeToken: TypeToken<T>) :
        DefaultCatalogType(key), DataTranslator<T> {

    override fun getToken() = this.typeToken
    override fun toStringHelper() = super.toStringHelper().add("typeToken", this.typeToken)
}
