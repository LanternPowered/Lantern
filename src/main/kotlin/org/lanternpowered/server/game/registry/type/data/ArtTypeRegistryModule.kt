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
package org.lanternpowered.server.game.registry.type.data

import org.lanternpowered.api.catalog.CatalogKeys
import org.lanternpowered.server.data.type.LanternArtType
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule
import org.spongepowered.api.data.type.ArtType
import org.spongepowered.api.data.type.ArtTypes

class ArtTypeRegistryModule : DefaultCatalogRegistryModule<ArtType>(ArtTypes::class.java) {

    override fun registerDefaults() {
        var internalId = 0
        fun register(id: String, name: String, width: Int, height: Int) =
                register(LanternArtType(CatalogKeys.minecraft(id, name), internalId++, width, height))

        register("alban", "Alban", 1, 1)
        register("aztec", "Aztec", 1, 1)
        register("aztec_2", "Aztec2", 1, 1)
        register("bomb", "Bomb", 1, 1)
        register("burning_skull", "BurningSkull", 4, 4)
        register("bust", "Bust", 2, 2)
        register("courbet", "Courbet", 2, 1)
        register("creebet", "Creebet", 2, 1)
        register("donkey_kong", "DonkeyKong", 4, 3)
        register("fighters", "Fighters", 4, 2)
        register("graham", "Graham", 1, 2)
        register("kebab", "Kebab", 1, 1)
        register("match", "Match", 2, 2)
        register("pigscene", "Pigscene", 4, 4)
        register("plant", "Plant", 1, 1)
        register("pointer", "Pointer", 4, 4)
        register("pool", "Pool", 2, 1)
        register("sea", "Sea", 2, 1)
        register("skeleton", "Skeleton", 4, 3)
        register("skull_and_roses", "SkullAndRoses", 2, 2)
        register("stage", "Stage", 2, 2)
        register("sunset", "Sunset", 2, 1)
        register("void", "Void", 2, 2)
        register("wanderer", "Wanderer", 1, 2)
        register("wasteland", "Wasteland", 1, 1)
        register("wither", "Wither", 2, 2)
    }
}
