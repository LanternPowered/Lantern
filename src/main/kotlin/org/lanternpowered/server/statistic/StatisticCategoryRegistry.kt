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
package org.lanternpowered.server.statistic

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.api.util.type.typeTokenOf
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.lanternpowered.server.text.translation.TranslationHelper.tr
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.statistic.StatisticCategories
import org.spongepowered.api.statistic.StatisticCategory

object StatisticCategoryRegistry : AdditionalPluginCatalogRegistryModule<StatisticCategory>(StatisticCategories::class) {

    override fun registerDefaults() {
        register(LanternStatisticCategory(NamespacedKey.minecraft("custom"), tr("Custom")))

        register<BlockType>("blocks_broken", "Blocks Broken")
        register<EntityType<*>>("entities_killed", "Entities Killed")
        register<ItemType>("items_broken", "Items Broken")
        register<ItemType>("items_crafted", "Items Crafted")
        register<ItemType>("items_dropped", "Items Dropped")
        register<ItemType>("items_picked_up", "Items Picked Up")
        register<ItemType>("items_used", "Items Used")
        register<EntityType<*>>("killed_by_entities", "Killed By Entities")
    }

    private inline fun <reified C : CatalogType> register(id: String, name: String) {
        register<C>(NamespacedKey.minecraft(id), tr(name))
    }

    private inline fun <reified C : CatalogType> register(key: NamespacedKey, name: Translation) {
        register(LanternStatisticCategoryForCatalogType<C>(key, name, typeTokenOf()))
    }
}
