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
package org.lanternpowered.server.registry.type.util

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.util.rotation.Rotation

val RotationRegistry = catalogTypeRegistry<Rotation> {
    fun register(id: String, angle: Int) =
            register(LanternRotation(NamespacedKey.minecraft(id), angle))

    register("top", 0)
    register("top_right", 45)
    register("right", 90)
    register("bottom_right", 135)
    register("bottom", 180)
    register("bottom_left", 225)
    register("left", 270)
    register("top_left", 315)
}

private class LanternRotation(key: NamespacedKey, private val angle: Int) : DefaultCatalogType(key), Rotation {
    override fun getAngle() = this.angle
    override fun toStringHelper() = super.toStringHelper()
            .add("angle", this.angle)
}
