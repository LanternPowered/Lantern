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

import org.spongepowered.api.event.cause.entity.health.source.HealingSource
import org.spongepowered.api.event.cause.entity.health.source.common.AbstractHealingSourceBuilder

class LanternHealingSourceBuilder : AbstractHealingSourceBuilder<HealingSource, HealingSource.Builder>(), HealingSource.Builder {

    override fun build(): HealingSource {
        checkNotNull(healingType) { "The healing type must be set" }
        return LanternHealingSource(this)
    }
}
