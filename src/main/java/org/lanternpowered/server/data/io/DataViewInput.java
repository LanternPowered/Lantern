package org.lanternpowered.server.data.io;

import java.io.IOException;

import org.spongepowered.api.data.DataView;

/**
 * A input that can be used to read data views.
 */
public interface DataViewInput {

    /**
     * Reads a data view from the input.
     * 
     * @return the data view
     * @throws IOException when a i/o error occurred
     */
    DataView read() throws IOException;
}
