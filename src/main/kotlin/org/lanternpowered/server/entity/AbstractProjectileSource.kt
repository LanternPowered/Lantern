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
package org.lanternpowered.server.entity

import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.projectile.Projectile
import org.spongepowered.api.projectile.source.ProjectileSource
import org.spongepowered.api.world.Locatable
import org.spongepowered.math.vector.Vector3d
import java.util.*

interface AbstractProjectileSource : Locatable, ProjectileSource {

    @JvmDefault
    override fun <T : Projectile> launchProjectileTo(projectileClass: Class<T>, target: Entity): Optional<T> {
        TODO("Not yet implemented")
    }

    @JvmDefault
    override fun <T : Projectile> launchProjectile(projectileClass: Class<T>): Optional<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @JvmDefault
    override fun <T : Projectile> launchProjectile(projectileClass: Class<T>, velocity: Vector3d): Optional<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
