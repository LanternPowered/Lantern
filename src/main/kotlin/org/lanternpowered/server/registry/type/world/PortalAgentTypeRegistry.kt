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
package org.lanternpowered.server.registry.type.world

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.world.World
import org.lanternpowered.server.world.portal.EmptyPortalAgent
import org.lanternpowered.server.world.portal.LanternPortalAgentType
import org.spongepowered.api.world.teleport.PortalAgent
import org.spongepowered.api.world.teleport.PortalAgentType
import kotlin.reflect.KClass

val PortalAgentTypeRegistry = catalogTypeRegistry<PortalAgentType> {
    fun <T : PortalAgent> register(id: String, agentType: KClass<T>, agentProvider: (World, LanternPortalAgentType<T>) -> T) =
            register(LanternPortalAgentType(NamespacedKey.minecraft(id), agentType.java, agentProvider))

    register("empty", EmptyPortalAgent::class) { _, type -> EmptyPortalAgent(type) }
}
