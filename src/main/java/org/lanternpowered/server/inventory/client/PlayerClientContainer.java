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
package org.lanternpowered.server.inventory.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.behavior.HotbarBehavior;
import org.lanternpowered.server.inventory.behavior.SimpleHotbarBehavior;
import org.lanternpowered.server.network.message.Packet;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutHeldItemChange;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PlayerClientContainer extends ClientContainer {

    private static final int[] TOP_SLOT_FLAGS = new int[] {
            FLAG_REVERSE_SHIFT_INSERTION | FLAG_DISABLE_SHIFT_INSERTION, // Crafting output slot
            FLAG_DISABLE_SHIFT_INSERTION, // Crafting input slot 1
            FLAG_DISABLE_SHIFT_INSERTION, // Crafting input slot 2
            FLAG_DISABLE_SHIFT_INSERTION, // Crafting input slot 3
            FLAG_DISABLE_SHIFT_INSERTION, // Crafting input slot 4
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION | 40 << FLAG_SILENT_SLOT_INDEX_SHIFT, // Equipment slot 1
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION | 39 << FLAG_SILENT_SLOT_INDEX_SHIFT, // Equipment slot 2
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION | 38 << FLAG_SILENT_SLOT_INDEX_SHIFT, // Equipment slot 3
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION | 37 << FLAG_SILENT_SLOT_INDEX_SHIFT, // Equipment slot 4
            FLAG_DISABLE_SHIFT_INSERTION | 41 << FLAG_SILENT_SLOT_INDEX_SHIFT, // Offhand slot
    };
    private static final int OFFHAND_SLOT_INDEX = TOP_SLOT_FLAGS.length - 1;
    private static final int EQUIPMENT_START_INDEX = OFFHAND_SLOT_INDEX - 4;
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
    protected Packet createInitMessage() {
        return null;
    }

    // Originally is the offhand slot the last index, after the main inventory,
    // but we modify this to move the slot before the main inventory

    @Override
    protected int clientSlotIndexToServer(int index) {
        return index < 0 ? -1 : index == ALL_SLOT_FLAGS.length - 1 ? OFFHAND_SLOT_INDEX : index < OFFHAND_SLOT_INDEX ? index : index + 1;
    }

    @Override
    protected int serverSlotIndexToClient(int index) {
        return index < 0 ? -1 : index == OFFHAND_SLOT_INDEX ? ALL_SLOT_FLAGS.length - 1 : index < OFFHAND_SLOT_INDEX ? index : index - 1;
    }

    @Override
    protected int getShiftFlags() {
        return SHIFT_CLICK_WHEN_FULL_TOP_AND_FILTER;
    }

    @Override
    protected void collectInitMessages(List<Packet> packets) {
        super.collectInitMessages(packets);
        this.previousSelectedHotbarSlot = this.hotbarBehavior.getSelectedSlotIndex();
        packets.add(new PacketPlayInOutHeldItemChange(this.previousSelectedHotbarSlot));
    }

    @Override
    protected void collectChangeMessages(List<Packet> packets) {
        super.collectChangeMessages(packets);
        collectHotbarSlotChange(packets);
    }

    /**
     * Update for changes while a other {@link ClientContainer} is opened.
     */
    public void closedUpdate() {
        final List<Packet> packets = new ArrayList<>();
        collectHotbarSlotChange(packets);
        for (int i = EQUIPMENT_START_INDEX; i <= OFFHAND_SLOT_INDEX; i++) {
            collectSlotChangeMessages(packets, i, true);
        }
        if (!packets.isEmpty()) {
            // Stream the messages to the player
            getPlayer().getConnection().send(packets);
        }
    }

    private void collectHotbarSlotChange(List<Packet> packets) {
        final int selectedHotbarSlot = this.hotbarBehavior.getSelectedSlotIndex();
        // Update the selected hotbar slot
        if (selectedHotbarSlot != this.previousSelectedHotbarSlot) {
            this.previousSelectedHotbarSlot = selectedHotbarSlot;
            packets.add(new PacketPlayInOutHeldItemChange(selectedHotbarSlot));
        }
    }

    @Override
    public ClientSlot.Slot bindSlot(int index, AbstractSlot slot) {
        return super.bindSlot(index, slot);
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
        final int hotbarSlotIndex = (index & FLAG_HOTBAR_MASK) >> 4;
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
