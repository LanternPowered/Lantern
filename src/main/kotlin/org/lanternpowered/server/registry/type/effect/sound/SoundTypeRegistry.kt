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
package org.lanternpowered.server.registry.type.effect.sound

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.server.effect.sound.LanternSoundType
import org.lanternpowered.server.game.registry.InternalRegistries
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.effect.sound.SoundType

val SoundTypeRegistry = internalCatalogTypeRegistry<SoundType> {
    matchSuggestedId { suggestedId, type ->
        type.key.value.replace('.', '_') == suggestedId
    }
    InternalRegistries.visit("sound_event") { key, internalId ->
        register(LanternSoundType(ResourceKey.resolve(key), internalId))
    }
}
