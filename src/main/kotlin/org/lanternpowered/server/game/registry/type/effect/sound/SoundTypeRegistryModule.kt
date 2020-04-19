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

import org.lanternpowered.api.catalog.CatalogKeys
import org.lanternpowered.server.effect.sound.LanternSoundType
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.lanternpowered.server.game.registry.InternalRegistries
import org.spongepowered.api.effect.sound.SoundType
import org.spongepowered.api.effect.sound.SoundTypes

class SoundTypeRegistryModule : AdditionalPluginCatalogRegistryModule<SoundType>(SoundTypes::class) {

    override fun registerDefaults() {
        InternalRegistries.visit("sound_event") { key, internalId ->
            register(LanternSoundType(CatalogKeys.resolve(key), internalId))
        }
    }

    override fun provideCatalogMap() = super.provideCatalogMap().mapKeys { (key, _) -> key.replace('.', '_') }
}
