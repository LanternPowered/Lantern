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

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.api.world.difficulty.Difficulty
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry

val DifficultyRegistry = internalCatalogTypeRegistry<Difficulty> {
    fun register(id: String) =
            register(LanternDifficulty(NamespacedKey.minecraft(id), translatableTextOf("options.difficulty.$id")))

    register("peaceful")
    register("easy")
    register("normal")
    register("hard")
}

private class LanternDifficulty(key: NamespacedKey, text: Text) : DefaultCatalogType(key), Difficulty, TextRepresentable by text
