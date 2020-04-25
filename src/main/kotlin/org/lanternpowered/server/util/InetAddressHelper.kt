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
package org.lanternpowered.server.util

import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException

object InetAddressHelper {

    // https://stackoverflow.com/questions/2406341/how-to-check-if-an-ip-address-is-the-local-host-on-a-multi-homed-system
    fun isLocalAddress(address: InetAddress): Boolean {
        // Check if the address is a valid special local or loop back
        return if (address.isAnyLocalAddress || address.isLoopbackAddress) {
            true
        } else try {
            // Check if the address is defined on any interface
            NetworkInterface.getByInetAddress(address) != null
        } catch (e: SocketException) {
            false
        }
    }
}
