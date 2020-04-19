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
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayOutSetEntityPassengers implements Message {

    private final int entityId;
    private final int[] passengerIds;

    public MessagePlayOutSetEntityPassengers(int entityId, int... passengerIds) {
        this.entityId = entityId;
        this.passengerIds = passengerIds;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int[] getPassengersIds() {
        return this.passengerIds;
    }
}
