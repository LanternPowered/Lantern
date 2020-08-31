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
package org.lanternpowered.api.item.inventory.container

import org.lanternpowered.api.item.inventory.container.layout.AnvilContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.BeaconContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.BrewingContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.CartographyContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.DonkeyContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.LecternContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.SmeltingContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.GridContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.GrindstoneContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.HorseContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.LlamaContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.LoomContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.TopBottomContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.MerchantContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.StoneCutterContainerLayout
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.require

/**
 * An enumeration of all the supported [ExtendedContainerType]s.
 */
object ContainerTypes {

    val Furnace: ExtendedContainerType<TopBottomContainerLayout<SmeltingContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("furnace")).fixWithLayout()

    val BlastFurnace: ExtendedContainerType<TopBottomContainerLayout<SmeltingContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("blast_furnace")).fixWithLayout()

    val Smoker: ExtendedContainerType<TopBottomContainerLayout<SmeltingContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("smoker")).fixWithLayout()

    val Hopper: ExtendedContainerType<TopBottomContainerLayout<GridContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("hopper")).fixWithLayout()

    val Generic3x3: ExtendedContainerType<TopBottomContainerLayout<GridContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("generic_3x3")).fixWithLayout()

    /**
     * Gets a generic 9xN container layout for the given number of rows.
     *
     * Rows must be between 1 and 6 (inclusive).
     */
    fun generic9xN(rows: Int): ExtendedContainerType<TopBottomContainerLayout<GridContainerLayout>> =
            when (rows) {
                1 -> Generic9x1
                2 -> Generic9x2
                3 -> Generic9x3
                4 -> Generic9x4
                5 -> Generic9x5
                6 -> Generic9x6
                else -> throw IllegalArgumentException("No generic layout with $rows rows.")
            }

    val Generic9x1: ExtendedContainerType<TopBottomContainerLayout<GridContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("generic_9x1")).fixWithLayout()

    val Generic9x2: ExtendedContainerType<TopBottomContainerLayout<GridContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("generic_9x2")).fixWithLayout()

    val Generic9x3: ExtendedContainerType<TopBottomContainerLayout<GridContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("generic_9x3")).fixWithLayout()

    val Generic9x4: ExtendedContainerType<TopBottomContainerLayout<GridContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("generic_9x4")).fixWithLayout()

    val Generic9x5: ExtendedContainerType<TopBottomContainerLayout<GridContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("generic_9x5")).fixWithLayout()

    val Generic9x6: ExtendedContainerType<TopBottomContainerLayout<GridContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("generic_9x6")).fixWithLayout()

    val Lectern: ExtendedContainerType<LecternContainerLayout> =
            CatalogRegistry.require<ContainerType>(minecraftKey("lectern")).fixWithLayout()

    val ShulkerBox: ExtendedContainerType<TopBottomContainerLayout<GridContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("shulker_box")).fixWithLayout()

    val Brewing: ExtendedContainerType<TopBottomContainerLayout<BrewingContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("brewing_stand")).fixWithLayout()

    val Merchant: ExtendedContainerType<TopBottomContainerLayout<MerchantContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("merchant")).fixWithLayout()

    val Beacon: ExtendedContainerType<TopBottomContainerLayout<BeaconContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("beacon")).fixWithLayout()

    val Cartography: ExtendedContainerType<TopBottomContainerLayout<CartographyContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("cartography")).fixWithLayout()

    val Grindstone: ExtendedContainerType<TopBottomContainerLayout<GrindstoneContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("grindstone")).fixWithLayout()

    val Loom: ExtendedContainerType<TopBottomContainerLayout<LoomContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("loom")).fixWithLayout()

    val StoneCutter: ExtendedContainerType<TopBottomContainerLayout<StoneCutterContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("stonecutter")).fixWithLayout()

    val Anvil: ExtendedContainerType<TopBottomContainerLayout<AnvilContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("anvil")).fixWithLayout()

    val Horse: ExtendedContainerType<TopBottomContainerLayout<HorseContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("horse")).fixWithLayout()

    val Donkey: ExtendedContainerType<TopBottomContainerLayout<DonkeyContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("donkey")).fixWithLayout()

    val DonkeyChested: ExtendedContainerType<TopBottomContainerLayout<DonkeyContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("donkey_chested")).fixWithLayout()

    val Llama: ExtendedContainerType<TopBottomContainerLayout<LlamaContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("llama")).fixWithLayout()

    val LlamaChested1x3: ExtendedContainerType<TopBottomContainerLayout<LlamaContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("llama_chested_1x3")).fixWithLayout()

    val LlamaChested2x3: ExtendedContainerType<TopBottomContainerLayout<LlamaContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("llama_chested_2x3")).fixWithLayout()

    val LlamaChested3x3: ExtendedContainerType<TopBottomContainerLayout<LlamaContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("llama_chested_3x3")).fixWithLayout()

    val LlamaChested4x3: ExtendedContainerType<TopBottomContainerLayout<LlamaContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("llama_chested_4x3")).fixWithLayout()

    val LlamaChested5x3: ExtendedContainerType<TopBottomContainerLayout<LlamaContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("llama_chested_5x3")).fixWithLayout()

    /**
     * Gets a chested llama container layout for the given number of columns.
     *
     * Columns must be between 0 and 5 (inclusive).
     */
    fun llamaChested(columns: Int): ExtendedContainerType<TopBottomContainerLayout<LlamaContainerLayout>> =
            when (columns) {
                0 -> Llama
                1 -> LlamaChested1x3
                2 -> LlamaChested2x3
                3 -> LlamaChested3x3
                4 -> LlamaChested4x3
                5 -> LlamaChested5x3
                else -> throw IllegalArgumentException("No chested llama layout with $columns columns.")
            }
}
