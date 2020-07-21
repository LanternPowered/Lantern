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

public final class PacketPlayOutEntityCollectItem implements Packet {

    private final int collectorId;
    private final int collectedId;
    private final int collectItemCount;

    public PacketPlayOutEntityCollectItem(int collectorId, int collectedId, int collectItemCount) {
        this.collectorId = collectorId;
        this.collectedId = collectedId;
        this.collectItemCount = collectItemCount;
    }

    public int getCollectorId() {
        return this.collectorId;
    }

    public int getCollectedId() {
        return this.collectedId;
    }

    public int getCollectItemCount() {
        return this.collectItemCount;
    }
}
