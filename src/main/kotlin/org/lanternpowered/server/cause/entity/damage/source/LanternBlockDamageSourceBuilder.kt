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

import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.event.cause.entity.damage.source.BlockDamageSource
import org.spongepowered.api.event.cause.entity.damage.source.common.AbstractDamageSourceBuilder
import org.spongepowered.api.world.Location

class LanternBlockDamageSourceBuilder : AbstractDamageSourceBuilder<BlockDamageSource, BlockDamageSource.Builder>(), BlockDamageSource.Builder {

    private var location: Location? = null
    private var blockSnapshot: BlockSnapshot? = null

    override fun block(location: Location): BlockDamageSource.Builder = apply { this.location = location }
    override fun block(blockSnapshot: BlockSnapshot): BlockDamageSource.Builder = apply { this.blockSnapshot = blockSnapshot }

    override fun reset(): BlockDamageSource.Builder = apply {
        super.reset()
        this.location = null
        this.blockSnapshot = null
    }

    override fun from(value: BlockDamageSource): BlockDamageSource.Builder = apply {
        super.from(value)
        this.location = value.location
        this.blockSnapshot = value.blockSnapshot
    }

    override fun build(): BlockDamageSource {
        var location: Location? = this.location
        var blockSnapshot: BlockSnapshot? = this.blockSnapshot
        if (location == null && blockSnapshot != null) {
            location = blockSnapshot.location.orElse(null)
        } else if (location != null && blockSnapshot == null) {
            blockSnapshot = location.createSnapshot()
        }
        val location0 = checkNotNull(location) { "The location must be set" }
        val blockSnapshot0 = checkNotNull(blockSnapshot) { "The block snapshot must be set" }
        return LanternBlockDamageSource(this, location0, blockSnapshot0)
    }
}
