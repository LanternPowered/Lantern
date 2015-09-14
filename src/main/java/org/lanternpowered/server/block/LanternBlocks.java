package org.lanternpowered.server.block;

import org.lanternpowered.server.game.LanternGame;

public class LanternBlocks {

    private static BlockRegistry registry;

    /**
     * Gets the {@link BlockRegistry}.
     * 
     * @return the block registry
     */
    public static BlockRegistry reg() {
        if (registry == null) {
            registry = LanternGame.get().getRegistry().getBlockRegistry();
        }
        return registry;
    }
}
