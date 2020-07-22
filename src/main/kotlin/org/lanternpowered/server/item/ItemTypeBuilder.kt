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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.data.Key
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.server.behavior.Behavior
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline
import org.lanternpowered.server.data.LocalKeyRegistry

/**
 * Constructs a new [ItemType].
 */
fun itemTypeOf(key: NamespacedKey, fn: ItemTypeBuilder.() -> Unit): ItemType {
    return LanternItemTypeBuilder().apply(fn).build(key)
}

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@DslMarker
annotation class ItemTypeBuilderDsl

@ItemTypeBuilderDsl
interface ItemTypeBuilder {

    fun name(name: String)
    fun name(name: Translation)
    fun name(fn: @ItemTypeBuilderDsl ItemStack.() -> Translation)

    /**
     * Sets the maximum stack quantity.
     */
    fun maxStackQuantity(quantity: Int)

    /**
     * Applies properties to the [ItemType].
     */
    fun keys(fn: @ItemTypeBuilderDsl LocalKeyRegistry<ItemType>.() -> Unit)

    /**
     * Applies behaviors to the [ItemType].
     */
    fun behaviors(fn: @ItemTypeBuilderDsl MutableBehaviorPipeline<Behavior>.() -> Unit)

    /**
     * Applies a function that can be used to register
     * [Key]s on [ItemStack]s of the built item type.
     */
    fun stackKeys(fn: @ItemTypeBuilderDsl LocalKeyRegistry<ItemStack>.() -> Unit)
}
