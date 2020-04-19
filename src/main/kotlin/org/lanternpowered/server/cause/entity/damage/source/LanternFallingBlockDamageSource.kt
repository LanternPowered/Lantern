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

import org.lanternpowered.api.value.immutableValueOf
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.entity.FallingBlock
import org.spongepowered.api.event.cause.entity.damage.source.FallingBlockDamageSource

internal class LanternFallingBlockDamageSource(builder: LanternFallingBlockDamageSourceBuilder) :
        LanternEntityDamageSource(builder), FallingBlockDamageSource {

    internal val canPlace = builder.canPlace ?: this.source.require(Keys.CAN_PLACE_AS_BLOCK)
    internal val fallTime = builder.fallTime ?: this.source.require(Keys.FALL_TIME)
    internal val hurtsEnemies = builder.hurtsEnemies ?: this.source.require(Keys.CAN_HURT_ENTITIES)
    internal val maxDamage = builder.maxDamage ?: this.source.require(Keys.MAX_FALL_DAMAGE)
    internal val damagePerBlock = builder.damagePerBlock ?: this.source.require(Keys.DAMAGE_PER_BLOCK)
    internal val canDropAsItem = builder.canDropAsItem ?: this.source.require(Keys.CAN_DROP_AS_ITEM)

    private val blockStateValue: Value.Immutable<BlockState> = this.source.blockState().asImmutable()
    private val canPlaceValue by lazy { immutableValueOf(Keys.CAN_PLACE_AS_BLOCK, this.canPlace) }
    private val fallTimeValue by lazy { immutableValueOf(Keys.FALL_TIME, this.fallTime) }
    private val hurtsEnemiesValue by lazy { immutableValueOf(Keys.CAN_HURT_ENTITIES, this.hurtsEnemies) }
    private val maxDamageValue by lazy { immutableValueOf(Keys.MAX_FALL_DAMAGE, this.maxDamage) }
    private val damagePerBlockValue by lazy { immutableValueOf(Keys.DAMAGE_PER_BLOCK, this.damagePerBlock) }
    private val canDropAsItemValue by lazy { immutableValueOf(Keys.CAN_DROP_AS_ITEM, this.canDropAsItem) }

    override fun blockState() = this.blockStateValue
    override fun fallDamagePerBlock() = this.damagePerBlockValue
    override fun maxFallDamage() = this.maxDamageValue
    override fun canPlaceAsBlock() = this.canPlaceValue
    override fun canDropAsItem() = this.canDropAsItemValue
    override fun fallTime() = this.fallTimeValue
    override fun canHurtEntities() = this.hurtsEnemiesValue

    override fun getSource() = super.getSource() as FallingBlock
}
