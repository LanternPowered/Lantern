/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.lanternpowered.server.world.dimension.LanternDimensionType;

public final class MessagePlayOutPlayerJoinGame implements Message {

    private final int entityId;
    private final int playerListSize;

    private final LanternGameMode gameMode;
    private final LanternDifficulty difficulty;
    private final LanternDimensionType dimensionType;

    // Reduce info on the debug screen
    private final boolean reducedDebug;

    // Whether the hardcore mode is enabled, there is currently
    // no other way to change this on the client.
    // This will change the appearance of the hearts.
    private final boolean hardcore;

    private final boolean lowHorizon;

    public MessagePlayOutPlayerJoinGame(LanternGameMode gameMode, LanternDimensionType dimensionType,
            LanternDifficulty difficulty, int entityId, int playerListSize,
            boolean reducedDebug, boolean hardcore, boolean lowHorizon) {
        this.dimensionType = checkNotNull(dimensionType, "dimensionType");
        this.difficulty = checkNotNull(difficulty, "difficulty");
        this.gameMode = checkNotNull(gameMode, "gameMode");
        this.playerListSize = playerListSize;
        this.reducedDebug = reducedDebug;
        this.hardcore = hardcore;
        this.entityId = entityId;
        this.lowHorizon = lowHorizon;
    }

    /**
     * Gets whether the hardcore mode is enabled.
     * 
     * @return hardcore mode
     */
    public boolean isHardcore() {
        return this.hardcore;
    }

    /**
     * Gets the entity id of the player.
     * 
     * @return the entity id
     */
    public int getEntityId() {
        return this.entityId;
    }

    /**
     * Gets the size of the player list.
     * 
     * @return the list size
     */
    public int getPlayerListSize() {
        return this.playerListSize;
    }

    /**
     * Gets the game mode of the player.
     * 
     * @return the game mode
     */
    public LanternGameMode getGameMode() {
        return this.gameMode;
    }

    /**
     * Gets the dimension type of the world this player is currently in.
     * 
     * @return the dimension type
     */
    public LanternDimensionType getDimensionType() {
        return this.dimensionType;
    }

    /**
     * Gets the difficulty of the world this player is currently in.
     * 
     * @return the difficulty
     */
    public LanternDifficulty getDifficulty() {
        return this.difficulty;
    }

    /**
     * Gets whether reduced debug should be used, no idea what this will do,
     * maybe less information in the f3 screen?
     * 
     * @return reduced debug
     */
    public boolean getReducedDebug() {
        return this.reducedDebug;
    }

    public boolean isLowHorizon() {
        return this.lowHorizon;
    }
}
