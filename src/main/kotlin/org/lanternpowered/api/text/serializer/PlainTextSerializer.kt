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

/**
 * A text serializer for plain text.
 */
interface PlainTextSerializer : SafeTextSerializer {

    /**
     * The singleton instance of [PlainTextSerializer].
     */
    companion object : PlainTextSerializer by factoryOf<TextSerializerFactory>().plain
}
