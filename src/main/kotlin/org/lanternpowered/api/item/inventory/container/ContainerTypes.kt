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
import org.lanternpowered.api.item.inventory.container.layout.EmptyContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.FurnaceContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.GridContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.GrindstoneContainerLayout
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

    val Furnace: ExtendedContainerType<TopBottomContainerLayout<FurnaceContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("furnace")).fixWithLayout()

    val BlastFurnace: ExtendedContainerType<TopBottomContainerLayout<FurnaceContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("blast_furnace")).fixWithLayout()

    val Smoker: ExtendedContainerType<TopBottomContainerLayout<FurnaceContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("smoker")).fixWithLayout()

    val Hopper: ExtendedContainerType<TopBottomContainerLayout<GridContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("hopper")).fixWithLayout()

    val Generic3x3: ExtendedContainerType<TopBottomContainerLayout<GridContainerLayout>> =
            CatalogRegistry.require<ContainerType>(minecraftKey("generic_3x3")).fixWithLayout()

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

    val Lectern: ExtendedContainerType<EmptyContainerLayout> =
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

}
