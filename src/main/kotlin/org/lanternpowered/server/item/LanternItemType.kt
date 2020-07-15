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
package org.lanternpowered.server.item

import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.server.behavior.Behavior
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.data.LocalImmutableDataHolder
import org.lanternpowered.server.data.LocalKeyRegistry
import org.lanternpowered.server.item.appearance.ItemAppearance
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.block.BlockType
import java.util.Optional

class LanternItemType(
        key: ResourceKey,
        val nameFunction: ItemStack.() -> Translation,
        private val blockType: BlockType?,
        private val maxStackQuantity: Int,
        private val stackKeyRegistry: LocalKeyRegistry<ItemStack>,
        override val keyRegistry: LocalKeyRegistry<out LocalImmutableDataHolder<ItemType>>,
        val behaviorPipeline: BehaviorPipeline<Behavior>,
        val appearance: ItemAppearance? = null // TODO: When custom item types get implemented,
) : DefaultCatalogType(key), ItemType, LocalImmutableDataHolder<ItemType> {

    private val name by lazy { this.nameFunction(ItemStack.of(this)) }

    override fun getTranslation() = this.name
    override fun getBlock() = this.blockType.optional()
    override fun getMaxStackQuantity() = this.maxStackQuantity

    override fun getContainer(): Optional<ItemType> {
        TODO("Not yet implemented")
    }
}
