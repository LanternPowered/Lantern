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

/**
 * Represents a type of "healing", used for [HealEntityEvent]s.
 */
@CatalogedBy(HealingTypes::class)
interface HealingType : CatalogType
