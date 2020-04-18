/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
