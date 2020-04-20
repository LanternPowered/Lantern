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
 * The legacy text serializer.
 */
@Deprecated(message = "Legacy formatting codes are being phased out of minecraft.")
interface LegacyTextSerializer : FormattingCodeTextSerializer {

    companion object : LegacyTextSerializer by factoryOf<TextSerializerFactory>().legacy
}
