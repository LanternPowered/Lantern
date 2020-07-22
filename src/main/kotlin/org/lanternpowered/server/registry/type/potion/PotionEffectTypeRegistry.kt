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
package org.lanternpowered.server.registry.type.potion

import org.lanternpowered.api.effect.potion.PotionEffectType
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.key.resolveNamespacedKey
import org.lanternpowered.server.effect.potion.LanternPotionEffectType
import org.lanternpowered.server.game.registry.InternalRegistries
import org.lanternpowered.server.registry.internalCatalogTypeRegistry

val PotionEffectTypeRegistry = internalCatalogTypeRegistry<PotionEffectType> {
    val internalIds = mutableMapOf<NamespacedKey, Int>()

    InternalRegistries.visitElements("mob_effect") { id, element ->
        internalIds[resolveNamespacedKey(id)] = element.asJsonObject["internal_id"].asInt
    }

    fun register(id: String): LanternPotionEffectType {
        val key = minecraftKey(id)
        val internalId = internalIds[key]!!
        return register(internalId, LanternPotionEffectType(key))
    }

    register("speed")
    register("slowness")
    register("haste")
    register("mining_fatigue")
    register("strength")
    register("instant_health").instant()
    register("instant_damage").instant()
    register("jump_boost")
    register("nausea")
    register("regeneration")
    register("resistance")
    register("fire_resistance")
    register("water_breathing")
    register("invisibility")
    register("blindness")
    register("night_vision")
    register("hunger")
    register("weakness")
    register("poison")
    register("wither")
    register("health_boost")
    register("absorption")
    register("saturation")
    register("glowing")
    register("levitation")
    register("luck")
    register("unluck")
    register("bad_omen")
    register("conduit_power")
    register("dolphins_grace")
    register("hero_of_the_village")
    register("slow_falling")
}
