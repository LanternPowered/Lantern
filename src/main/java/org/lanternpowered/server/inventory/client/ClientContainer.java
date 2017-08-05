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

import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Arrays;

import javax.annotation.Nullable;

public abstract class ClientContainer {

    protected static final int[] MAIN_INVENTORY_FLAGS = new int[36];

    /**
     * A flag that enables reverse shift insertion behavior from the target slot.
     */
    protected static final int FLAG_REVERSE_SHIFT_INSERTION = 0x1;

    /**
     * A flag that disables shift operations to the target slot.
     */
    protected static final int FLAG_DISABLE_SHIFT_INSERTION = 0x2;

    /**
     * A flag that defines that not all the shift operation may succeed. For example,
     * in furnaces can the shift operation only succeed if the shifted item
     * is smeltable/cookable.
     */
    protected static final int FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION = 0x4;

    /**
     * A flag that defines that the slot is present in the main inventory.
     */
    protected static final int FLAG_MAIN_INVENTORY = 0x8;

    /**
     * A counter for container ids.
     */
    private static int containerIdCounter = 1;

    static {
        Arrays.fill(MAIN_INVENTORY_FLAGS, FLAG_MAIN_INVENTORY);
    }

    private static final class EmptyClientSlot implements ClientSlot.Empty {

        static final EmptyClientSlot INSTANCE = new EmptyClientSlot();

        @Override
        public ItemStack get() {
            return ItemStack.empty();
        }
    }

    private static final class SlotClientSlot implements ClientSlot.Slot {

        private final LanternSlot slot;

        private SlotClientSlot(LanternSlot slot) {
            this.slot = slot;
        }

        @Override
        public ItemStack get() {
            return this.slot.peek().orElse(ItemStack.empty());
        }

        @Override
        public LanternSlot getSlot() {
            return this.slot;
        }
    }

    private static final class IconClientSlot implements ClientSlot.Icon {

        private ItemStack itemStack = ItemStack.empty();

        @Override
        public ItemStack get() {
            return this.itemStack.copy();
        }

        @Override
        public void set(ItemStack itemStack) {
            this.itemStack = checkNotNull(itemStack, "itemStack").copy();
        }
    }

    /**
     * Compiles a new slot flags array which includes the
     * flags of the main player inventory (36 slots).
     *
     * @param flags The slot flags
     * @return The new slot flags
     */
    protected static int[] compileAllSlotFlags(int[] flags) {
        final int[] allFlags = new int[flags.length + MAIN_INVENTORY_FLAGS.length];
        System.arraycopy(flags, 0, allFlags, 0, flags.length);
        System.arraycopy(MAIN_INVENTORY_FLAGS, 0, allFlags, flags.length, MAIN_INVENTORY_FLAGS.length);
        return allFlags;
    }

    private final Text title;
    private final ClientSlot[] slots;
    private final int containerId;

    public ClientContainer(Text title) {
        this.title = title;
        final int[] flags = getSlotFlags();
        // Create a array to bind slots
        this.slots = new ClientSlot[flags.length];
        Arrays.fill(this.slots, EmptyClientSlot.INSTANCE);
        // Generate a new container id
        this.containerId = generateContainerId();
    }

    /**
     * Gets the title.
     *
     * @return The title
     */
    public Text getTitle() {
        return this.title;
    }

    /**
     * Gets the container id.
     *
     * @return The container id
     */
    public int getContainerId() {
        return this.containerId;
    }

    /**
     * Generates a container id.
     *
     * @return The container id
     */
    protected int generateContainerId() {
        final int containerId = containerIdCounter++;
        if (containerIdCounter >= 100) {
            containerIdCounter = 1;
        }
        return containerId;
    }

    @Nullable
    protected abstract Message createInitMessage();

    /**
     * Binds a {@link LanternSlot} to the
     * given slot index.
     *
     * @param index The slot index
     * @return The bound client slot
     */
    public ClientSlot.Slot bindSlot(int index, LanternSlot slot) {
        checkArgument(this.slots[index] == null, "Slot index %s is already bound", index);
        final SlotClientSlot clientSlot = new SlotClientSlot(slot);
        this.slots[index] = clientSlot;
        return clientSlot;
    }

    /**
     * Binds a {@link ItemStack} as a icon to the
     * given slot index.
     *
     * @param index The slot index
     * @return The bound client slot
     */
    public ClientSlot.Icon bindIcon(int index) {
        checkArgument(this.slots[index] == null, "Slot index %s is already bound", index);
        final IconClientSlot clientSlot = new IconClientSlot();
        this.slots[index] = clientSlot;
        return clientSlot;
    }

    /**
     * Gets a {@link ClientSlot} for the given slot index.
     *
     * @param index The slot index
     * @return The client slot if present, otherwise {@code null}
     */
    @Nullable
    public ClientSlot getSlot(int index) {
        return index < 0 || index >= this.slots.length ? null : this.slots[index];
    }

    /**
     * Gets a array that contains all the flags
     * of the slots in this container. This does
     * not include the flags of the main player
     * inventory.
     *
     * @return The slot flags
     */
    protected abstract int[] getSlotFlags();

    /**
     * Gets a array that contains all the flags
     * of the slots in this container.
     *
     * @return The slot flags
     */
    protected int[] getAllSlotFlags() {
        return compileAllSlotFlags(getSlotFlags());
    }

    /**
     * Gets whether shift clicking should be
     * disabled when the top inventory is full.
     *
     * @return Disable shift click
     */
    protected boolean disableShiftClickWhenFull() {
        return true;
    }
}
