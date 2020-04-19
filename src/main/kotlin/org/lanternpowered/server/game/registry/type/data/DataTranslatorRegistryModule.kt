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
package org.lanternpowered.server.game.registry.type.data

import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.spongepowered.api.data.persistence.DataTranslator

object DataTranslatorRegistryModule :
        AdditionalPluginCatalogRegistryModule<DataTranslator<*>>(org.spongepowered.api.data.persistence.DataTranslators::class) {

    override fun <A : DataTranslator<*>> register(catalogType: A): A {
        DataSerializerRegistry.registerTranslator(catalogType, false)
        return catalogType
    }

    override fun registerAdditionalCatalog(catalogType: DataTranslator<*>) {
        DataSerializerRegistry.registerTranslator(catalogType, true)
    }

    internal fun doRegistration0(dataTranslator: DataTranslator<*>, disallowInbuiltPluginIds: Boolean) {
        super.doRegistration(dataTranslator, disallowInbuiltPluginIds)
    }
}
