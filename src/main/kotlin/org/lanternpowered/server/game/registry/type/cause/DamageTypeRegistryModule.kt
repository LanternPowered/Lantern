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
package org.lanternpowered.server.game.registry.type.cause

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.ext.*
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.spongepowered.api.event.cause.entity.damage.DamageType
import org.spongepowered.api.event.cause.entity.damage.DamageTypes

class DamageTypeRegistryModule : AdditionalPluginCatalogRegistryModule<DamageType>(DamageTypes::class) {

    override fun registerDefaults() {
        val register = { id: String -> register(DamageType(CatalogKey.minecraft(id))) }
        register("attack")
        register("contact")
        register("custom")
        register("drown")
        register("explosive")
        register("fall")
        register("fire")
        register("generic")
        register("hunger")
        register("magic")
        register("magma")
        register("projectile")
        register("suffocate")
        register("sweeping_attack")
        register("void")
        register(POISON)
        register(WITHER)
        register(LIGHTNING)
    }

    companion object {

        @JvmField
        val POISON = DamageType(CatalogKey.minecraft("poison"))

        @JvmField
        val WITHER = DamageType(CatalogKey.minecraft("wither"))

        @JvmField
        val LIGHTNING = DamageType(CatalogKey.minecraft("lightning"))
    }
}
