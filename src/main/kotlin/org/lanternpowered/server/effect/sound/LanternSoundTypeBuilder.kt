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

import org.lanternpowered.server.catalog.AbstractCatalogBuilder
import org.lanternpowered.api.namespace.NamespacedKey
import org.spongepowered.api.effect.sound.SoundType

class LanternSoundTypeBuilder : AbstractCatalogBuilder<SoundType, SoundType.Builder>(), SoundType.Builder {

    override fun reset(): SoundType.Builder = apply {}
    override fun build(key: NamespacedKey): SoundType = LanternSoundType(key)
}
