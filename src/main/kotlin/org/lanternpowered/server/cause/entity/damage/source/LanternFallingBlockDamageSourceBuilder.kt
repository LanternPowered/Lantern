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

import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableFallingBlockData
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.FallingBlock
import org.spongepowered.api.event.cause.entity.damage.source.FallingBlockDamageSource

class LanternFallingBlockDamageSourceBuilder : AbstractEntityDamageSourceBuilder<FallingBlockDamageSource, FallingBlockDamageSource.Builder>(),
        FallingBlockDamageSource.Builder {

    internal var fallingBlockData: ImmutableFallingBlockData? = null

    override fun entity(entity: Entity): LanternFallingBlockDamageSourceBuilder {
        check(entity is FallingBlock) { "Entity source must be a falling block and not ${entity::class.java.simpleName}" }
        return super.entity(entity) as LanternFallingBlockDamageSourceBuilder
    }

    override fun fallingBlock(fallingBlockData: ImmutableFallingBlockData): FallingBlockDamageSource.Builder =
            apply { this.fallingBlockData = fallingBlockData }

    override fun from(value: FallingBlockDamageSource): FallingBlockDamageSource.Builder = apply {
        super.from(value)
        this.fallingBlockData = value.fallingBlockData
    }

    override fun reset(): FallingBlockDamageSource.Builder = apply {
        super.reset()
        this.fallingBlockData = null
    }

    override fun build(): FallingBlockDamageSource {
        checkNotNull(this.source) { "The falling block entity must be set" }
        return LanternFallingBlockDamageSource(this)
    }
}
