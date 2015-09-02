package org.lanternpowered.server.item;

import javax.annotation.Nullable;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.item.ItemType;

public class LanternItems {

    private static LanternItemRegistry registry;

    /**
     * Gets the {@link LanternItemRegistry}.
     * 
     * @return the block registry
     */
    public static LanternItemRegistry getRegistry() {
        if (registry == null) {
            registry = LanternGame.get().getRegistry().getItemRegistry();
        }
        return registry;
    }

    @Nullable
    public static ItemType getById(int internalId) {
        return getRegistry().getById(internalId);
    }

    @Nullable
    public static Short getId(ItemType itemType) {
        return getRegistry().getId(itemType);
    }

}
