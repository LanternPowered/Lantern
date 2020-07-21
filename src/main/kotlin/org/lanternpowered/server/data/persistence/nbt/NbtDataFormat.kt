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
package org.lanternpowered.server.data.persistence.nbt

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.server.data.persistence.AbstractDataFormat
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.persistence.InvalidDataFormatException

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class NbtDataFormat(key: ResourceKey) : AbstractDataFormat(key) {

    override fun readFrom(input: InputStream): DataContainer = NbtStreamUtils.read(input, false)
    override fun writeTo(output: OutputStream, data: DataView) = NbtStreamUtils.write(data, output, false)
}
