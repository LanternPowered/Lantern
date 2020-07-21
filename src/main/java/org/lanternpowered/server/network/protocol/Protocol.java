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
package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.LanternGame;
import org.lanternpowered.server.network.packet.MessageRegistry;

public interface Protocol {

    int CURRENT_VERSION = LanternGame.PROTOCOL_VERSION;

    /**
     * Gets the inbound {@link MessageRegistry}.
     *
     * @return The registry
     */
    MessageRegistry inbound();

    /**
     * Gets the outbound {@link MessageRegistry}.
     *
     * @return The registry
     */
    MessageRegistry outbound();
}
