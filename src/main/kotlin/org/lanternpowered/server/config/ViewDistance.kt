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
package org.lanternpowered.server.config

object ViewDistance {

    /**
     * If this setting is used, it will default to the global setting.
     */
    const val USE_GLOBAL_SETTING = -1

    /**
     * The maximum view distance that can be used.
     */
    const val MAXIMUM = 32

    /**
     * The default view distance that will be used.
     */
    const val DEFAULT = 10

    /**
     * The minimum view distance that can be used.
     */
    const val MINIMUM = 3
}
