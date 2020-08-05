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
package org.lanternpowered.server.effect.sound

import org.lanternpowered.api.effect.sound.SoundType
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.server.catalog.DefaultCatalogType

class LanternSoundType @JvmOverloads constructor(
        key: NamespacedKey, val eventId: Int? = null
) : DefaultCatalogType(key), SoundType
