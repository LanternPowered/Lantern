package org.lanternpowered.server.network.vanilla.message.type.play;

import javax.annotation.Nullable;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.item.inventory.ItemStack;

public final class MessagePlayOutSetWindowSlot implements Message {

    private final int index;
    private final int window;

    @Nullable private final ItemStack itemStack;

    public MessagePlayOutSetWindowSlot(int window, int index, @Nullable ItemStack itemStack) {
        this.itemStack = itemStack;
        this.window = window;
        this.index = index;
    }

    /**
     * Gets the item stack that should be set at the index.
     * 
     * @return the item stack
     */
    public @Nullable ItemStack getItem() {
        return this.itemStack;
    }

    /**
     * Gets the index of the slot.
     * 
     * @return the index
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Gets the window id.
     * 
     * @return the window id
     */
    public int getWindow() {
        return this.window;
    }

}
