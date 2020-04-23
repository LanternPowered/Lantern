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
package org.lanternpowered.server.network.vanilla.recipe

object NetworkRecipeTypes {
    const val CRAFTING_SHAPED = "minecraft:crafting_shaped"
    const val CRAFTING_SHAPELESS = "minecraft:crafting_shapeless"
    const val SMELTING = "minecraft:smelting"
    const val BLASTING = "minecraft:blasting"
    const val SMOKING = "minecraft:smoking"
    const val CAMPFIRE_COOKING = "minecraft:campfire_cooking"

    // Special cases
    const val ARMOR_DYE = "minecraft:crafting_special_armordye"
    const val BOOK_CLONING = "minecraft:crafting_special_bookcloning"
    const val MAP_CLONING = "minecraft:crafting_special_mapcloning"
    const val MAP_EXTENDING = "minecraft:crafting_special_mapextending"
    const val FIREWORK_ROCKET = "minecraft:crafting_special_firework_rocket"
    const val FIREWORK_STAR = "minecraft:crafting_special_firework_star"
    const val FIREWORK_STAR_FADE = "minecraft:crafting_special_firework_star_fade"
    const val REPAIR_ITEM = "minecraft:crafting_special_repairitem"
    const val TIPPED_ARROW = "minecraft:crafting_special_tippedarrow"
    const val BANNER_DUPLICATE = "minecraft:crafting_special_bannerduplicate"
    const val BANNER_ADD_PATTERN = "minecraft:crafting_special_banneraddpattern"
    const val SHIELD_DECORATION = "minecraft:crafting_special_shielddecoration"
    const val SHULKER_BOX_COLORING = "minecraft:crafting_special_shulkerboxcoloring"
}
