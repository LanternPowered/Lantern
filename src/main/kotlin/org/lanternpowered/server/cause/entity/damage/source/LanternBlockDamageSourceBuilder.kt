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
