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
import org.spongepowered.api.util.Direction;
import org.spongepowered.math.vector.Vector3i;

public final class PacketPlayInPlayerDigging implements Packet {

    private final Action action;
    private final Vector3i position;
    private final Direction face;

    public PacketPlayInPlayerDigging(Action action, Vector3i position, Direction face) {
        this.position = position;
        this.action = action;
        this.face = face;
    }

    public Action getAction() {
        return this.action;
    }

    public Vector3i getPosition() {
        return this.position;
    }

    public Direction getFace() {
        return this.face;
    }

    public enum Action {
        START,
        CANCEL,
        FINISH,
    }
}
