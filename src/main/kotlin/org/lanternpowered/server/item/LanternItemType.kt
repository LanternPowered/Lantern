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
package org.lanternpowered.server.item

import org.lanternpowered.api.ext.optional
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.server.behavior.Behavior
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.data.LocalKeyRegistry
import org.lanternpowered.server.data.property.LocalPropertyHolder
import org.lanternpowered.server.data.property.PropertyRegistry
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.block.BlockType

class LanternItemType(
        key: CatalogKey,
        private val nameFunction: ItemStack.() -> Translation,
        private val blockType: BlockType?,
        private val maxStackQuantity: Int,
        private val valueKeyRegistry: LocalKeyRegistry<ItemStack>,
        val behaviorPipeline: BehaviorPipeline<Behavior>,
        override val propertyRegistry: PropertyRegistry<out LocalPropertyHolder>
) : DefaultCatalogType(key), ItemType, LocalPropertyHolder {

    private val name by lazy { this.nameFunction(ItemStack.of(this)) }

    override fun getTranslation() = this.name
    override fun getBlock() = this.blockType.optional()
    override fun getMaxStackQuantity() = this.maxStackQuantity
}
