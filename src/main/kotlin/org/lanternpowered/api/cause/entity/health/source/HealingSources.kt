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
package org.lanternpowered.api.cause.entity.health.source

import org.lanternpowered.api.cause.entity.health.HealingTypes

/**
 * A static collection of various [HealingSource]s that remain static, or
 * otherwise "ambiguous" with regards to the actual source.
 */
object HealingSources {

    @JvmField val FOOD: HealingSource = healingSourceOf(HealingTypes.FOOD)
    @JvmField val GENERIC: HealingSource = healingSourceOf(HealingTypes.GENERIC)
    @JvmField val MAGIC: HealingSource = healingSource { type(HealingTypes.MAGIC).magical() }
}
