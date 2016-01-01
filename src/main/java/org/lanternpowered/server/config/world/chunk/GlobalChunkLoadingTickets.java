/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
public class GlobalChunkLoadingTickets extends WorldChunkLoadingTickets {

    @Setting(value = ChunkLoading.PLAYER_TICKET_COUNT, comment =
            "The number of tickets a player can be assigned instead of a plugin. This is shared\n" +
            "across all plugins.")
    private int playerTicketCount = 500;

    /**
     * Gets the maximum amount of tickets that can be requested
     * per player.
     * 
     * TODO: Make this configurable per world? This is currently not possible using the current api.
     * 
     * @return the player ticket count
     */
    public int getPlayerTicketCount() {
        return this.playerTicketCount;
    }

}
