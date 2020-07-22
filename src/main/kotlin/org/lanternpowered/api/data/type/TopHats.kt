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
    val BLACK: TopHat by CatalogRegistry.provide("BLACK")
    val BLUE: TopHat by CatalogRegistry.provide("BLUE")
    val BROWN: TopHat by CatalogRegistry.provide("BROWN")
    val CYAN: TopHat by CatalogRegistry.provide("CYAN")
    val GOLD: TopHat by CatalogRegistry.provide("GOLD")
    val GRAY: TopHat by CatalogRegistry.provide("GRAY")
    val GREEN: TopHat by CatalogRegistry.provide("GREEN")
    val IRON: TopHat by CatalogRegistry.provide("IRON")
    val LIGHT_BLUE: TopHat by CatalogRegistry.provide("LIGHT_BLUE")
    val LIME: TopHat by CatalogRegistry.provide("LIME")
    val MAGENTA: TopHat by CatalogRegistry.provide("MAGENTA")
    val ORANGE: TopHat by CatalogRegistry.provide("ORANGE")
    val PINK: TopHat by CatalogRegistry.provide("PINK")
    val PURPLE: TopHat by CatalogRegistry.provide("PURPLE")
    val RED: TopHat by CatalogRegistry.provide("RED")
    val LIGHT_GRAY: TopHat by CatalogRegistry.provide("LIGHT_GRAY")
    val SNOW: TopHat by CatalogRegistry.provide("SNOW")
    val STONE: TopHat by CatalogRegistry.provide("STONE")
    val WHITE: TopHat by CatalogRegistry.provide("WHITE")
    val WOOD: TopHat by CatalogRegistry.provide("WOOD")
    val YELLOW: TopHat by CatalogRegistry.provide("YELLOW")
}
