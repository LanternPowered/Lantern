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
import org.lanternpowered.server.item.ItemTypeRegistry
import org.spongepowered.api.advancement.Advancement
import org.spongepowered.api.registry.util.RegistrationDependency

@RegistrationDependency(
        AdvancementTypeRegistryModule::class,
        ItemTypeRegistry::class,
        TriggerRegistryModule::class
)
object AdvancementRegistryModule : AdditionalPluginCatalogRegistryModule<Advancement>()
