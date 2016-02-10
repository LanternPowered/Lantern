/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.config.world.chunk;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Global/default settings of the chunk loading, all these settings are available in
 * the global config and the world specific configs.
 */
@ConfigSerializable
public class WorldChunkLoadingTickets implements ChunkLoadingTickets {

    @Setting(value = ChunkLoading.MAXIMUM_CHUNKS_PER_TICKET, comment =
            "The default maximum number of chunks a plugin can force, per ticket, for a plugin\n " +
            "without an override. This is the maximum number of chunks a single ticket can force.")
    private int maximumChunksPerTicket = 25;

    @Setting(value = ChunkLoading.MAXIMUM_TICKET_COUNT, comment =
            "The default maximum ticket count for a plugin which does not have an override\n " +
            "in this file. This is the number of chunk loading requests a plugin is allowed to make.")
    private int maximumTicketCount = 200;

    @Override
    public int getMaximumChunksPerTicket() {
        return this.maximumChunksPerTicket;
    }

    @Override
    public int getMaximumTicketCount() {
        return this.maximumTicketCount;
    }

}
