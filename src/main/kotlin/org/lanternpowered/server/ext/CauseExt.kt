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
@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package org.lanternpowered.server.ext

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.cause.entity.health.HealingType
import org.lanternpowered.server.cause.entity.healing.LanternHealingType

inline fun HealingType(key: ResourceKey): HealingType = LanternHealingType(key)
