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
import org.lanternpowered.api.text.translation.Translatable
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.text.translation.Translated
import org.spongepowered.api.effect.sound.SoundType
import org.spongepowered.api.effect.sound.music.MusicDisc

class LanternMusicDisc(key: CatalogKey, translation: String, private val soundType: SoundType) :
        DefaultCatalogType(key), MusicDisc, Translatable by Translated(translation) {

    override fun getSound(): SoundType = this.soundType
}
