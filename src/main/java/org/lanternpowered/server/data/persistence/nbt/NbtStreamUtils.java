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
package org.lanternpowered.server.data.persistence.nbt;

import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class NbtStreamUtils {

    /**
     * Reads a data container from a input stream that contains
     * data with the nbt format.
     * 
     * @param inputStream the input stream
     * @param compressed whether the data is compressed
     * @return the data container
     * @throws IOException
     */
    public static DataContainer read(InputStream inputStream, boolean compressed) throws IOException {
        try (NbtDataContainerInputStream input = new NbtDataContainerInputStream(
                compressed ? new GZIPInputStream(inputStream) : inputStream)) {
            return input.read();
        }
    }

    /**
     * Writes a data container (view) to the output stream in the nbt format.
     * 
     * @param dataView the data view to write
     * @param outputStream the output stream to write to
     * @param compressed whether the data should be compressed
     * @throws IOException
     */
    public static void write(DataView dataView, OutputStream outputStream, boolean compressed) throws IOException {
        try (NbtDataContainerOutputStream output = new NbtDataContainerOutputStream(
                compressed ? new GZIPOutputStream(outputStream) : outputStream)) {
            output.write(dataView);
            output.flush();
        }
    }

    private NbtStreamUtils() {
    }

}
