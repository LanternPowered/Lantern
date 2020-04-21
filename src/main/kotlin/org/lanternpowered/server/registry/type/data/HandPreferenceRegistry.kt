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
package org.lanternpowered.server.registry.type.data

import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.text.translation.Translated
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.type.HandPreference
import org.spongepowered.api.text.translation.Translatable

val HandPreferenceRegistry = catalogTypeRegistry<HandPreference> {
    fun register(id: String, translationKey: String) =
            register(LanternHandPreference(CatalogKey.minecraft(id), translationKey))

    register("left", "options.mainHand.left")
    register("right", "options.mainHand.right")
}

private class LanternHandPreference(key: CatalogKey, translationKey: String) :
        DefaultCatalogType(key), HandPreference, Translatable by Translated(translationKey)
