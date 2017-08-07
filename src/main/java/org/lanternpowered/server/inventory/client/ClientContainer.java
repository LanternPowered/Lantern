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
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.apache.commons.lang3.ArrayUtils;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.DefaultStackSizes;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.lanternpowered.server.inventory.slot.SlotChangeTracker;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetWindowSlot;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWindowItems;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

/**
 * A container that guesses what will change on a client container when a specific
 * operation is executed. For example shift clicking on an item, etc.
 * Additionally can every slot be bound to a specific client slot, and not every slot
 * has to be bound. This allows that you can reorder the complete client inventory
 * without having to modify your original {@link IInventory}. Just bind each slot
 * to the proper index and the client container will handle the rest.
 */
@SuppressWarnings("unchecked")
public abstract class ClientContainer implements SlotChangeTracker {

    /**
     * The slot index that should be used to bind the cursor slot.
     */
    public static final int CURSOR_SLOT_INDEX = -1;

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
        Arrays.fill(MAIN_INVENTORY_FLAGS, 0, MAIN_INVENTORY_FLAGS.length, FLAG_MAIN_INVENTORY);
        for (int i = 0; i < 9; i++) {
            // Apply the hotbar flags
            MAIN_INVENTORY_FLAGS[27 + i] |= ((i + 1) << 4);
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

        int dirtyState = 0;

        protected abstract ItemStack getRaw();
    }

    private final class EmptyClientSlot extends BaseClientSlot implements ClientSlot.Empty {

        @Override
        public ItemStack get() {
            return ItemStack.empty();
        }

        @Override
        protected ItemStack getRaw() {
            return ItemStack.empty();
        }
    }

    private final class SlotClientSlot extends BaseClientSlot implements ClientSlot.Slot {

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
            final ItemStack itemStack = this.slot.getRawItemStack();
            return itemStack == null ? ItemStack.empty() : itemStack;
        }
    }

    private final class IconClientSlot extends BaseClientSlot implements ClientSlot.Button {

        private ItemStack itemStack = ItemStack.empty();

        @Override
        public ItemStack get() {
            return this.itemStack.copy();
        }

        @Override
        public void setIcon(ItemStack itemStack) {
            this.itemStack = checkNotNull(itemStack, "itemStack").copy();
            queueSilentSlotChange(this);
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
    private final Multimap<LanternSlot, SlotClientSlot> slotMap = HashMultimap.create();
    private BaseClientSlot cursor = new EmptyClientSlot(); // Not really a slot, but the implementation does the trick
    private final int containerId;
    @SuppressWarnings("NullableProblems") private BaseClientSlot[] slots;
    @Nullable private LanternPlayer player;

    public ClientContainer(Text title) {
        // Generate a new container id
        this.containerId = generateContainerId();
        this.title = title;
    }

    /**
     * Populates this {@link ClientContainer}
     * with initial content.
     */
    @SuppressWarnings("ConstantConditions")
    private void populate() {
        // Is already populated
        if (this.slots != null) {
            return;
        }
        final int[] flags = getSlotFlags();
        // Create a array to bind slots
        this.slots = new BaseClientSlot[flags.length];
        for (int i = 0; i < this.slots.length; i++) {
            this.slots[i] = new EmptyClientSlot();
        }
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
        populate();
        final SlotClientSlot clientSlot = new SlotClientSlot(slot);
        removeSlot(index);
        if (index == -1) {
            this.cursor = clientSlot;
        } else {
            this.slots[index] = clientSlot;
        }
        this.slotMap.put(slot, clientSlot);
        if (this.player != null) {
            slot.addTracker(this);
        }
        queueSilentSlotChange(clientSlot);
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
        populate();
        final IconClientSlot clientSlot = new IconClientSlot();
        removeSlot(index);
        this.slots[index] = clientSlot;
        return clientSlot;
    }

    private void removeSlot(int index) {
        // Cleanup the old client slot
        final BaseClientSlot oldClientSlot = index == -1 ? this.cursor : this.slots[index];
        if (oldClientSlot instanceof SlotClientSlot) {
            final LanternSlot slot = ((SlotClientSlot) oldClientSlot).slot;
            // Remove the tracker from this slot
            if (this.slotMap.remove(slot, oldClientSlot) &&
                    this.player != null && this.slotMap.get(slot).isEmpty()) {
                slot.removeTracker(this);
            }
        }
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
    @Override
    public void queueSlotChange(LanternSlot slot) {
        this.slotMap.get(checkNotNull(slot, "slot")).forEach(this::queueSlotChange);
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
        populate();
        if (this.player == null) {
            return;
        }
        clientSlot.dirtyState = BaseClientSlot.IS_DIRTY;
    }

    private void queueSlotChangeSafely(BaseClientSlot clientSlot) {
        populate();
        if (this.player == null) {
            return;
        }
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
        this.slotMap.get(checkNotNull(slot, "slot")).forEach(this::queueSilentSlotChange);
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
        populate();
        if (this.player == null) {
            return;
        }
        clientSlot.dirtyState = BaseClientSlot.IS_DIRTY | BaseClientSlot.SILENT_UPDATE;
    }

    private void queueSilentSlotChangeSafely(BaseClientSlot clientSlot) {
        populate();
        if (this.player == null) {
            return;
        }
        if ((clientSlot.dirtyState & BaseClientSlot.IS_DIRTY) == 0) {
            clientSlot.dirtyState = BaseClientSlot.IS_DIRTY | BaseClientSlot.SILENT_UPDATE;
        }
    }

    /**
     * Binds this {@link ClientContainer} to the
     * given {@link LanternPlayer}.
     *
     * @param player The player
     */
    public void bind(Player player) {
        checkNotNull(player, "player");
        checkState(this.player == null, "There is already a player bound");
        populate();
        this.player = (LanternPlayer) player;
        // Add the tracker to each slot
        for (LanternSlot slot : this.slotMap.keySet()) {
            slot.addTracker(this);
        }
    }

    /**
     * Initializes the container for the bounded {@link Player}.
     */
    public void init() {
        checkState(this.player != null);
        populate();
        final List<Message> messages = new ArrayList<>();
        final Message message = createInitMessage();
        if (message != null) {
            messages.add(message);
        }
        final ItemStack[] items = new ItemStack[getSlotFlags().length];
        for (int i = 0; i < items.length; i++) {
            if (this.slots[i] instanceof ClientSlot.Empty) {
                System.out.println("DEBUG A: " + i);
            }
            items[serverSlotIndexToClient(i)] = this.slots[i].get();
        }
        // Send the inventory content
        messages.add(new MessagePlayOutWindowItems(this.containerId, items));
        // Send the cursor item if present
        if (!this.cursor.getRaw().isEmpty()) {
            messages.add(new MessagePlayOutSetWindowSlot(-1, -1, this.cursor.get()));
        }
        // Collect additional messages
        collectInitMessages(messages);
        // Stream the messages to the player
        this.player.getConnection().send(messages);
    }

    protected void collectInitMessages(List<Message> messages) {
    }

    public void update() {
        checkState(this.player != null);
        populate();
        final List<Message> messages = new ArrayList<>();
        // Collect all the changes
        collectChangeMessages(messages);
        if (!messages.isEmpty()) {
            // Stream the messages to the player
            this.player.getConnection().send(messages);
        }
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
                messages.add(new MessagePlayOutSetWindowSlot(containerId, serverSlotIndexToClient(index), slot.get()));
            }
        }
        // Update the cursor item if needed
        if ((this.cursor.dirtyState & BaseClientSlot.IS_DIRTY) != 0) {
            messages.add(new MessagePlayOutSetWindowSlot(-1, -1, this.cursor.get()));
        }
        // Collect the property changes
        collectPropertyChanges(messages);
    }

    protected void collectPropertyChanges(List<Message> messages) {
    }

    /**
     * Releases all the {@link LanternSlot} and
     * removes the {@link LanternPlayer}.
     */
    public void release() {
        populate();
        if (this.player == null) {
            return;
        }
        this.player = null;
        // Remove the tracker from each slot
        for (LanternSlot slot : this.slotMap.keySet()) {
            slot.removeTracker(this);
        }
    }

    /**
     * Gets a {@link ClientSlot} for the given slot index.
     *
     * @param index The slot index
     * @return The client slot if present, otherwise {@link Optional#empty()}
     */
    public Optional<ClientSlot> getClientSlot(int index) {
        populate();
        return index == -1 ? Optional.of(this.cursor) : index < 0 || index >= this.slots.length ? Optional.empty() : Optional.of(this.slots[index]);
    }

    /**
     * Attempts to get the bound {@link LanternSlot} for the given index.
     *
     * @param index The slot index
     * @return The bound slot if present, otherwise {@link Optional#empty()}
     */
    public Optional<LanternSlot> getSlot(int index) {
        populate();
        if (index < -1 || index >= this.slots.length) {
            return Optional.empty();
        }
        final BaseClientSlot clientSlot = index == -1 ? this.cursor : this.slots[index];
        return clientSlot instanceof SlotClientSlot ? Optional.of(((SlotClientSlot) clientSlot).slot) : Optional.empty();
    }

    public int getTopSlotsCount() {
        return getTopSlotFlags().length;
    }

    /**
     * Gets a array that contains all the flags
     * of the slots in this container. This does
     * not include the flags of the main player
     * inventory.
     *
     * @return The slot flags
     */
    protected abstract int[] getTopSlotFlags();

    /**
     * Gets a array that contains all the flags
     * of the slots in this container.
     *
     * @return The slot flags
     */
    protected int[] getSlotFlags() {
        return compileAllSlotFlags(getTopSlotFlags());
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

    protected int clientSlotIndexToServer(int index) {
        return index;
    }

    protected int serverSlotIndexToClient(int index) {
        return index;
    }

    /**
     * Handles a left or right click interaction.
     *
     * @param slotIndex The slot index
     * @param right Whether it's a right click action
     */
    public void handleLeftRightClick(int slotIndex, boolean right) {
        final BaseClientSlot slot = this.slots[slotIndex];
        // Only changes can occur if the cursor slot and the target slot are empty
        if (!slot.getRaw().isEmpty() && !this.cursor.getRaw().isEmpty()) {
            // Update the slot and cursor
            queueSilentSlotChangeSafely(slot);
            queueSlotChange(this.cursor);
        }
    }

    /**
     * Handles a shift click on the specified slot index, this will queue
     * all the slot updates that are required to force the client to revert
     * the changes.
     *
     * @param slotIndex The slot index
     */
    public void handleShiftClick(int slotIndex) {
        populate();
        slotIndex = clientSlotIndexToServer(slotIndex);
        final int[] flags = getSlotFlags();
        // Check if the slot is in the main inventory
        final boolean main = (flags[slotIndex] & FLAG_MAIN_INVENTORY) != 0;
        final boolean hotbar = (flags[slotIndex] & FLAG_HOTBAR) != 0;
        // Get the client slot
        final BaseClientSlot slot = this.slots[slotIndex];
        ItemStack itemStack = slot.get();
        System.out.println("Shift click: " + itemStack);
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
        final IntList mainSlots = new IntArrayList();
        for (int i = start; i != end; i += step) {
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
                itemStack.setQuantity(Math.max(0, itemStack.getQuantity() - removed));
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
        final int[] mainSlotsArray = mainSlots.toIntArray();
        if (reverse) {
            ArrayUtils.reverse(mainSlotsArray);
        }
        for (int i : mainSlotsArray) {
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
            if (!itemStack1.isEmpty() && (!LanternItemStack.areSimilar(itemStack, itemStack1) ||
                    itemStack1.getQuantity() >= limit)) {
                continue;
            }
            // Now, we take some items away from the shifted stack and continue
            // the process for the rest of the slots
            final int removed = Math.min(limit, itemStack1.getQuantity());
            if (removed > 0) {
                itemStack.setQuantity(Math.max(0, itemStack.getQuantity() - removed));
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
