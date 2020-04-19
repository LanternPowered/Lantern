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
package org.lanternpowered.server.game.registry.type.data;

import org.lanternpowered.server.data.type.LanternMusicDisc;
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.sound.SoundTypeRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.effect.sound.music.MusicDisc;
import org.spongepowered.api.effect.sound.music.MusicDiscs;
import org.spongepowered.api.registry.util.RegistrationDependency;

@RegistrationDependency(SoundTypeRegistryModule.class)
public class MusicDiscRegistryModule extends InternalPluginCatalogRegistryModule<MusicDisc> {

    private static final MusicDiscRegistryModule INSTANCE = new MusicDiscRegistryModule();

    public static MusicDiscRegistryModule get() {
        return INSTANCE;
    }

    private MusicDiscRegistryModule() {
        super(MusicDiscs.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternMusicDisc(CatalogKey.minecraft("thirteen"), "item.record.13.desc", 0, SoundTypes.MUSIC_DISC_13));
        register(new LanternMusicDisc(CatalogKey.minecraft("cat"), "item.record.cat.desc", 1, SoundTypes.MUSIC_DISC_CAT));
        register(new LanternMusicDisc(CatalogKey.minecraft("blocks"), "item.record.blocks.desc", 2, SoundTypes.MUSIC_DISC_BLOCKS));
        register(new LanternMusicDisc(CatalogKey.minecraft("chirp"), "item.record.chirp.desc", 3, SoundTypes.MUSIC_DISC_CHIRP));
        register(new LanternMusicDisc(CatalogKey.minecraft("far"), "item.record.far.desc", 4, SoundTypes.MUSIC_DISC_FAR));
        register(new LanternMusicDisc(CatalogKey.minecraft("mall"), "item.record.mall.desc", 5, SoundTypes.MUSIC_DISC_MALL));
        register(new LanternMusicDisc(CatalogKey.minecraft("mellohi"), "item.record.mellohi.desc", 6, SoundTypes.MUSIC_DISC_MELLOHI));
        register(new LanternMusicDisc(CatalogKey.minecraft("stal"), "item.record.stal.desc", 7, SoundTypes.MUSIC_DISC_STAL));
        register(new LanternMusicDisc(CatalogKey.minecraft("strad"), "item.record.strad.desc", 8, SoundTypes.MUSIC_DISC_STRAD));
        register(new LanternMusicDisc(CatalogKey.minecraft("ward"), "item.record.ward.desc", 9, SoundTypes.MUSIC_DISC_WARD));
        register(new LanternMusicDisc(CatalogKey.minecraft("eleven"), "item.record.11.desc", 10, SoundTypes.MUSIC_DISC_11));
        register(new LanternMusicDisc(CatalogKey.minecraft("wait"), "item.record.wait.desc", 11, SoundTypes.MUSIC_DISC_WAIT));
    }
}
