package org.lanternpowered.server.world.biome;

import javax.annotation.Nullable;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.world.biome.BiomeType;

public class LanternBiomes {

    private static LanternBiomeRegistry registry;

    /**
     * Gets the {@link LanternBiomeRegistry}.
     * 
     * @return the block registry
     */
    public static LanternBiomeRegistry getRegistry() {
        if (registry == null) {
            registry = LanternGame.get().getRegistry().getBiomeRegistry();
        }
        return registry;
    }

    @Nullable
    public static BiomeType getById(int biomeId) {
        return getRegistry().getById(biomeId);
    }

    @Nullable
    public static Short getId(BiomeType biomeType) {
        return getRegistry().getId(biomeType);
    }
}
