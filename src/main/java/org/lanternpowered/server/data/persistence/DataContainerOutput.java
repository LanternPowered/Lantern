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
package org.lanternpowered.server.data.persistence;

import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataFormatException;

import java.io.IOException;

/**
 * A output that can be used to write data views.
 */
public interface DataContainerOutput {

    /**
     * Writes a {@link DataView} or {@link DataContainer} to the output.
     * 
     * @param view the data view
     * @throws IOException when a i/o error occurred
     */
    void write(DataView view) throws InvalidDataFormatException, IOException;

}
