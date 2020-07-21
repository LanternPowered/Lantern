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

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import org.lanternpowered.server.network.message.Packet;
import org.spongepowered.api.data.persistence.DataView;

import java.util.List;

/**
 * Represents the blockEntity entity data to send for a specific chunk section.
 */
public interface BlockEntityChunkProtocolData {

    Short2ObjectMap<DataView> getInitData();

    List<Packet> getPackets();
}
