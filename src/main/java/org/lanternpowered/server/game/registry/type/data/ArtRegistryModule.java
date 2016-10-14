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

import org.lanternpowered.server.data.type.LanternArt;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.spongepowered.api.data.type.Art;
import org.spongepowered.api.data.type.Arts;

public class ArtRegistryModule extends PluginCatalogRegistryModule<Art> {

    public ArtRegistryModule() {
        super(Arts.class);
    }

    @Override
    public void registerDefaults() {
        this.register(new LanternArt("minecraft", "alban", "Alban", 1, 1));
        this.register(new LanternArt("minecraft", "aztec", "Aztec", 1, 1));
        this.register(new LanternArt("minecraft", "aztec_2", "Aztec2", 1, 1));
        this.register(new LanternArt("minecraft", "bomb", "Bomb", 1, 1));
        this.register(new LanternArt("minecraft", "burning_skull", "BurningSkull", 4, 4));
        this.register(new LanternArt("minecraft", "bust", "Bust", 2, 2));
        this.register(new LanternArt("minecraft", "courbet", "Courbet", 2, 1));
        this.register(new LanternArt("minecraft", "creebet", "Creebet", 2, 1));
        this.register(new LanternArt("minecraft", "donkey_kong", "DonkeyKong", 4, 3));
        this.register(new LanternArt("minecraft", "fighters", "Fighters", 4, 2));
        this.register(new LanternArt("minecraft", "graham", "Graham", 1, 2));
        this.register(new LanternArt("minecraft", "kebab", "Kebab", 1, 1));
        this.register(new LanternArt("minecraft", "match", "Match", 2, 2));
        this.register(new LanternArt("minecraft", "pigscene", "Pigscene", 4, 4));
        this.register(new LanternArt("minecraft", "plant", "Plant", 1, 1));
        this.register(new LanternArt("minecraft", "pointer", "Pointer", 4, 4));
        this.register(new LanternArt("minecraft", "pool", "Pool", 2, 1));
        this.register(new LanternArt("minecraft", "sea", "Sea", 2, 1));
        this.register(new LanternArt("minecraft", "skeleton", "Skeleton", 4, 3));
        this.register(new LanternArt("minecraft", "skull_and_roses", "SkullAndRoses", 2, 2));
        this.register(new LanternArt("minecraft", "stage", "Stage", 2, 2));
        this.register(new LanternArt("minecraft", "sunset", "Sunset", 2, 1));
        this.register(new LanternArt("minecraft", "void", "Void", 2, 2));
        this.register(new LanternArt("minecraft", "wanderer", "Wanderer", 1, 2));
        this.register(new LanternArt("minecraft", "wasteland", "Wasteland", 1, 1));
        this.register(new LanternArt("minecraft", "wither", "Wither", 2, 2));
    }
}
