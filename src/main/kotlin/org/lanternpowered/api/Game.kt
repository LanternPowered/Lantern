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

import org.lanternpowered.api.injector.Injector
import org.lanternpowered.api.util.palette.PaletteBasedArrayFactory
import org.spongepowered.api.Client

typealias MinecraftVersion = org.spongepowered.api.MinecraftVersion
typealias Platform = org.spongepowered.api.Platform
typealias PlatformComponent = org.spongepowered.api.Platform.Component
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
     * The injector.
     */
    val injector: Injector

    @Deprecated(message = "The client isn't supported.", replaceWith = ReplaceWith(""), level = DeprecationLevel.HIDDEN)
    @JvmDefault
    override fun getClient(): Client = throw UnsupportedOperationException("The client isn't supported.")

    @Deprecated(message = "The client isn't supported.", replaceWith = ReplaceWith(""), level = DeprecationLevel.HIDDEN)
    @JvmDefault
    override fun isClientAvailable(): Boolean = false

    /**
     * The game singleton.
     */
    companion object : Game by Lantern.game
}
