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
import org.spongepowered.api.entity.FallingBlock
import org.spongepowered.api.event.cause.entity.damage.source.FallingBlockDamageSource

class LanternFallingBlockDamageSourceBuilder : AbstractEntityDamageSourceBuilder<FallingBlockDamageSource, FallingBlockDamageSource.Builder>(),
        FallingBlockDamageSource.Builder {

    internal var canPlace: Boolean? = null
    internal var fallTime: Int? = null
    internal var hurtsEnemies: Boolean? = null
    internal var maxDamage: Double? = null
    internal var damagePerBlock: Double? = null
    internal var canDropAsItem: Boolean? = null

    override fun entity(entity: Entity): LanternFallingBlockDamageSourceBuilder {
        check(entity is FallingBlock) { "Entity source must be a falling block and not ${entity::class.java.simpleName}" }
        return super.entity(entity) as LanternFallingBlockDamageSourceBuilder
    }

    override fun places(canPlace: Boolean) = apply { this.canPlace = canPlace }
    override fun fallTime(time: Int) = apply { this.fallTime = time }
    override fun hurtsEntities(hurts: Boolean) = apply { this.hurtsEnemies = hurts }
    override fun maxDamage(damage: Double) = apply { this.maxDamage = damage }
    override fun damagePerBlock(damagePer: Double) = apply { this.damagePerBlock = damagePer }

    override fun from(value: FallingBlockDamageSource) = apply {
        super.from(value)
        value as LanternFallingBlockDamageSource
        this.canPlace = value.canPlace
        this.fallTime = value.fallTime
        this.hurtsEnemies = value.hurtsEnemies
        this.maxDamage = value.maxDamage
        this.damagePerBlock = value.damagePerBlock
        this.canDropAsItem = value.canDropAsItem
    }

    override fun reset(): FallingBlockDamageSource.Builder = apply {
        super.reset()
        this.canPlace = null
        this.fallTime = null
        this.hurtsEnemies = null
        this.maxDamage = null
        this.damagePerBlock = null
        this.canDropAsItem = null
    }

    override fun build(): FallingBlockDamageSource {
        checkNotNull(this.source) { "The falling block entity must be set" }
        return LanternFallingBlockDamageSource(this)
    }
}
