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
import org.lanternpowered.server.data.SpongeKeys
import org.spongepowered.api.data.value.Value
import org.spongepowered.math.vector.Vector3d

typealias Entity = org.spongepowered.api.entity.Entity
typealias EntityType<T> = org.spongepowered.api.entity.EntityType<T>
typealias EntityTypes = org.spongepowered.api.entity.EntityTypes

interface ExtendedEntity : Entity {

    override fun getLocation(): Location

    @JvmDefault
    @Deprecated("Use lantern Keys instead.",
            ReplaceWith("this.requireValue(SpongeKeys.VELOCITY).asMutable()", "org.spongepowered.api.data.Keys"))
    override fun velocity(): Value.Mutable<Vector3d> = this.requireValue(SpongeKeys.VELOCITY).asMutable()
}
