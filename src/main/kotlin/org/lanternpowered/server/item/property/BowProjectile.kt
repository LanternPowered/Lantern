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
package org.lanternpowered.server.item.property

import org.lanternpowered.api.item.inventory.ItemStack
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.entity.projectile.Projectile

data class BowProjectile<P : Projectile>(
        val entityType: EntityType<P>,
        val populator: P.(itemStack: ItemStack) -> Unit
)
