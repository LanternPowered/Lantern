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
package org.lanternpowered.server.network.block;

/**
 * Internal access only.
 */
public final class BlockEntityProtocolHelper {

    public static void update(AbstractBlockEntityProtocol protocol, BlockEntityProtocolUpdateContext context, int elapsedTicks) {
        protocol.updateTickCounter += elapsedTicks;
        if (protocol.updateTickCounter > protocol.getUpdateInterval()) {
            protocol.update(context);
            protocol.updateTickCounter = 0;
        }
    }

    public static void init(AbstractBlockEntityProtocol protocol, BlockEntityProtocolUpdateContext context) {
        protocol.init(context);
    }
}
