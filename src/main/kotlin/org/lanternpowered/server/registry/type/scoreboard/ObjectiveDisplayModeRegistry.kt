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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode

val ObjectiveDisplayModeRegistry = internalCatalogTypeRegistry<ObjectiveDisplayMode> {
    fun register(id: String) =
            register(LanternObjectiveDisplayMode(NamespacedKey.minecraft(id)))

    register("integer")
    register("hearts")
}

private class LanternObjectiveDisplayMode(key: NamespacedKey) : DefaultCatalogType(key), ObjectiveDisplayMode
