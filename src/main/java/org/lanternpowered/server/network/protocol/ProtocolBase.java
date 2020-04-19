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

import org.lanternpowered.server.network.message.MessageRegistry;

class ProtocolBase implements Protocol {

    private final MessageRegistry inbound = new MessageRegistry();
    private final MessageRegistry outbound = new MessageRegistry();

    @Override
    public MessageRegistry inbound() {
        return this.inbound;
    }

    @Override
    public MessageRegistry outbound() {
        return this.outbound;
    }

}
