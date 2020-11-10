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
package org.lanternpowered.server.network.entity

import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.require
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.entity.player.LanternPlayer

object EntityProtocolTypes {
    val ARMOR_STAND: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("armor_stand"))
    val BAT: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("bat"))
    val CHICKEN: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("chicken"))
    val ENDER_DRAGON: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("ender_dragon"))
    val ENDERMITE: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("endermite"))
    val EXPERIENCE_ORB: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("experience_orb"))
    val GIANT: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("giant"))
    val HUMAN: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("human"))
    val HUSK: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("husk"))
    val IRON_GOLEM: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("iron_golem"))
    val ITEM: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("item"))
    val LIGHTNING: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("lightning"))
    val MAGMA_CUBE: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("magma_cube"))
    val PAINTING: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("painting"))
    val PIG: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("pig"))
    val PLAYER: EntityProtocolType<LanternPlayer> = CatalogRegistry.require(minecraftKey("player"))
    val RABBIT: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("rabbit"))
    val SHEEP: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("sheep"))
    val SILVERFISH: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("silverfish"))
    val SLIME: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("slime"))
    val SNOWMAN: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("snowman"))
    val VILLAGER: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("villager"))
    val ZOMBIE: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("zombie"))
    val ZOMBIE_VILLAGER: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("zombie_villager"))
    val HORSE: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("horse"))
    val DONKEY: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("donkey"))
    val LLAMA: EntityProtocolType<LanternEntity> = CatalogRegistry.require(minecraftKey("llama"))

}