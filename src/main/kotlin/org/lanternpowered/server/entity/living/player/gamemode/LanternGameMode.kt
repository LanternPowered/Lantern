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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.entity.living.player.gamemode.GameMode

class LanternGameMode(key: NamespacedKey, text: Text, val abilityApplier: DataHolder.Mutable.() -> Unit) :
        DefaultCatalogType(key), GameMode, TextRepresentable by text
