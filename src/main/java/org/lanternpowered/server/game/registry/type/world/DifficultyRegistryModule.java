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
package org.lanternpowered.server.game.registry.type.world;

import org.lanternpowered.server.game.registry.AdditionalInternalPluginCatalogRegistryModule;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;

public final class DifficultyRegistryModule extends AdditionalInternalPluginCatalogRegistryModule<Difficulty> {

    private static final DifficultyRegistryModule instance = new DifficultyRegistryModule();

    public static DifficultyRegistryModule get() {
        return instance;
    }

    private DifficultyRegistryModule() {
        super(Difficulties.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternDifficulty(CatalogKey.minecraft("peaceful"), 0));
        register(new LanternDifficulty(CatalogKey.minecraft("easy"), 1));
        register(new LanternDifficulty(CatalogKey.minecraft("normal"), 2));
        register(new LanternDifficulty(CatalogKey.minecraft("hard"), 3));
    }
}
