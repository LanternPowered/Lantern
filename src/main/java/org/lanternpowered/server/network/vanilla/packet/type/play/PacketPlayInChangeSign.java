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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Packet;
import org.spongepowered.math.vector.Vector3i;

public final class PacketPlayInChangeSign implements Packet {

    private final Vector3i position;
    private final String[] lines;

    public PacketPlayInChangeSign(Vector3i position, String[] lines) {
        this.position = checkNotNull(position, "position");
        checkNotNull(lines, "lines");
        checkArgument(lines.length == 4, "lines length must be 4");
        this.lines = lines;
    }

    /**
     * Gets the sign position of this message.
     * 
     * @return the position
     */
    public Vector3i getPosition() {
        return this.position;
    }

    /**
     * Gets the lines of the sign.
     * 
     * @return the lines
     */
    public String[] getLines() {
        return this.lines;
    }
}
