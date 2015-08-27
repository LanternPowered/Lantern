package org.lanternpowered.server.data.io;

import java.io.IOException;

import org.spongepowered.api.data.DataContainer;

/**
 * A input that can be used to read data views.
 */
public interface DataContainerInput {

    /**
     * Reads a {@link DataContainer} from the input.
     * 
     * @return the data view
     * @throws IOException when a i/o error occurred
     */
    DataContainer read() throws IOException;
}
