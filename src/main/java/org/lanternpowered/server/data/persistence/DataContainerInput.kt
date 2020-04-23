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
package org.lanternpowered.server.data.persistence

import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.InvalidDataFormatException
import java.io.IOException

/**
 * A input that can be used to read data views.
 */
interface DataContainerInput {

    /**
     * Reads a [DataContainer] from the input.
     *
     * @return The data container
     * @throws IOException If an IO error occurred
     * @throws InvalidDataFormatException If the data container can't be read
     */
    @Throws(InvalidDataFormatException::class, IOException::class)
    fun read(): DataContainer
}
