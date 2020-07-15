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
package org.lanternpowered.server.registry.type.world

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.world.scheduler.UpdatePriority
import org.lanternpowered.server.world.update.LanternUpdatePriority

val UpdatePriorityRegistry = catalogTypeRegistry<UpdatePriority> {
    fun register(id: String, value: Int) =
            register(LanternUpdatePriority(ResourceKey.minecraft(id), value))

    register("extremely_high", -3)
    register("very_high", -2)
    register("high", -1)
    register("normal", 0)
    register("low", 1)
    register("very_low", 2)
    register("extremely_low", 3)
}
