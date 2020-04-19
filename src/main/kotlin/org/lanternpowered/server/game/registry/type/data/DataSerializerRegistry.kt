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

import org.lanternpowered.server.data.persistence.DataTypeSerializerCollection
import org.lanternpowered.server.data.persistence.SimpleDataTypeSerializerCollection
import org.spongepowered.api.data.persistence.DataTranslator

object DataSerializerRegistry : SimpleDataTypeSerializerCollection() {

    override fun <T> registerTranslator(serializer: DataTranslator<T>): DataTypeSerializerCollection {
        registerTranslator(serializer, false)
        return this
    }

    internal fun <T> registerTranslator(serializer: DataTranslator<T>, disallowInbuiltPluginIds: Boolean) {
        super.registerTranslator(serializer)
        DataTranslatorRegistryModule.doRegistration0(serializer, disallowInbuiltPluginIds)
    }
}
