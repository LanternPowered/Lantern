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

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.namespace.minecraftKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.data.type.HandType

val HandTypeRegistry = catalogTypeRegistry<HandType> {
    fun register(id: String, translationKey: String) =
            register(LanternHandType(minecraftKey(id), translationKey))

    register("main_hand", "options.mainHand")
    register("off_hand", "hand.off")
}

private class LanternHandType(key: NamespacedKey, translationKey: String) :
        DefaultCatalogType(key), HandType, TextRepresentable by translatableTextOf(translationKey)
