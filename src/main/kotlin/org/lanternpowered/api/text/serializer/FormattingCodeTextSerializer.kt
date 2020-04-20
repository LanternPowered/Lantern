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
package org.lanternpowered.api.text.serializer

import org.lanternpowered.api.registry.factoryOf

interface FormattingCodeTextSerializer : SafeTextSerializer, org.spongepowered.api.text.serializer.FormattingCodeTextSerializer {

    companion object {

        /**
         * Gets a [FormattingCodeTextSerializer] for the given formatting code.
         */
        operator fun get(code: Char): FormattingCodeTextSerializer =
                factoryOf<TextSerializerFactory>().formatting(code)
    }
}
