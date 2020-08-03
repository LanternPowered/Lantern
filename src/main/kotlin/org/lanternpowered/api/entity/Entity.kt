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
package org.lanternpowered.api.entity

import org.lanternpowered.api.world.Location

typealias Entity = org.spongepowered.api.entity.Entity
typealias EntityType<T> = org.spongepowered.api.entity.EntityType<T>
typealias EntityTypes = org.spongepowered.api.entity.EntityTypes

interface ExtendedEntity : Entity {

    override fun getLocation(): Location
}
