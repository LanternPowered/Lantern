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

import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource

class LanternEntityDamageSourceBuilder : AbstractEntityDamageSourceBuilder<EntityDamageSource, EntityDamageSource.Builder>(),
        EntityDamageSource.Builder {

    override fun build(): EntityDamageSource {
        checkNotNull(this.damageType) { "The damage type must be set" }
        checkNotNull(this.source) { "The entity must be set" }
        return LanternEntityDamageSource(this)
    }
}
