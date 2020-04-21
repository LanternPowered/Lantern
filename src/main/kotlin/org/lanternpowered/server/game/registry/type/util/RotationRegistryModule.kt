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
package org.lanternpowered.server.game.registry.type.util

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.server.ext.*
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule
import org.lanternpowered.server.util.rotation.LanternRotation
import org.spongepowered.api.util.rotation.Rotation
import org.spongepowered.api.util.rotation.Rotations
import java.util.Optional

object RotationRegistryModule : DefaultCatalogRegistryModule<Rotation>(Rotations::class) {

    @JvmStatic
    fun get(): RotationRegistryModule = this

    override fun registerDefaults() {
        val register = { id: String, angle: Int -> register(LanternRotation(CatalogKey.minecraft(id), angle)) }
        register("top", 0)
        register("top_right", 45)
        register("right", 90)
        register("bottom_right", 135)
        register("bottom", 180)
        register("bottom_left", 225)
        register("left", 270)
        register("top_left", 315)
    }

    fun getRotationFromDegree(degrees: Int): Optional<Rotation> {
        val angle = Math.round(degrees.wrapDegRot().toFloat() / 360f * 8f) * 45
        return all.firstOrNull { it.angle == angle }.optional()
    }
}
