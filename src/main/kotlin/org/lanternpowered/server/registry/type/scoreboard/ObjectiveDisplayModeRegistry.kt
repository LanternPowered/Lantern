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
package org.lanternpowered.server.registry.type.scoreboard

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.lanternpowered.server.scoreboard.LanternObjectiveDisplayMode
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode

val ObjectiveDisplayModeRegistry = internalCatalogTypeRegistry<ObjectiveDisplayMode> {
    fun register(id: String) =
            register(LanternObjectiveDisplayMode(CatalogKey.minecraft(id)))

    register("integer")
    register("hearts")
}
