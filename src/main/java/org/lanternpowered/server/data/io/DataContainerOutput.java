package org.lanternpowered.server.data.io;

import java.io.IOException;

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.DataContainer;

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
    void write(DataView view) throws IOException;
}
