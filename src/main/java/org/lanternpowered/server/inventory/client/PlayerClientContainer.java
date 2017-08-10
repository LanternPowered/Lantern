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
package org.lanternpowered.server.inventory.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.behavior.HotbarBehavior;
import org.lanternpowered.server.inventory.behavior.SimpleHotbarBehavior;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutHeldItemChange;
import org.spongepowered.api.text.Text;

import java.util.List;

public class PlayerClientContainer extends ClientContainer {

    private static final int[] TOP_SLOT_FLAGS = new int[] {
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
    private static final int OFFHAND_SLOT_INDEX = TOP_SLOT_FLAGS.length - 1;
    private static final int[] ALL_SLOT_FLAGS = compileAllSlotFlags(TOP_SLOT_FLAGS);

    private HotbarBehavior hotbarBehavior = new SimpleHotbarBehavior();
    private int previousSelectedHotbarSlot;

    public PlayerClientContainer(Text title) {
        super(title);
    }

    @Override
    protected int[] getTopSlotFlags() {
        return TOP_SLOT_FLAGS;
    }

    @Override
    protected int[] getSlotFlags() {
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

    // Originally is the offhand slot the last index, after the main inventory,
    // but we modify this to move the slot before the main inventory

    @Override
    protected int clientSlotIndexToServer(int index) {
        return index == ALL_SLOT_FLAGS.length - 1 ? OFFHAND_SLOT_INDEX : index < OFFHAND_SLOT_INDEX ? index : index + 1;
    }

    @Override
    protected int serverSlotIndexToClient(int index) {
        return index == OFFHAND_SLOT_INDEX ? ALL_SLOT_FLAGS.length - 1 : index < OFFHAND_SLOT_INDEX ? index : index - 1;
    }

    @Override
    protected boolean disableShiftClickWhenFull() {
        return false;
    }

    @Override
    protected void collectInitMessages(List<Message> messages) {
        super.collectInitMessages(messages);
        this.previousSelectedHotbarSlot = this.hotbarBehavior.getSelectedSlotIndex();
        messages.add(new MessagePlayInOutHeldItemChange(this.previousSelectedHotbarSlot));
    }

    @Override
    protected void collectChangeMessages(List<Message> messages) {
        super.collectChangeMessages(messages);
        final int selectedHotbarSlot = this.hotbarBehavior.getSelectedSlotIndex();
        // Update the selected hotbar slot
        if (selectedHotbarSlot != this.previousSelectedHotbarSlot) {
            this.previousSelectedHotbarSlot = selectedHotbarSlot;
            messages.add(new MessagePlayInOutHeldItemChange(selectedHotbarSlot));
        }
    }

    public void handleHeldItemChange(int hotbarSlotIndex) {
        // We don't need to send an update if the client switches the held item
        this.previousSelectedHotbarSlot = hotbarSlotIndex;
        this.hotbarBehavior.handleSelectedSlotChange(this, hotbarSlotIndex);
    }

    /**
     * Binds the {@link HotbarBehavior}.
     *
     * @param hotbarBehavior The hotbar behavior
     */
    public void bindHotbarBehavior(HotbarBehavior hotbarBehavior) {
        checkNotNull(hotbarBehavior, "hotbarBehavior");
        this.hotbarBehavior = hotbarBehavior;
    }

    /**
     * Sets the selected hotbar by using the {@link ClientSlot}. The
     * {@link ClientSlot} must be located in the hotbar.
     *
     * @param hotbarSlot The hotbar client slot
     */
    public void setSelectedHotbarSlot(ClientSlot hotbarSlot) {
        checkNotNull(hotbarSlot, "hotbarSlot");
        final int index = ((BaseClientSlot) hotbarSlot).index;
        final int hotbarSlotIndex = (index & FLAG_HOTBAR) >> 4;
        checkArgument(hotbarSlotIndex != 0, "The client slot isn't located in the hotbar.");
        setSelectedHotbarSlotIndex(hotbarSlotIndex - 1);
    }

    /**
     * Gets the currently selected hotbar {@link ClientSlot}.
     *
     * @return The hotbar client slot
     */
    public ClientSlot getSelectedHotbarSlot() {
        return this.slots[getHotbarSlotIndex(getSelectedHotbarSlotIndex())];
    }

    /**
     * Sets the selected hotbar slot index. To get the hotbar slot index
     * within a {@link ClientContainer}, use {@link #getHotbarSlotIndex(int)}.
     * This value varies between 0 and 8.
     *
     * @param hotbarSlotIndex The hotbar slot index
     */
    public void setSelectedHotbarSlotIndex(int hotbarSlotIndex) {
        checkArgument(hotbarSlotIndex >= 0 && hotbarSlotIndex <= 8);
        this.hotbarBehavior.setSelectedSlotIndex(hotbarSlotIndex);
    }

    /**
     * Gets the selected hotbar slot index. To get the hotbar slot index
     * within a {@link ClientContainer}, use {@link #getHotbarSlotIndex(int)}.
     * This value varies between 0 and 8.
     *
     * @return The hotbar slot index
     */
    public int getSelectedHotbarSlotIndex() {
        final int hotbarSlotIndex = this.hotbarBehavior.getSelectedSlotIndex();
        checkArgument(hotbarSlotIndex >= 0 && hotbarSlotIndex <= 8);
        return hotbarSlotIndex;
    }
}
