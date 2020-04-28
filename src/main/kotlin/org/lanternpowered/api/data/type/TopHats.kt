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
package org.lanternpowered.api.data.type

import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.provide

/**
 * An enumeration of known [TopHat] types.
 */
object TopHats {
    val BLACK: TopHat = CatalogRegistry.provide("BLACK")
    val BLUE: TopHat = CatalogRegistry.provide("BLUE")
    val BROWN: TopHat = CatalogRegistry.provide("BROWN")
    val CYAN: TopHat = CatalogRegistry.provide("CYAN")
    val GOLD: TopHat = CatalogRegistry.provide("GOLD")
    val GRAY: TopHat = CatalogRegistry.provide("GRAY")
    val GREEN: TopHat = CatalogRegistry.provide("GREEN")
    val IRON: TopHat = CatalogRegistry.provide("IRON")
    val LIGHT_BLUE: TopHat = CatalogRegistry.provide("LIGHT_BLUE")
    val LIME: TopHat = CatalogRegistry.provide("LIME")
    val MAGENTA: TopHat = CatalogRegistry.provide("MAGENTA")
    val ORANGE: TopHat = CatalogRegistry.provide("ORANGE")
    val PINK: TopHat = CatalogRegistry.provide("PINK")
    val PURPLE: TopHat = CatalogRegistry.provide("PURPLE")
    val RED: TopHat = CatalogRegistry.provide("RED")
    val LIGHT_GRAY: TopHat = CatalogRegistry.provide("LIGHT_GRAY")
    val SNOW: TopHat = CatalogRegistry.provide("SNOW")
    val STONE: TopHat = CatalogRegistry.provide("STONE")
    val WHITE: TopHat = CatalogRegistry.provide("WHITE")
    val WOOD: TopHat = CatalogRegistry.provide("WOOD")
    val YELLOW: TopHat = CatalogRegistry.provide("YELLOW")
}
