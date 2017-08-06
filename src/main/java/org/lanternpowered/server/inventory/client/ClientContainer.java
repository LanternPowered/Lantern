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

import static com.google.common.base.Preconditions.checkNotNull;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.DefaultStackSizes;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetWindowSlot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
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
     * A flag that defines that the slot is present in the hotbar. The flag
     * uses 4 bits and this is the raw slot index. Counting from 1 to 9.
     */
    protected static final int FLAG_HOTBAR = 0xf0;

    /**
     * A flag that defines that only one item (item stack with quantity one)
     * can be present in the target slot.
     */
    protected static final int FLAG_ONE_ITEM = 0x100;

    /**
     * A counter for container ids.
     */
    private static int containerIdCounter = 1;

    static {
        Arrays.fill(MAIN_INVENTORY_FLAGS, 0, 27, FLAG_MAIN_INVENTORY);
        for (int i = 0; i < 9; i++) {
            // Apply the hotbar flags
            MAIN_INVENTORY_FLAGS[27 + i] = FLAG_MAIN_INVENTORY | ((i + 1) << 4);
        }
    }

    private static abstract class BaseClientSlot implements ClientSlot {

        /**
         * Whether the state is dirty.
         */
        public final static int IS_DIRTY = 0x1;

        /**
         * Whether the slot should be updated silently.
         */
        public final static int SILENT_UPDATE = 0x2;

        private int dirtyState = 0;

        protected abstract ItemStack getRaw();
    }

    private static final class EmptyClientSlot extends BaseClientSlot implements ClientSlot.Empty {

        @Override
        public ItemStack get() {
            return ItemStack.empty();
        }

        @Override
        protected ItemStack getRaw() {
            return ItemStack.empty();
        }
    }

    private static final class SlotClientSlot extends BaseClientSlot implements ClientSlot.Slot {

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

        @Override
        protected ItemStack getRaw() {
            return this.slot.getRawItemStack();
        }
    }

    private static final class IconClientSlot extends BaseClientSlot implements ClientSlot.Button {

        private ItemStack itemStack = ItemStack.empty();

        @Override
        public ItemStack get() {
            return this.itemStack.copy();
        }

        @Override
        public void setIcon(ItemStack itemStack) {
            this.itemStack = checkNotNull(itemStack, "itemStack").copy();
        }

        @Override
        protected ItemStack getRaw() {
            return this.itemStack;
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
    private final BaseClientSlot[] slots;
    private final Map<LanternSlot, SlotClientSlot> slotMap = new HashMap<>();
    private final int containerId;

    public ClientContainer(Text title) {
        this.title = title;
        final int[] flags = getSlotFlags();
        // Create a array to bind slots
        this.slots = new BaseClientSlot[flags.length];
        for (int i = 0; i < this.slots.length; i++) {
            this.slots[i] = new EmptyClientSlot();
        }
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

    /**
     * Creates a init {@link Message} that can be used to
     * open the container on the client. A {@code null} may
     * be returned if the container can't be opened by
     * the server.
     *
     * @return The init message
     */
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
        final SlotClientSlot clientSlot = new SlotClientSlot(slot);
        final BaseClientSlot oldClientSlot = this.slots[index];
        if (oldClientSlot instanceof SlotClientSlot) {
            this.slotMap.remove(((SlotClientSlot) oldClientSlot).slot);
        }
        this.slots[index] = clientSlot;
        this.slotMap.put(slot, clientSlot);
        return clientSlot;
    }

    /**
     * Binds a {@link ItemStack} as a icon to the
     * given slot index.
     *
     * @param index The slot index
     * @return The bound client slot
     */
    public ClientSlot.Button bindButton(int index) {
        final IconClientSlot clientSlot = new IconClientSlot();
        this.slots[index] = clientSlot;
        return clientSlot;
    }

    /**
     * Binds a {@link InventoryProperty} type to
     * the given {@link Supplier}.
     *
     * @param propertyType The property type
     * @param supplier The supplier
     * @param <T> The property type
     */
    public <T extends InventoryProperty<?,?>> void bindProperty(Class<T> propertyType, Supplier<T> supplier) {
        checkNotNull(propertyType, "propertyType");
        checkNotNull(supplier, "supplier");
    }

    /**
     * Binds a {@link InventoryProperty} type to
     * the given constant value.
     *
     * @param property The property
     * @param <T> The property type
     */
    public <T extends InventoryProperty<?,?>> void bindProperty(T property) {
        bindProperty((Class<T>) property.getClass(), () -> property);
    }

    /**
     * Queues a silent slot change for the specified {@link LanternSlot}.
     *
     * @param slot The slot
     */
    public void queueSlotChange(LanternSlot slot) {
        checkNotNull(slot, "slot");
        final SlotClientSlot clientSlot = this.slotMap.get(slot);
        if (clientSlot != null) {
            queueSlotChange(clientSlot);
        }
    }

    /**
     * Queues a slot change for the specified {@link ClientSlot}.
     *
     * @param clientSlot The client slot
     */
    public void queueSlotChange(ClientSlot clientSlot) {
        queueSlotChange((BaseClientSlot) clientSlot);
    }

    /**
     * Queues a slot change for the specified slot index.
     *
     * @param index The slot index
     */
    public void queueSlotChange(int index) {
        queueSlotChange(this.slots[index]);
    }

    private void queueSlotChange(BaseClientSlot clientSlot) {
        clientSlot.dirtyState = BaseClientSlot.IS_DIRTY;
    }

    private void queueSlotChangeSafely(BaseClientSlot clientSlot) {
        if ((clientSlot.dirtyState & BaseClientSlot.IS_DIRTY) == 0) {
            clientSlot.dirtyState = BaseClientSlot.IS_DIRTY;
        }
    }

    /**
     * Queues a silent slot change for the specified {@link LanternSlot}.
     *
     * @param slot The slot
     */
    public void queueSilentSlotChange(LanternSlot slot) {
        checkNotNull(slot, "slot");
        final SlotClientSlot clientSlot = this.slotMap.get(slot);
        if (clientSlot != null) {
            queueSilentSlotChange(clientSlot);
        }
    }

    /**
     * Queues a silent slot change for the specified {@link ClientSlot}.
     *
     * @param clientSlot The client slot
     */
    public void queueSilentSlotChange(ClientSlot clientSlot) {
        queueSilentSlotChange((BaseClientSlot) clientSlot);
    }

    /**
     * Queues a silent slot change for the specified slot index.
     *
     * @param index The slot index
     */
    public void queueSilentSlotChange(int index) {
        queueSilentSlotChange(this.slots[index]);
    }

    private void queueSilentSlotChange(BaseClientSlot clientSlot) {
        clientSlot.dirtyState = BaseClientSlot.IS_DIRTY | BaseClientSlot.SILENT_UPDATE;
    }

    private void queueSilentSlotChangeSafely(BaseClientSlot clientSlot) {
        if ((clientSlot.dirtyState & BaseClientSlot.IS_DIRTY) == 0) {
            clientSlot.dirtyState = BaseClientSlot.IS_DIRTY | BaseClientSlot.SILENT_UPDATE;
        }
    }

    public boolean openAndInitializeFor(Player player) {
        final Message message = createInitMessage();
        if (message == null) {
            return false;
        }
        final List<Message> messages = new ArrayList<>();
        messages.add(message);
        // Collect additional messages
        collectInitMessages(messages);
        // Stream the messages to the player
        ((LanternPlayer) player).getConnection().send(messages);
        return true;
    }

    protected void collectInitMessages(List<Message> messages) {
    }

    public void updateFor(Player player) {
        final List<Message> messages = new ArrayList<>();
        // Collect all the changes
        collectChangeMessages(messages);
        // Stream the messages to the player
        ((LanternPlayer) player).getConnection().send(messages);
    }

    protected void collectChangeMessages(List<Message> messages) {
        final int[] flags = getSlotFlags();
        for (int i = 0; i < this.slots.length; i++) {
            final BaseClientSlot slot = this.slots[i];
            if ((slot.dirtyState & BaseClientSlot.IS_DIRTY) != 0) {
                int containerId = getContainerId();
                final int index;
                final int hotbarSlot;
                // Check if we can do a silent update
                if ((slot.dirtyState & BaseClientSlot.SILENT_UPDATE) != 0 &&
                        (hotbarSlot = flags[i] & FLAG_HOTBAR) != 0) {
                    index = hotbarSlot - 1;
                    containerId = -2;
                } else {
                    index = i;
                }
                // Reset the dirty state
                slot.dirtyState = 0;
                // Add a update message
                messages.add(new MessagePlayOutSetWindowSlot(containerId, index, slot.get()));
            }
        }
        // Collect the property changes
        collectPropertyChanges(messages);
    }

    protected void collectPropertyChanges(List<Message> messages) {
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

    /**
     * Handles a shift click on the specified slot index, this will queue
     * all the slot updates that are required to force the client to revert
     * the changes.
     *
     * @param slotIndex The slot index
     */
    public void handleShiftClick(int slotIndex) {
        final int[] flags = getSlotFlags();
        // Check if the slot is in the main inventory
        final boolean main = (flags[slotIndex] & FLAG_MAIN_INVENTORY) != 0;
        final boolean hotbar = (flags[slotIndex] & FLAG_HOTBAR) != 0;
        // Get the client slot
        final BaseClientSlot slot = this.slots[slotIndex];
        ItemStack itemStack = slot.get();
        // Shift clicking on a empty slot doesn't have any effect
        if (itemStack.isEmpty()) {
            return;
        }
        // Loop reverse through the slots if the insertion is reversed
        final boolean reverse = (flags[slotIndex] & FLAG_REVERSE_SHIFT_INSERTION) != 0;
        final int start = reverse ? flags.length - 1 : 0;
        final int end = reverse ? 0 : flags.length - 1;
        final int step = reverse ? -1 : 1;
        // Get the max stack size for the shifted item
        final int maxStack = DefaultStackSizes.getOriginalMaxSize(itemStack.getType());
        final IntSet mainSlots = new IntOpenHashSet();
        for (int i = start; i <= end; i += step) {
            // Don't shift to itself
            if (i == slotIndex) {
                continue;
            }
            // You can never shift insert to this slot
            if ((flags[i] & FLAG_DISABLE_SHIFT_INSERTION) != 0) {
                continue;
            }
            final boolean main1 = (flags[i] & FLAG_MAIN_INVENTORY) != 0;
            final boolean hotbar1 = (flags[i] & FLAG_HOTBAR) != 0;
            if (main && hotbar != hotbar1) {
                mainSlots.add(i);
            }
            // Only attempt to move from bottom to top or from top to bottom inventory
            if (main == main1) {
                continue;
            }
            final BaseClientSlot slot1 = this.slots[i];
            final ItemStack itemStack1 = slot1.getRaw();
            // Get the amount of items that can be put in the stack
            final int limit = Math.min((flags[i] & FLAG_ONE_ITEM) != 0 ? 1 : 64, maxStack);
            // If the items aren't equal, they won't be able to stack anyway,
            // or if the slot is full
            if (!itemStack1.isEmpty() && (itemStack1.getQuantity() >= limit ||
                    !LanternItemStack.areSimilar(itemStack, itemStack1))) {
                continue;
            }
            // Just force the slot to update and skip it, we don't know for
            // sure that there will be an item put in it.
            if ((flags[i] & FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION) != 0) {
                // Do it silently if possible, avoid any animations
                queueSilentSlotChangeSafely(slot1);
                continue;
            }
            // Now, we take some items away from the shifted stack and continue
            // the process for the rest of the slots
            final int removed = limit - itemStack1.getQuantity();
            if (removed > 0) {
                itemStack.setQuantity(itemStack.getQuantity() - removed);
                // Do it silently if possible, avoid any animations
                queueSilentSlotChangeSafely(slot1);
            }
            // We are at the end, the stack is empty
            if (itemStack.isEmpty()) {
                return;
            }
        }
        // Shift between main and hotbar for the rest of the stack
        if (disableShiftClickWhenFull()) {
            return;
        }
        for (int i : mainSlots.toIntArray()) {
            // No need to check if shifting is disabled, it will always work
            // for the main inventory
            final boolean hotbar1 = (flags[i] & FLAG_HOTBAR) != 0;
            // Only move between hotbar and main
            if (hotbar == hotbar1) {
                continue;
            }
            final BaseClientSlot slot1 = this.slots[i];
            final ItemStack itemStack1 = slot1.getRaw();
            // Get the amount of items that can be put in the stack
            final int limit = Math.min((flags[i] & FLAG_ONE_ITEM) != 0 ? 1 : 64, maxStack);
            // If the items aren't equal, they won't be able to stack anyway,
            // or if the slot is full
            if (!itemStack1.isEmpty() && (itemStack1.getQuantity() >= limit ||
                    !LanternItemStack.areSimilar(itemStack, itemStack1))) {
                continue;
            }
            // Now, we take some items away from the shifted stack and continue
            // the process for the rest of the slots
            final int removed = limit - itemStack1.getQuantity();
            if (removed > 0) {
                itemStack.setQuantity(itemStack.getQuantity() - removed);
                // Do it silently if possible, avoid any animations
                queueSilentSlotChangeSafely(slot1);
            }
            // We are at the end, the stack is empty
            if (itemStack.isEmpty()) {
                return;
            }
        }
    }
}
