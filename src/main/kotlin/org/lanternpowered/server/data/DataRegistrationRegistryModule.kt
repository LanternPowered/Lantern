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
package org.lanternpowered.server.data

import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.spongepowered.api.data.DataRegistration
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value

@Suppress("UNCHECKED_CAST")
object DataRegistrationRegistryModule : AdditionalPluginCatalogRegistryModule<DataRegistration>() {

    override fun <A : DataRegistration> register(registration: A): A {
        return super.register(registration).apply {
            for (key in registration.keys) {
                key as Key<Value<Any>>

                val provider = registration.getProviderFor(key).orNull()
                if (provider != null) {
                    GlobalKeyRegistry.register(key).addProvider(provider)
                } else {
                    // TODO: Register as non provider key
                }
            }
        }
    }
}
