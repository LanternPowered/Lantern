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
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.world.SerializationBehavior

val SerializationBehaviorRegistry = catalogTypeRegistry<SerializationBehavior> {
    fun register(id: String) =
            register(LanternSerializationBehavior(NamespacedKey.minecraft(id)))

    register("automatic")
    register("manual")
    register("none")
}

private class LanternSerializationBehavior(key: NamespacedKey) : DefaultCatalogType(key), SerializationBehavior
