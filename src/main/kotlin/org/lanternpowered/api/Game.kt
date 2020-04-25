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
package org.lanternpowered.api

import org.lanternpowered.api.util.palette.PaletteBasedArrayFactory

typealias GameState = org.spongepowered.api.GameState
typealias MinecraftVersion = org.spongepowered.api.MinecraftVersion
typealias Platform = org.spongepowered.api.Platform
typealias Server = org.spongepowered.api.Server
typealias Sponge = org.spongepowered.api.Sponge

/**
 * The game.
 */
interface Game : org.spongepowered.api.Game {

    /**
     * The [PaletteBasedArrayFactory].
     */
    val paletteBasedArrayFactory: PaletteBasedArrayFactory

    /**
     * The game singleton.
     */
    companion object : Game by Lantern.game
}
