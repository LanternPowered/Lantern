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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.data.type.ArtType

val ArtTypeRegistry = internalCatalogTypeRegistry<ArtType> {
    fun register(id: String, name: String, width: Int, height: Int) =
            register(LanternArtType(CatalogKey.minecraft(id), name, width, height))

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

private class LanternArtType(key: CatalogKey, name: String, private val width: Int, private val height: Int):
        DefaultCatalogType.Named(key, name), ArtType {

    override fun getHeight(): Int = this.height
    override fun getWidth(): Int = this.width

    override fun toStringHelper() = super.toStringHelper()
            .add("width", this.width)
            .add("height", this.height)
}
