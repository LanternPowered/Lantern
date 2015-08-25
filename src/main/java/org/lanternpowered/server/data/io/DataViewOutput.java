package org.lanternpowered.server.data.io;

import java.io.IOException;

import org.spongepowered.api.data.DataView;

/**
 * A output that can be used to write data views.
 */
public interface DataViewOutput {

    /**
     * Writes a data view to the output.
     * 
     * @param view the data view
     * @throws IOException when a i/o error occurred
     */
    void write(DataView view) throws IOException;
}
