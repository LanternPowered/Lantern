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

import org.lanternpowered.api.world.WorldManager

/**
 * Represents the server.
 */
interface Server : org.spongepowered.api.Server {

    override fun getWorldManager(): WorldManager

    /**
     * A singleton instance of the server.
     */
    companion object : Server by Lantern.server
}
