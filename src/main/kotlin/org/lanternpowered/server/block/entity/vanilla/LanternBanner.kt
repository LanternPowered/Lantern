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
package org.lanternpowered.server.block.entity.vanilla

import org.lanternpowered.server.block.entity.LanternBlockEntity
import org.lanternpowered.server.network.block.BlockEntityProtocolTypes
import org.spongepowered.api.block.entity.Banner
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.type.DyeColors

class LanternBanner : LanternBlockEntity(), Banner {

    init {
        this.protocolType = BlockEntityProtocolTypes.BANNER
    }

    public override fun registerKeys() {
        super.registerKeys()

        keyRegistry {
            register(Keys.DYE_COLOR, DyeColors.WHITE)
            register(Keys.BANNER_PATTERN_LAYERS, mutableListOf())
        }
    }
}
