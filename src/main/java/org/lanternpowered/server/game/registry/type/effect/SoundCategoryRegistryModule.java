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
package org.lanternpowered.server.game.registry.type.effect;

import org.lanternpowered.server.effect.sound.LanternSoundCategory;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundCategory;

public final class SoundCategoryRegistryModule extends PluginCatalogRegistryModule<SoundCategory> {

    public SoundCategoryRegistryModule() {
        super(SoundCategories.class);
    }

    @Override
    public void registerDefaults() {
        this.register(new LanternSoundCategory("minecraft", "master", 0));
        this.register(new LanternSoundCategory("minecraft", "music", 1));
        this.register(new LanternSoundCategory("minecraft", "record", 2));
        this.register(new LanternSoundCategory("minecraft", "weather", 3));
        this.register(new LanternSoundCategory("minecraft", "block", 4));
        this.register(new LanternSoundCategory("minecraft", "hostile", 5));
        this.register(new LanternSoundCategory("minecraft", "neutral", 6));
        this.register(new LanternSoundCategory("minecraft", "player", 7));
        this.register(new LanternSoundCategory("minecraft", "ambient", 8));
        this.register(new LanternSoundCategory("minecraft", "voice", 9));
    }
}
