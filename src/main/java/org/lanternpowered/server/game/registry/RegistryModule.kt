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
package org.lanternpowered.server.game.registry

/**
 * A module that handles registrations of [Object]s, usually
 * [CatalogType]s. The use of this is to manage like-object
 * registrations in a centralized manner, without requiring hard
 * `ids` or content affecting behavior. All `module`s
 * are to be loaded and registered with the [GameRegistry].
 *
 *
 * Note that there are various aspects of how initialization of
 * these modules can be affected, be it from [CatalogRegistryModule]s,
 * [DelayedRegistration], [AlternateCatalogRegistryModule]s,
 * or [RegistrationDependency].
 */
@Deprecated(message = "TODO")
interface RegistryModule {

    /**
     * Performs default registrations of objects for use with either
     * companion objects, or the [GameRegistry]. This is only
     * called once, either at the pre-initialization phase of the
     * [GameRegistry], or if annotated with
     * [DelayedRegistration], at a later [RegistrationPhase].
     */
    @JvmDefault
    fun registerDefaults() {}
}
