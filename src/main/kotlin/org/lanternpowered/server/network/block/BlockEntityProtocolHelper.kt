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
package org.lanternpowered.server.network.block

/**
 * Internal access only.
 */
object BlockEntityProtocolHelper {

    @JvmStatic
    fun update(protocol: BlockEntityProtocol<*>, context: BlockEntityProtocolUpdateContext, elapsedTicks: Int) {
        protocol.updateTickCounter += elapsedTicks
        if (protocol.updateTickCounter > protocol.updateInterval) {
            protocol.update(context)
            protocol.updateTickCounter = 0
        }
    }

    @JvmStatic
    fun init(protocol: BlockEntityProtocol<*>, context: BlockEntityProtocolUpdateContext) {
        protocol.init(context)
    }
}
