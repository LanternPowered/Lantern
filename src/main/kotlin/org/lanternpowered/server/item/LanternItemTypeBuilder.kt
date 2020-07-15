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

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.text.translation.FixedTranslation
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.server.behavior.Behavior
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline
import org.lanternpowered.server.behavior.pipeline.impl.MutableBehaviorPipelineImpl
import org.lanternpowered.server.data.LocalKeyRegistry
import org.lanternpowered.server.text.translation.TranslationHelper.tr
import org.spongepowered.api.block.BlockType

class LanternItemTypeBuilder : ItemTypeBuilder {

    private var nameFunction: (ItemStack.() -> Translation)? = null
    private var maxStackQuantity = 64
    private val keysFunctions = mutableListOf<LocalKeyRegistry<ItemType>.() -> Unit>()
    private val stackKeysFunctions = mutableListOf<LocalKeyRegistry<ItemStack>.() -> Unit>()
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

    override fun keys(fn: LocalKeyRegistry<ItemType>.() -> Unit) {
        this.keysFunctions += fn
    }

    override fun stackKeys(fn: LocalKeyRegistry<ItemStack>.() -> Unit) {
        this.stackKeysFunctions += fn
    }

    override fun behaviors(fn: MutableBehaviorPipeline<Behavior>.() -> Unit) {
        this.behaviorsBuilderFunctions += fn
    }

    fun build(key: ResourceKey): ItemType {
        var nameFunction = this.nameFunction
        if (nameFunction == null) {
            val def = tr("item.${key.namespace}.${key.value}")
            nameFunction = { def }
        }

        val keyRegistry = LocalKeyRegistry.of<ItemType>()
        for (fn in this.keysFunctions) {
            keyRegistry.fn()
        }

        // Already create the key registry, this can be copied
        // to every item stack later, instead of reapplying every function
        val stackKeyRegistry = LocalKeyRegistry.of<ItemStack>()
        for (fn in this.stackKeysFunctions) {
            stackKeyRegistry.fn()
        }

        val behaviorPipeline = MutableBehaviorPipelineImpl(Behavior::class.java, mutableListOf())
        for (fn in this.behaviorsBuilderFunctions) {
            behaviorPipeline.fn()
        }

        return LanternItemType(key, nameFunction, this.blockType, this.maxStackQuantity,
                stackKeyRegistry, behaviorPipeline, keyRegistry.forHolderUnchecked())
    }
}
