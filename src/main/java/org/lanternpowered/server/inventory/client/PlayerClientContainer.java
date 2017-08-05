package org.lanternpowered.server.inventory.client;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.text.Text;

public class PlayerClientContainer extends ClientContainer {

    private static final int[] SLOT_FLAGS = new int[] {
            FLAG_REVERSE_SHIFT_INSERTION | FLAG_DISABLE_SHIFT_INSERTION, // Crafting output slot
            FLAG_DISABLE_SHIFT_INSERTION, // Crafting input slot 1
            FLAG_DISABLE_SHIFT_INSERTION, // Crafting input slot 2
            FLAG_DISABLE_SHIFT_INSERTION, // Crafting input slot 3
            FLAG_DISABLE_SHIFT_INSERTION, // Crafting input slot 4
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION, // Equipment slot 1
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION, // Equipment slot 2
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION, // Equipment slot 3
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION, // Equipment slot 4
            FLAG_DISABLE_SHIFT_INSERTION, // Offhand slot
    };
    private static final int[] ALL_SLOT_FLAGS;

    static {
        ALL_SLOT_FLAGS = new int[SLOT_FLAGS.length + MAIN_INVENTORY_FLAGS.length];
        System.arraycopy(SLOT_FLAGS, 0, ALL_SLOT_FLAGS, 0, SLOT_FLAGS.length - 1);
        System.arraycopy(MAIN_INVENTORY_FLAGS, 0, ALL_SLOT_FLAGS, SLOT_FLAGS.length - 1, MAIN_INVENTORY_FLAGS.length);
        // The offhand slot uses the last index
        ALL_SLOT_FLAGS[ALL_SLOT_FLAGS.length - 1] = SLOT_FLAGS[SLOT_FLAGS.length - 1];
    }

    public PlayerClientContainer(Text title) {
        super(title);
    }

    @Override
    protected int[] getSlotFlags() {
        return SLOT_FLAGS;
    }

    @Override
    protected int[] getAllSlotFlags() {
        return ALL_SLOT_FLAGS;
    }

    @Override
    protected int generateContainerId() {
        // A player container id is always 0
        return 0;
    }

    @Override
    protected Message createInitMessage() {
        return null;
    }
}
