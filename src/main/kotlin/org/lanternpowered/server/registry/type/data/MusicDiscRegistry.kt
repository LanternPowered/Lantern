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
import org.lanternpowered.api.text.translation.Translatable
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.lanternpowered.server.text.translation.Translated
import org.spongepowered.api.effect.sound.SoundType
import org.spongepowered.api.effect.sound.SoundTypes
import org.spongepowered.api.effect.sound.music.MusicDisc
import java.util.function.Supplier

val MusicDiscRegistry = internalCatalogTypeRegistry<MusicDisc> {
    fun register(id: String, translation: String, soundType: Supplier<out SoundType>) =
            register(LanternMusicDisc(minecraftKey(id), translation, soundType.get()))

    register("thirteen", "item.record.13.desc", SoundTypes.MUSIC_DISC_13)
    register("cat", "item.record.cat.desc", SoundTypes.MUSIC_DISC_CAT)
    register("blocks", "item.record.blocks.desc", SoundTypes.MUSIC_DISC_BLOCKS)
    register("chirp", "item.record.chirp.desc", SoundTypes.MUSIC_DISC_CHIRP)
    register("far", "item.record.far.desc", SoundTypes.MUSIC_DISC_FAR)
    register("mall", "item.record.mall.desc", SoundTypes.MUSIC_DISC_MALL)
    register("mellohi", "item.record.mellohi.desc", SoundTypes.MUSIC_DISC_MELLOHI)
    register("stal", "item.record.stal.desc", SoundTypes.MUSIC_DISC_STAL)
    register("strad", "item.record.strad.desc", SoundTypes.MUSIC_DISC_STRAD)
    register("ward", "item.record.ward.desc", SoundTypes.MUSIC_DISC_WARD)
    register("eleven", "item.record.11.desc", SoundTypes.MUSIC_DISC_11)
    register("wait", "item.record.wait.desc", SoundTypes.MUSIC_DISC_WAIT)
}

private class LanternMusicDisc(key: NamespacedKey, translation: String, private val soundType: SoundType) :
        DefaultCatalogType(key), MusicDisc, Translatable by Translated(translation) {

    override fun getSound(): SoundType = this.soundType
}
