/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.data.persistence.nbt;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;

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
