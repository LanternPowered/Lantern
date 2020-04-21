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
package org.lanternpowered.server.registry.type.data

import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.type.PickupRule

val PickupRuleRegistry = internalCatalogTypeRegistry<PickupRule> {
    fun register(id: String) =
            register(LanternPickupRule(CatalogKey.minecraft(id)))

    register("disallowed")
    register("allowed")
    register("creative_only")
}

private class LanternPickupRule(key: CatalogKey) : DefaultCatalogType(key), PickupRule
