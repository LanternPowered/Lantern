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
@file:JvmName("AdvancementTreeRegistry")
package org.lanternpowered.server.registry.type.advancement

import org.lanternpowered.api.registry.MutableCatalogTypeRegistry
import org.lanternpowered.api.registry.mutableCatalogTypeRegistry
import org.lanternpowered.server.advancement.layout.LanternTreeLayout
import org.spongepowered.api.advancement.Advancement
import org.spongepowered.api.advancement.AdvancementTree

@get:JvmName("get")
val AdvancementTreeRegistry = mutableCatalogTypeRegistry<AdvancementTree>()

/**
 * Updates the [AdvancementTree] layouts.
 */
fun MutableCatalogTypeRegistry<AdvancementTree>.updateLayouts() {
    for (tree in this) {
        val layout = LanternTreeLayout(tree)
        layout.generate()
    }
}
