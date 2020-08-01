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

import org.lanternpowered.api.block.BlockType
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.server.behavior.Behavior
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.data.LocalImmutableDataHolder
import org.lanternpowered.server.data.LocalKeyRegistry
import org.lanternpowered.server.item.appearance.ItemAppearance
import java.util.Optional

class LanternItemType(
        key: NamespacedKey,
        val nameFunction: ItemStack.() -> Text,
        private val blockType: BlockType?,
        private val maxStackQuantity: Int,
        override val keyRegistry: LocalKeyRegistry<ItemType>,
        val stackKeyRegistry: LocalKeyRegistry<ItemStack>,
        val behaviorPipeline: BehaviorPipeline<Behavior>,
        val appearance: ItemAppearance? = null // TODO: When custom item types get implemented,
) : DefaultCatalogType(key), ItemType, LocalImmutableDataHolder<ItemType> {

    private val name by lazy { this.nameFunction(ItemStack.of(this)) }

    override fun asComponent(): Text = this.name
    override fun getBlock() = this.blockType.asOptional()
    override fun getMaxStackQuantity() = this.maxStackQuantity

    override fun getContainer(): Optional<ItemType> {
        TODO("Not yet implemented")
    }
}
