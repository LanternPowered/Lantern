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
package org.lanternpowered.server.game.registry.type.effect

import org.lanternpowered.api.catalog.CatalogKeys
import org.lanternpowered.api.catalog.CatalogKeys.minecraft
import org.lanternpowered.server.effect.potion.LanternPotionEffectType
import org.lanternpowered.server.game.registry.AdditionalInternalPluginCatalogRegistryModule
import org.lanternpowered.server.game.registry.InternalRegistries
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.effect.potion.PotionEffectType
import org.spongepowered.api.effect.potion.PotionEffectTypes

object PotionEffectTypeRegistryModule : AdditionalInternalPluginCatalogRegistryModule<PotionEffectType>(PotionEffectTypes::class) {

    override fun registerDefaults() {
        val internalIds = mutableMapOf<CatalogKey, Int>()

        InternalRegistries.visitElements("mob_effect") { id, element ->
            internalIds[CatalogKeys.resolve(id)] = element.asJsonObject["internal_id"].asInt
        }

        fun register(key: CatalogKey) = register(LanternPotionEffectType(key, internalIds[key]!!))

        register(minecraft("speed"))
        register(minecraft("slowness"))
        register(minecraft("haste"))
        register(minecraft("mining_fatigue"))
        register(minecraft("strength"))
        register(minecraft("instant_health")).instant()
        register(minecraft("instant_damage")).instant()
        register(minecraft("jump_boost"))
        register(minecraft("nausea"))
        register(minecraft("regeneration"))
        register(minecraft("resistance"))
        register(minecraft("fire_resistance"))
        register(minecraft("water_breathing"))
        register(minecraft("invisibility"))
        register(minecraft("blindness"))
        register(minecraft("night_vision"))
        register(minecraft("hunger"))
        register(minecraft("weakness"))
        register(minecraft("poison"))
        register(minecraft("wither"))
        register(minecraft("health_boost"))
        register(minecraft("absorption"))
        register(minecraft("saturation"))
        register(minecraft("glowing"))
        register(minecraft("levitation"))
        register(minecraft("luck"))
        register(minecraft("unluck"))
        register(minecraft("bad_omen"))
        register(minecraft("conduit_power"))
        register(minecraft("dolphins_grace"))
        register(minecraft("hero_of_the_village"))
        register(minecraft("slow_falling"))
    }
}
