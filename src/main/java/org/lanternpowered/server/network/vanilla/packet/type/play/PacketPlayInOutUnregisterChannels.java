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
package org.lanternpowered.server.network.vanilla.packet.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.packet.Packet;

import java.util.Set;

public final class PacketPlayInOutUnregisterChannels implements Packet {

    private final Set<String> channels;

    /**
     * Creates a new unregister channels message.
     * 
     * @param channels the channels
     */
    public PacketPlayInOutUnregisterChannels(Set<String> channels) {
        this.channels = checkNotNull(channels, "channels");
    }

    /**
     * Gets the channels.
     * 
     * @return the channels
     */
    public Set<String> getChannels() {
        return this.channels;
    }

}
