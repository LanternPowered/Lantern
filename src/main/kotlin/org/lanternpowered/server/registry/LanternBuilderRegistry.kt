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
package org.lanternpowered.server.registry

import org.lanternpowered.api.registry.BaseBuilder
import org.lanternpowered.api.registry.BuilderRegistry

object LanternBuilderRegistry : BuilderRegistry {

    override fun <T : BaseBuilder<*, in T>> provideBuilder(builderClass: Class<T>): T {
        TODO("Not yet implemented")
    }
}
