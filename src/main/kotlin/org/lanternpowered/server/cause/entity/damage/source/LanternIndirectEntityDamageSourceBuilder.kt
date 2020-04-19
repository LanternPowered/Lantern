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
package org.lanternpowered.server.cause.entity.damage.source

import org.spongepowered.api.entity.Entity
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource

class LanternIndirectEntityDamageSourceBuilder : AbstractEntityDamageSourceBuilder<IndirectEntityDamageSource, IndirectEntityDamageSource.Builder>(),
        IndirectEntityDamageSource.Builder {

    internal var indirect: Entity? = null

    override fun proxySource(proxy: Entity): IndirectEntityDamageSource.Builder = apply { this.indirect = proxy }

    override fun from(value: IndirectEntityDamageSource): IndirectEntityDamageSource.Builder = apply {
        super.from(value)
        this.indirect = value.indirectSource
    }

    override fun reset(): IndirectEntityDamageSource.Builder = apply {
        super.reset()
        this.indirect = null
    }

    override fun build(): IndirectEntityDamageSource {
        checkNotNull(this.damageType) { "The damage type must be set" }
        checkNotNull(this.source) { "The entity must be set" }
        checkNotNull(this.indirect) { "The proxy source must be set" }
        return LanternIndirectEntityDamageSource(this)
    }
}
