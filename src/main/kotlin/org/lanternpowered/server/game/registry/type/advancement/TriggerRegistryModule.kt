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
package org.lanternpowered.server.game.registry.type.advancement

import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.spongepowered.api.advancement.criteria.trigger.Trigger

object TriggerRegistryModule : AdditionalPluginCatalogRegistryModule<Trigger<*>>() {

    /**
     * Gets the [TriggerRegistryModule].
     *
     * @return The advancement trigger registry module
     */
    @JvmStatic
    fun get(): TriggerRegistryModule = this
}
