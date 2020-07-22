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
package org.lanternpowered.server.event

import org.lanternpowered.api.cause.CauseContextKey
import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.server.catalog.DefaultCatalogType

class LanternCauseContextKey<T>(
        key: NamespacedKey, private val typeToken: TypeToken<T>
) : DefaultCatalogType(key), CauseContextKey<T> {

    override fun getAllowedType(): TypeToken<T> = this.typeToken

    override fun toStringHelper() = super.toStringHelper()
            .add("allowedType", this.typeToken)
}
