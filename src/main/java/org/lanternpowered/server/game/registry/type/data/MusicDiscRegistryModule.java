/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
