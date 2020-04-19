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

import org.lanternpowered.server.advancement.layout.LanternTreeLayout
import org.spongepowered.api.registry.RegistrationPhase
import org.spongepowered.api.registry.RegistryModule
import org.spongepowered.api.registry.util.DelayedRegistration
import org.spongepowered.api.registry.util.RegistrationDependency

@RegistrationDependency(AdvancementTreeRegistryModule::class)
class AdvancementTreeLayoutModule : RegistryModule {

    @DelayedRegistration(RegistrationPhase.INIT)
    override fun registerDefaults() {
        for (tree in AdvancementTreeRegistryModule.get().all) {
            val layout = LanternTreeLayout(tree)
            layout.generate()
        }
    }
}
