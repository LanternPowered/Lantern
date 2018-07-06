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
package org.lanternpowered.server.game.registry.type.util

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.ext.*
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
