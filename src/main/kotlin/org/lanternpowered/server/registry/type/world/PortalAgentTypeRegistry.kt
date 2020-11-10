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

import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.world.portal.PortalType
import org.lanternpowered.server.world.portal.EmptyPortalType

val PortalAgentTypeRegistry = catalogTypeRegistry<PortalType> {
    register(EmptyPortalType)
}
