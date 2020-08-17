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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.server.block.LanternBlockType
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.block.entity.BlockEntityType

class LanternBlockEntityType<E : BlockEntity>(
        key: NamespacedKey,
        private val constructor: (BlockEntityCreationData) -> E
) : DefaultCatalogType(key), BlockEntityType {

    private val creationData = BlockEntityCreationData(this)

    /**
     * Constructs a new [BlockEntity].
     */
    fun constructBlockEntity() = this.constructor(this.creationData)

    override fun isValidBlock(block: BlockType): Boolean {
        block as LanternBlockType
        return block.blockEntityType == this
    }
}
