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

import com.google.common.reflect.TypeToken
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.event.cause.EventContextKey

class LanternEventContextKey<T>(
        key: ResourceKey, private val typeToken: TypeToken<T>
) : DefaultCatalogType(key), EventContextKey<T> {

    override fun getAllowedType(): TypeToken<T> = this.typeToken

    override fun toStringHelper() = super.toStringHelper()
            .add("allowedType", this.typeToken)
}
