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
package org.lanternpowered.api.world

import org.lanternpowered.api.Lantern

/**
 * The world manager.
 */
interface WorldManager : org.spongepowered.api.world.server.WorldManager {

    /**
     * The singleton instance of the world manager.
     */
    companion object : WorldManager by Lantern.worldManager
}
