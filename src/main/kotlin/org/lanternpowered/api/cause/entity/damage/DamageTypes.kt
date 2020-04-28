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
package org.lanternpowered.api.cause.entity.damage

import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.provide

/**
 * All the known damage types.
 */
object DamageTypes {
    val ATTACK: DamageType = CatalogRegistry.provide("ATTACK")
    val CONTACT: DamageType = CatalogRegistry.provide("CONTACT")
    val CUSTOM: DamageType = CatalogRegistry.provide("CUSTOM")
    val DROWN: DamageType = CatalogRegistry.provide("DROWN")
    val DRYOUT: DamageType = CatalogRegistry.provide("DRYOUT")
    val EXPLOSIVE: DamageType = CatalogRegistry.provide("EXPLOSIVE")
    val FALL: DamageType = CatalogRegistry.provide("FALL")
    val FIRE: DamageType = CatalogRegistry.provide("FIRE")
    val GENERIC: DamageType = CatalogRegistry.provide("GENERIC")
    val HUNGER: DamageType = CatalogRegistry.provide("HUNGER")
    val MAGIC: DamageType = CatalogRegistry.provide("MAGIC")
    val MAGMA: DamageType = CatalogRegistry.provide("MAGMA")
    val PROJECTILE: DamageType = CatalogRegistry.provide("PROJECTILE")
    val SUFFOCATE: DamageType = CatalogRegistry.provide("SUFFOCATE")
    val SWEEPING_ATTACK: DamageType = CatalogRegistry.provide("SWEEPING_ATTACK")
    val POISON: DamageType = CatalogRegistry.provide("POISON")
    val LIGHTNING: DamageType = CatalogRegistry.provide("LIGHTNING")
    val WITHER: DamageType = CatalogRegistry.provide("WITHER")
    val VOID: DamageType = CatalogRegistry.provide("VOID")
}
