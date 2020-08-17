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
package org.lanternpowered.server.block.entity

import org.spongepowered.api.block.entity.BlockEntityType

/**
 * Represents the information that is used to construct
 * a block entity instance.
 */
data class BlockEntityCreationData(
        val type: BlockEntityType
)
