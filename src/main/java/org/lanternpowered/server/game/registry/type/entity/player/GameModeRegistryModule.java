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
package org.lanternpowered.server.game.registry.type.entity.player;

import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.game.registry.AdditionalInternalPluginCatalogRegistryModule;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

public final class GameModeRegistryModule extends AdditionalInternalPluginCatalogRegistryModule<GameMode> {

    private static final GameModeRegistryModule instance = new GameModeRegistryModule();

    public static GameModeRegistryModule get() {
        return instance;
    }

    private GameModeRegistryModule() {
        super(GameModes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternGameMode("minecraft", "not_set", "gameMode.notSet", -1,
                player -> {}));
        register(new LanternGameMode("minecraft", "survival", "gameMode.survival", 0,
                player -> {
                    player.offer(Keys.CAN_FLY, false);
                    player.offer(Keys.IS_FLYING, false);
                    player.offer(Keys.INVULNERABLE, false);
                }));
        register(new LanternGameMode("minecraft", "creative", "gameMode.creative", 1,
                player -> {
                    player.offer(Keys.CAN_FLY, true);
                    player.offer(Keys.INVULNERABLE, true);
                }));
        register(new LanternGameMode("minecraft", "adventure", "gameMode.adventure", 2,
                player -> {
                    player.offer(Keys.CAN_FLY, false);
                    player.offer(Keys.IS_FLYING, false);
                    player.offer(Keys.INVULNERABLE, false);
                }));
        register(new LanternGameMode("minecraft", "spectator", "gameMode.spectator", 3,
                player -> {
                    player.offer(Keys.CAN_FLY, true);
                    player.offer(Keys.IS_FLYING, true);
                    player.offer(Keys.INVULNERABLE, true);
                }));
    }
}
