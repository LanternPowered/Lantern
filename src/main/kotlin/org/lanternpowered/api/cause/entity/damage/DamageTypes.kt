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
    val ATTACK: DamageType by CatalogRegistry.provide("ATTACK")
    val CONTACT: DamageType by CatalogRegistry.provide("CONTACT")
    val CUSTOM: DamageType by CatalogRegistry.provide("CUSTOM")
    val DROWN: DamageType by CatalogRegistry.provide("DROWN")
    val DRYOUT: DamageType by CatalogRegistry.provide("DRYOUT")
    val EXPLOSIVE: DamageType by CatalogRegistry.provide("EXPLOSIVE")
    val FALL: DamageType by CatalogRegistry.provide("FALL")
    val FIRE: DamageType by CatalogRegistry.provide("FIRE")
    val GENERIC: DamageType by CatalogRegistry.provide("GENERIC")
    val HUNGER: DamageType by CatalogRegistry.provide("HUNGER")
    val MAGIC: DamageType by CatalogRegistry.provide("MAGIC")
    val MAGMA: DamageType by CatalogRegistry.provide("MAGMA")
    val PROJECTILE: DamageType by CatalogRegistry.provide("PROJECTILE")
    val SUFFOCATE: DamageType by CatalogRegistry.provide("SUFFOCATE")
    val SWEEPING_ATTACK: DamageType by CatalogRegistry.provide("SWEEPING_ATTACK")
    val POISON: DamageType by CatalogRegistry.provide("POISON")
    val LIGHTNING: DamageType by CatalogRegistry.provide("LIGHTNING")
    val WITHER: DamageType by CatalogRegistry.provide("WITHER")
    val VOID: DamageType by CatalogRegistry.provide("VOID")
}
