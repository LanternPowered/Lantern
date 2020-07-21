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

import org.lanternpowered.server.network.message.Packet;
import org.lanternpowered.server.network.vanilla.command.RootNode;

public final class PacketPlayOutDefineCommands implements Packet {

    private final RootNode rootNode;

    public PacketPlayOutDefineCommands(RootNode rootNode) {
        this.rootNode = rootNode;
    }

    public RootNode getRootNode() {
        return this.rootNode;
    }

}
