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
package org.lanternpowered.server.cause.entity.healing.source

import org.spongepowered.api.event.cause.entity.health.source.EntityHealingSource
import org.spongepowered.api.event.cause.entity.health.source.common.AbstractEntityHealingSourceBuilder

class LanternEntityHealingSourceBuilder : AbstractEntityHealingSourceBuilder<EntityHealingSource, EntityHealingSource.Builder>(),
        EntityHealingSource.Builder {

    override fun build(): EntityHealingSource {
        checkNotNull(healingType) { "The healing type must be set" }
        checkNotNull(entity) { "The entity must be set" }
        return LanternEntityHealingSource(this)
    }
}
