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
package org.lanternpowered.api.cause.entity.health

import org.spongepowered.api.CatalogType
import org.spongepowered.api.util.annotation.CatalogedBy
import org.lanternpowered.api.cause.Cause

/**
 * A type of [HealthModifier] that can apply a "grouping" so to speak
 * for the damage modifier. The use case is being able to differentiate between
 * various [HealthModifier]s based on the [HealthModifierType]
 * without digging through the [Cause] provided by
 * [HealthModifier.cause].
 */
@CatalogedBy(HealthModifierTypes::class)
interface HealthModifierType : CatalogType
