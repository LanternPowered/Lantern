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
package org.lanternpowered.server.entity.living.player.gamemode

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.text.translation.Translated
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.text.translation.Translatable

class LanternGameMode(key: NamespacedKey, translationPart: String, val abilityApplier: DataHolder.Mutable.() -> Unit) :
        DefaultCatalogType(key), GameMode, Translatable by Translated("gameMode.$translationPart")
