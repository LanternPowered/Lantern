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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.text.translation.FixedTranslation
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.server.behavior.Behavior
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline
import org.lanternpowered.server.behavior.pipeline.impl.MutableBehaviorPipelineImpl
import org.lanternpowered.server.data.LocalKeyRegistry
import org.lanternpowered.server.data.property.PropertyRegistry
import org.spongepowered.api.block.BlockType

class LanternItemTypeBuilder : ItemTypeBuilder {

    private var nameFunction: (ItemStack.() -> Translation)? = null
    private var maxStackQuantity = 64
    private val propertiesBuilderFunctions = mutableListOf<ItemTypePropertyRegistryBuilder.() -> Unit>()
    private val valueKeysFunctions = mutableListOf<LocalKeyRegistry<ItemStack>.() -> Unit>()
    private val behaviorsBuilderFunctions = mutableListOf<MutableBehaviorPipeline<Behavior>.() -> Unit>()

    /**
     * The block type bound to this item type, if any.
     */
    internal var blockType: BlockType? = null

    override fun name(fn: ItemStack.() -> Translation) {
        this.nameFunction = fn
    }

    override fun name(name: String) {
        name(FixedTranslation(name))
    }

    override fun name(name: Translation) {
        name { name }
    }

    override fun maxStackQuantity(quantity: Int) {
        check(quantity > 0) { "The max stack quantity must be greater than 0" }
        this.maxStackQuantity = quantity
    }

    override fun properties(fn: ItemTypePropertyRegistryBuilder.() -> Unit) {
        this.propertiesBuilderFunctions += fn
    }

    override fun valueKeys(fn: LocalKeyRegistry<ItemStack>.() -> Unit) {
        this.valueKeysFunctions += fn
    }

    override fun behaviors(fn: MutableBehaviorPipeline<Behavior>.() -> Unit) {
        this.behaviorsBuilderFunctions += fn
    }

    fun build(key: CatalogKey): ItemType {
        var nameFunction = this.nameFunction
        if (nameFunction == null) {
            val unknown = FixedTranslation("Unknown")
            nameFunction = { unknown }
        }

        val properties = PropertyRegistry.of<ItemType>()

        val builder = LanternItemTypePropertyRegistryBuilder(properties)
        for (fn in this.propertiesBuilderFunctions) {
            builder.fn()
        }

        // Already create the key registry, this can be copied
        // to every item stack later, instead of reapplying every function
        val valueKeyRegistry = LocalKeyRegistry.of<ItemStack>()
        for (fn in this.valueKeysFunctions) {
            valueKeyRegistry.fn()
        }

        val behaviorPipeline = MutableBehaviorPipelineImpl(Behavior::class.java, mutableListOf())
        for (fn in this.behaviorsBuilderFunctions) {
            behaviorPipeline.fn()
        }

        return LanternItemType(key, nameFunction, this.blockType, this.maxStackQuantity,
                valueKeyRegistry, behaviorPipeline, properties.forHolderUnchecked())
    }
}
