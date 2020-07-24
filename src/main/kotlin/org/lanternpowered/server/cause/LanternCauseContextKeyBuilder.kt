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
package org.lanternpowered.server.cause

import com.google.common.reflect.TypeToken
import org.lanternpowered.server.catalog.AbstractCatalogBuilder
import org.lanternpowered.api.key.NamespacedKey
import org.spongepowered.api.event.cause.EventContextKey

class LanternCauseContextKeyBuilder<T> : AbstractCatalogBuilder<EventContextKey<T>, EventContextKey.Builder<T>>(), EventContextKey.Builder<T> {

    private var typeToken: TypeToken<T>? = null

    @Suppress("UNCHECKED_CAST")
    override fun <N : Any> type(allowedType: TypeToken<N>) = apply {
        this.typeToken = allowedType as TypeToken<T>
    } as LanternCauseContextKeyBuilder<N>

    override fun reset() = apply {
        super.reset()
        this.typeToken = null
    }

    override fun build(key: NamespacedKey): EventContextKey<T> {
        val typeToken = checkNotNull(this.typeToken) { "The allowed type must be set" }
        return LanternCauseContextKey(key, typeToken)
    }
}
