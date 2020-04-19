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
package org.lanternpowered.server.world.update

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.scheduler.TaskPriorities
import org.spongepowered.api.scheduler.TaskPriority

object TaskPriorityRegistry : DefaultCatalogRegistryModule<TaskPriority>(TaskPriorities::class) {

    private val byPriority = Int2ObjectOpenHashMap<TaskPriority>()

    override fun registerDefaults() {
        var priority = -3

        fun register(id: String) {
            val value = priority++
            this.byPriority[value] = register(LanternTaskPriority(CatalogKey.minecraft(id), value))
        }

        register("extremely_high")
        register("very_high")
        register("high")
        register("normal")
        register("low")
        register("very_low")
        register("extremely_low")

        finalizeContent()
    }

    fun getByPriority(priority: Int) = this.byPriority[priority]
}
