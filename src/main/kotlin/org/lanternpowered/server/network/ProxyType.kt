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
package org.lanternpowered.server.network

import org.lanternpowered.api.util.optional.asOptional
import java.util.Optional

enum class ProxyType(val displayName: String) {

    /**
     * The BungeeCord proxy.
     *
     * @see [https://github.com/SpigotMC/BungeeCord](BungeeCord)
     */
    BUNGEE_CORD("BungeeCord"),

    /**
     * The LilyPad proxy.
     *
     * @see [https://github.com/LilyPad](LilyPad)
     */
    LILY_PAD("LilyPad"),

    /**
     * The Waterfall proxy.
     *
     * @see [https://github.com/WaterfallMC/Waterfall](Waterfall)
     */
    WATERFALL("Waterfall"),

    /**
     * The Velocity proxy.
     *
     * @see [https://github.com/VelocityPowered/Velocity](Velocity)
     */
    VELOCITY("Velocity"),

    /**
     * There is no proxy in use.
     */
    NONE("None"),
    ;

    companion object {

        @JvmStatic
        fun getByName(name: String): Optional<ProxyType> =
                values().firstOrNull { it.name.equals(name, ignoreCase = true) }.asOptional()
    }
}
