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
package org.lanternpowered.server.game.registry.type.effect.sound

import org.lanternpowered.server.effect.sound.LanternSoundCategory
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule
import org.lanternpowered.server.game.registry.InternalRegistries
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.effect.sound.SoundCategories
import org.spongepowered.api.effect.sound.SoundCategory

class SoundCategoryRegistryModule : DefaultCatalogRegistryModule<SoundCategory>(SoundCategories::class) {

    override fun registerDefaults() {
        InternalRegistries.visit("sound_category") { key, internalId ->
            register(LanternSoundCategory(CatalogKey.resolve(key), internalId))
        }
    }
}
