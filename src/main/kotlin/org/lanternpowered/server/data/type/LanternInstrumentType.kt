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

import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.catalog.asString
import org.spongepowered.api.CatalogType
import org.spongepowered.api.data.type.InstrumentType
import org.spongepowered.api.effect.sound.SoundType
import org.spongepowered.api.effect.sound.SoundTypes

enum class LanternInstrumentType(id: String, soundTypeSupplier: () -> SoundType) :
        InstrumentType, CatalogType by DefaultCatalogType.minecraft(id) {

    HARP        ("harp", { SoundTypes.BLOCK_NOTE_BLOCK_HARP }),
    BASEDRUM    ("bass_drum", { SoundTypes.BLOCK_NOTE_BLOCK_BASEDRUM }),
    SNARE       ("snare", { SoundTypes.BLOCK_NOTE_BLOCK_SNARE }),
    HAT         ("high_hat", { SoundTypes.BLOCK_NOTE_BLOCK_HAT }),
    BASS        ("bass_attack", { SoundTypes.BLOCK_NOTE_BLOCK_BASS }),
    FLUTE       ("flute", { SoundTypes.BLOCK_NOTE_BLOCK_FLUTE }),
    BELL        ("bell", { SoundTypes.BLOCK_NOTE_BLOCK_BELL }),
    GUITAR      ("guitar", { SoundTypes.BLOCK_NOTE_BLOCK_GUITAR }),
    CHIME       ("chime", { SoundTypes.BLOCK_NOTE_BLOCK_CHIME }),
    XYLOPHONE   ("xylophone", { SoundTypes.BLOCK_NOTE_BLOCK_XYLOPHONE }),
    ;

    private val soundType: SoundType by lazy(soundTypeSupplier)

    override fun getSound() = this.soundType
    override fun toString(): String = asString()
}
