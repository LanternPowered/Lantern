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
package org.lanternpowered.server.cause.entity.damage.source

import org.spongepowered.api.entity.Entity
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource
import org.spongepowered.api.event.cause.entity.damage.source.common.AbstractDamageSourceBuilder

@Suppress("UNCHECKED_CAST")
abstract class AbstractEntityDamageSourceBuilder<T : EntityDamageSource, B : EntityDamageSource.EntityDamageSourceBuilder<T, B>> :
        AbstractDamageSourceBuilder<T, B>(), EntityDamageSource.EntityDamageSourceBuilder<T, B> {

    internal var source: Entity? = null

    override fun entity(entity: Entity): B = apply { this.source = entity } as B

    override fun reset(): B = apply {
        super.reset()
        this.source = null
    } as B

    override fun from(value: T): B = apply {
        super.from(value)
        this.source = value.source
    } as B
}
