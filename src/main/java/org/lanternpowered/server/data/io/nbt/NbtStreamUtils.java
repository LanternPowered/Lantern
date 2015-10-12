package org.lanternpowered.server.data.io.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.spongepowered.api.data.DataContainer;

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
    public static DataContainer read(InputStream inputStream, boolean compressed)
            throws IOException {
        NbtDataContainerInputStream input = new NbtDataContainerInputStream(inputStream, compressed);
        DataContainer dataContainer = input.read();
        input.close();
        return dataContainer;
    }

    /**
     * Writes a data container to the output stream in the nbt format.
     * 
     * @param dataContainer the data container to write
     * @param outputStream the output stream to write to
     * @param compressed whether the data should be compressed
     * @throws IOException
     */
    public static void write(DataContainer dataContainer, OutputStream outputStream,
            boolean compressed) throws IOException {
        NbtDataContainerOutputStream output = new NbtDataContainerOutputStream(outputStream, compressed);
        output.write(dataContainer);
        output.flush();
        output.close();
    }

    private NbtStreamUtils() {
    }
}
