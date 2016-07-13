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
package org.lanternpowered.server.game.registry.type.entity.player;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import java.util.Optional;

public final class GameModeRegistryModule extends AdditionalPluginCatalogRegistryModule<GameMode> {

    public static GameModeRegistryModule getInstance() {
        return Holder.INSTANCE;
    }

    private final Int2ObjectMap<GameMode> byInternalId = new Int2ObjectOpenHashMap<>();

    public GameModeRegistryModule() {
        super(GameModes.class);
    }

    @Override
    protected void register(GameMode catalogType, boolean disallowInbuiltPluginIds) {
        super.register(catalogType, disallowInbuiltPluginIds);
        this.byInternalId.putIfAbsent((int) ((LanternGameMode) catalogType).getInternalId(), catalogType);
    }

    @Override
    public void registerDefaults() {
        this.register(new LanternGameMode("minecraft", "not_set", -1));
        this.register(new LanternGameMode("minecraft", "survival", 0));
        this.register(new LanternGameMode("minecraft", "creative", 1));
        this.register(new LanternGameMode("minecraft", "adventure", 2));
        this.register(new LanternGameMode("minecraft", "spectator", 3));
    }

    public Optional<GameMode> getByInternalId(int internalId) {
        return Optional.ofNullable(this.byInternalId.get(internalId));
    }

    private static final class Holder {
        private static final GameModeRegistryModule INSTANCE = new GameModeRegistryModule();
    }
}
