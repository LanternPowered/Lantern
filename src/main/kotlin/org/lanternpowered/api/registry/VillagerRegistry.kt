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
package org.lanternpowered.api.registry

/**
 * The villager registry.
 */
interface VillagerRegistry : org.spongepowered.api.item.merchant.VillagerRegistry {

    /**
     * The singleton instance of the villager registry.
     */
    companion object : VillagerRegistry by GameRegistry.villagerRegistry
}
