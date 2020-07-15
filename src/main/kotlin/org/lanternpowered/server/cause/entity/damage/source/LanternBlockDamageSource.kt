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

import org.lanternpowered.api.world.Location
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.event.cause.entity.damage.source.BlockDamageSource

internal class LanternBlockDamageSource(
        builder: LanternBlockDamageSourceBuilder,
        private val location: Location,
        private val blockSnapshot: BlockSnapshot
) : LanternDamageSource(builder), BlockDamageSource {

    override fun getLocation(): Location = this.location
    override fun getBlockSnapshot(): BlockSnapshot = this.blockSnapshot
}
