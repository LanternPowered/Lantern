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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.world.difficulty.Difficulty
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.lanternpowered.server.text.translation.Translated
import org.spongepowered.api.text.translation.Translatable

val DifficultyRegistry = internalCatalogTypeRegistry<Difficulty> {
    fun register(id: String) =
            register(LanternDifficulty(CatalogKey.minecraft(id)))

    register("peaceful")
    register("easy")
    register("normal")
    register("hard")
}

private class LanternDifficulty(key: CatalogKey) : DefaultCatalogType(key), Difficulty,
        Translatable by Translated("options.difficulty.${key.value}")
