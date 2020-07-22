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
package org.lanternpowered.server.registry.type.cause

import org.lanternpowered.api.cause.entity.damage.DamageType
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.cause.entity.damage.LanternDamageType

val DamageTypeRegistry = catalogTypeRegistry<DamageType> {
    fun register(id: String) =
            register(LanternDamageType(minecraftKey(id)))

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
    register("poison")
    register("wither")
    register("lightning")
}
