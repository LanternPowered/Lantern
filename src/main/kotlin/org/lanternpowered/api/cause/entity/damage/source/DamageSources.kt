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
package org.lanternpowered.api.cause.entity.damage.source

import org.lanternpowered.api.cause.entity.damage.DamageTypes
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources


/**
 * A static collection of various [DamageSource]s that remain static, or
 * otherwise "ambiguous" with regards to the actual source. Examples include:
 * in the event an [Entity] is being damaged due to falling through the
 * "void", an [Entity] being damaged for being "on fire" in which case
 * an [Keys.FIRE_TICKS] may be present from the [Entity], etc.
 *
 * [DamageSource]s that rely on live instances of various objects,
 * including other [Entity] instances, or a block at a specific
 * [Location] rely on the various other types of [DamageSource]s.
 */
object DamageSources {
    val DROWNING: DamageSource = DamageSources.DROWNING.get()
    val DRYOUT: DamageSource = DamageSources.DRYOUT.get()
    val FALLING: DamageSource = DamageSources.FALLING.get()
    val FIRE_TICK: DamageSource = DamageSources.FIRE_TICK.get()
    val GENERIC: DamageSource = DamageSources.GENERIC.get()
    val MAGIC: DamageSource = DamageSources.MAGIC.get()
    val STARVATION: DamageSource = DamageSources.STARVATION.get()
    val VOID: DamageSource = DamageSource.builder().type(DamageTypes.VOID).bypassesArmor().creative().build()
    val WITHER: DamageSource = DamageSource.builder().type(DamageTypes.WITHER).magical().bypassesArmor().build()
    val POISON: DamageSource = DamageSource.builder().type(DamageTypes.POISON).bypassesArmor().build()
}
