package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.network.message.MessageRegistry;

public interface Protocol {

    /**
     * Gets the inbound message registry.
     * 
     * @return the registry
     */
    MessageRegistry inbound();

    /**
     * Gets the outbound message registry.
     * 
     * @return the registry
     */
    MessageRegistry outbound();
}
