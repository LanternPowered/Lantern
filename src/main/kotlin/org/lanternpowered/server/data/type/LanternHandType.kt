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
package org.lanternpowered.server.data.type

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.text.translation.Translated
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.text.translation.Translatable

class LanternHandType(key: CatalogKey, translationKey: String) :
        DefaultCatalogType(key), HandType, Translatable by Translated(translationKey)
