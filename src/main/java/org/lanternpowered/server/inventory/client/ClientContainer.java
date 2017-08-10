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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.apache.commons.lang3.ArrayUtils;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.DefaultStackSizes;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.inventory.behavior.ContainerInteractionBehavior;
import org.lanternpowered.server.inventory.behavior.MouseButton;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.lanternpowered.server.inventory.slot.SlotChangeTracker;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetWindowSlot;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWindowItems;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
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
     * A flag that defines that the slot is an output slot. Double click can't
     * retrieve items from that slot.
     */
    protected static final int FLAG_IGNORE_DOUBLE_CLICK = 0x200;

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

    protected static abstract class BaseClientSlot implements ClientSlot {

        /**
         * Whether the state is dirty.
         */
        public final static int IS_DIRTY = 0x1;

        /**
         * Whether the slot should be updated silently.
         */
        public final static int SILENT_UPDATE = 0x2;

        protected final int index;
        protected int dirtyState = 0;

        protected BaseClientSlot(int index) {
            this.index = index;
        }

        protected abstract ItemStack getRaw();
    }

    private final class EmptyClientSlot extends BaseClientSlot implements ClientSlot.Empty {

        private EmptyClientSlot(int index) {
            super(index);
        }

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

        private SlotClientSlot(int index, LanternSlot slot) {
            super(index);
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

        private IconClientSlot(int index) {
            super(index);
        }

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
    private BaseClientSlot cursor = new EmptyClientSlot(CURSOR_SLOT_INDEX); // Not really a slot, but the implementation does the trick
    private final int containerId;
    @SuppressWarnings("NullableProblems") protected BaseClientSlot[] slots;
    @Nullable private LanternPlayer player;
    @Nullable private ContainerInteractionBehavior interactionBehavior;

    // Double click data
    @Nullable private ItemStack doubleClickItem;

    // Drag mode data
    private final IntSet dragSlots = new IntArraySet();
    private int dragMode = -1;

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
            this.slots[i] = new EmptyClientSlot(i);
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
     * Binds the {@link ContainerInteractionBehavior} to this container.
     *
     * @param interactionBehavior The interaction behavior
     */
    public void bindInteractionBehavior(ContainerInteractionBehavior interactionBehavior) {
        checkNotNull(interactionBehavior, "interactionBehavior");
        this.interactionBehavior = interactionBehavior;
    }

    /**
     * Unbinds/releases the slot of the given index.
     *
     * @param index The index
     */
    public void unbind(int index) {
        populate();
        if (this.slots[index] instanceof ClientSlot.Empty) {
            return;
        }
        removeSlot(index);
        final EmptyClientSlot clientSlot = new EmptyClientSlot(index);
        this.slots[index] = clientSlot;
        queueSilentSlotChangeSafely(clientSlot);
    }

    /**
     * Binds a {@link LanternSlot} to the
     * given slot index.
     *
     * @param index The slot index
     * @return The bound client slot
     */
    public ClientSlot.Slot bindSlot(int index, LanternSlot slot) {
        populate();
        final SlotClientSlot clientSlot = new SlotClientSlot(index, slot);
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
        final IconClientSlot clientSlot = new IconClientSlot(index);
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
     * Binds a {@link InventoryProperty} type which will
     * be retrieved from the target {@link Inventory}.
     *
     * @param propertyType The property type
     * @param inventory The inventory
     * @param <T> The property type
     */
    public <T extends InventoryProperty<?,?>> void bindProperty(Class<T> propertyType, Inventory inventory) {
        bindProperty(propertyType, () -> inventory.getInventoryProperty(propertyType).orElse(null));
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

    protected void queueSlotChange(BaseClientSlot clientSlot) {
        populate();
        if (this.player == null) {
            return;
        }
        clientSlot.dirtyState = BaseClientSlot.IS_DIRTY;
    }

    protected void queueSlotChangeSafely(BaseClientSlot clientSlot) {
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

    protected void queueSilentSlotChange(BaseClientSlot clientSlot) {
        populate();
        if (this.player == null) {
            return;
        }
        clientSlot.dirtyState = BaseClientSlot.IS_DIRTY | BaseClientSlot.SILENT_UPDATE;
    }

    protected void queueSilentSlotChangeSafely(BaseClientSlot clientSlot) {
        populate();
        if (this.player == null) {
            return;
        }
        if ((clientSlot.dirtyState & BaseClientSlot.IS_DIRTY) == 0) {
            clientSlot.dirtyState = BaseClientSlot.IS_DIRTY | BaseClientSlot.SILENT_UPDATE;
        }
    }

    public LanternPlayer getPlayer() {
        checkState(this.player != null, "No player is bound");
        return this.player;
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
                        (hotbarSlot = (flags[i] & FLAG_HOTBAR) >> 4) != 0) {
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
            this.cursor.dirtyState = 0;
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
     * Converts the hotbar slot index (0 - 8) to
     * a slot index within this container.
     *
     * @param hotbarSlot The hotbar slot
     * @return The slot index
     */
    public int getHotbarSlotIndex(int hotbarSlot) {
        return getSlotFlags().length - (9 - hotbarSlot);
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

    @SuppressWarnings({"ConstantConditions", "OptionalGetWithoutIsPresent"})
    public void handlePick(int slotIndex) {
        populate();
        // Convert the slot index
        slotIndex = clientSlotIndexToServer(slotIndex);

        queueSilentSlotChangeSafely(this.slots[slotIndex]);
        final int hotbarSlotIndex = ((PlayerClientContainer) this.player.getInventoryContainer()
                .getClientContainer(this.player).get()).getSelectedHotbarSlotIndex();
        queueSilentSlotChangeSafely(this.slots[getHotbarSlotIndex(hotbarSlotIndex)]);
        final int slotIndex1 = slotIndex;
        tryProcessBehavior(behavior -> behavior.handlePick(this, this.slots[slotIndex1]));
    }

    public void handleCreativeClick(int slotIndex, @Nullable ItemStack itemStack) {
        populate();
        // Convert the slot index
        slotIndex = clientSlotIndexToServer(slotIndex);

        // Update the target slot and cursor
        queueSilentSlotChange(this.slots[slotIndex]);
        // queueSlotChange(this.cursor);

        final int slotIndex1 = slotIndex;
        tryProcessBehavior(behavior -> behavior.handleCreativeClick(this,
                slotIndex1 == -999 ? null : this.slots[slotIndex1], itemStack));
    }

    public void handleClick(int slotIndex, int mode, int button) {
        populate();
        // Convert the slot index
        slotIndex = clientSlotIndexToServer(slotIndex);

        // Handle and/or reset the drag
        final boolean drag = mode == 5;
        if (!drag || !handleDrag(slotIndex, button)) {
            resetDrag();
        }

        // Reset the double click
        final boolean doubleClick = mode == 6 && button == 0;
        if (!doubleClick) {
            this.doubleClickItem = null;
        }

        if (mode == 0 && (button == 0 || button == 1) && slotIndex != -999) {
            // Left/right click inside the inventory
            handleLeftRightClick(slotIndex, button);
        } else if (mode == 1 && (button == 0 || button == 1)) {
            // Shift + left/right click
            handleShiftClick(slotIndex, button);
        } else if (doubleClick) {
            // Double click
            handleDoubleClick(slotIndex);
        } else if (mode == 2) {
            // Number keys
            handleNumberKey(slotIndex, button);
        } else if (mode == 4 && (button == 0 || button == 1)) {
            if (slotIndex == -999) {
                // Left/right click outside the inventory
                handleLeftRightClick(-999, button);
            } else {
                // (Control) drop key
                handleDropKey(slotIndex, button == 1);
            }
        } else if (mode == 3 && button == 2) {
            // Middle click
            handleMiddleClick(slotIndex);
        } else if (!drag) {
            // Warn about unhandled actions
            Lantern.getLogger().warn("Unknown client container click action: slotIndex: {}, mode: {}, button: {}",
                    slotIndex, mode, button);
        }
    }

    protected void tryProcessBehavior(Consumer<ContainerInteractionBehavior> behavior) {
        if (this.interactionBehavior != null) {
            try {
                behavior.accept(this.interactionBehavior);
            } catch (Throwable t) {
                Lantern.getLogger().error("Failed to process the inventory interaction behavior", t);
            }
        }
    }

    /**
     * Resets the current drag process.
     */
    private void resetDrag() {
        this.dragMode = -1;
        // Force each slot to update
        for (int i : this.dragSlots.toIntArray()) {
            queueSlotChange(this.slots[i]);
        }
        // Also update the cursor
        queueSlotChangeSafely(this.cursor);
        this.dragSlots.clear();
    }

    private boolean handleDrag(int slotIndex, int button) {
        // Extract the drag mode and state from the button
        final int mode = button >> 2;
        final int state = button & 0x3;
        // Check if the drag mode matches the current one, or if a new drag started
        if (mode != this.dragMode) {
            // Drag mode mismatch and state isn't "start"
            if (state != 0) {
                // Force to update the send slot if it's an add action
                if (state == 1) {
                    this.dragSlots.add(slotIndex);
                }
                return false;
            }
            this.dragMode = mode;
        }
        if (state == 0) {
            // Start state
            // Another start action? Just restart the drag.
            resetDrag();
            this.dragMode = mode;
        } else if (state == 1) {
            // Add slot state
            // Collect the slots
            this.dragSlots.add(slotIndex);
        } else if (state == 2) {
            // Finish state
            if (!this.dragSlots.isEmpty()) {
                tryProcessBehavior(behavior -> {
                    final List<ClientSlot> clientSlots = Arrays.stream(this.dragSlots.toIntArray())
                            .mapToObj(i -> this.slots[i])
                            .collect(ImmutableList.toImmutableList());
                    behavior.handleDrag(this, clientSlots, mode == 0 ? MouseButton.LEFT : mode == 1 ? MouseButton.RIGHT : MouseButton.MIDDLE);
                });
            }
            // Just reset the drag
            resetDrag();
        }
        return true;
    }

    /**
     * Handles a number key interaction.
     *
     * @param slotIndex The slot index that was clicked
     */
    private void handleMiddleClick(int slotIndex) {
        // Middle click is only used in creative,
        // you can only do it if the cursor is empty
        // and the target slot isn't empty.
        if (slotIndex != -999 && this.cursor.getRaw().isEmpty() &&
                !this.slots[slotIndex].getRaw().isEmpty() &&
                (this.player != null && this.player.get(Keys.GAME_MODE).get() == GameModes.CREATIVE)) {
            queueSlotChange(this.cursor);
        }
        tryProcessBehavior(behavior -> behavior.handleClick(this,
                slotIndex == -999 ? null : this.slots[slotIndex], MouseButton.MIDDLE));
    }

    /**
     * Handles a number key interaction.
     *
     * @param slotIndex The slot index that was selected while pressing the button
     * @param number The number that was pressed, 0 - 8 (0 is number 1, etc.)
     */
    private void handleNumberKey(int slotIndex, int number) {
        // Calculate the hotbar slot index
        final int hotbarSlotIndex = getHotbarSlotIndex(number);
        // Clicking to the same slot won't do anything and
        // if the both slots are empty also nothing will change
        if (slotIndex != hotbarSlotIndex && (!this.slots[slotIndex].getRaw().isEmpty() ||
                !this.slots[hotbarSlotIndex].getRaw().isEmpty())) {
            queueSilentSlotChangeSafely(this.slots[slotIndex]);
            queueSilentSlotChangeSafely(this.slots[hotbarSlotIndex]);
        }
        tryProcessBehavior(behavior -> behavior.handleNumberKey(this, this.slots[slotIndex], number + 1));
    }

    /**
     * Handles a double click interaction.
     *
     * @param slotIndex The slot index that was pressed
     */
    private void handleDoubleClick(int slotIndex) {
        if (this.doubleClickItem != null) {
            final ItemStack itemStack = this.doubleClickItem;
            final int maxStack = DefaultStackSizes.getOriginalMaxSize(itemStack.getType());
            final int[] flags = getSlotFlags();
            for (int i = 0; i < flags.length; i++) {
                // The stack is full, stop
                if (itemStack.getQuantity() >= maxStack) {
                    break;
                }
                if (i == slotIndex || (flags[i] & FLAG_IGNORE_DOUBLE_CLICK) != 0) {
                    continue;
                }
                final BaseClientSlot slot1 = this.slots[i];
                final ItemStack itemStack1 = slot1.getRaw();
                if (itemStack1.isEmpty() || !LanternItemStack.areSimilar(itemStack, itemStack1)) {
                    continue;
                }
                // Increase quantity
                itemStack.setQuantity(Math.min(maxStack, itemStack.getQuantity() + itemStack1.getQuantity()));
                // Queue a slot change
                queueSilentSlotChangeSafely(slot1);
            }
            // Update the cursor
            queueSlotChangeSafely(this.cursor);
        }
        this.doubleClickItem = null;
        tryProcessBehavior(behavior -> behavior.handleDoubleClick(this, this.slots[slotIndex]));
    }

    /**
     * Handles a drop key interaction.
     *
     * @param slotIndex The slot index that was selected while pressing the button
     * @param ctrl Whether the control button was pressed
     */
    private void handleDropKey(int slotIndex, boolean ctrl) {
        // The cursor has to be empty and the target slot
        // cannot be empty or nothing will happen
        if (this.cursor.getRaw().isEmpty() &&
                !this.slots[slotIndex].getRaw().isEmpty()) {
            queueSlotChangeSafely(this.slots[slotIndex]);
        }
        tryProcessBehavior(behavior -> behavior.handleDropKey(this, this.slots[slotIndex], ctrl));
    }

    /**
     * Handles a left or right click interaction. {@code slotIndex} with value -999
     * may be passed in when the click interaction occurs outside the container.
     *
     * @param slotIndex The slot index that was clicked
     * @param button The button that was pressed (0: left; 1: right)
     */
    private void handleLeftRightClick(int slotIndex, int button) {
        if (slotIndex != -999) {
            final BaseClientSlot slot = this.slots[slotIndex];
            if (!slot.getRaw().isEmpty()) {
                // Only changes can occur if the cursor slot and the target slot are empty
                if (!this.cursor.getRaw().isEmpty()) {
                    // Update the slot and cursor
                    queueSilentSlotChangeSafely(slot);
                    queueSlotChangeSafely(this.cursor);
                } else {
                    // Store the clicked item, it's possible that a double click occurs
                    this.doubleClickItem = slot.get();
                }
            }
        } else if (!this.cursor.getRaw().isEmpty()) {
            queueSlotChange(this.cursor);
        }
        tryProcessBehavior(behavior -> behavior.handleClick(this,
                slotIndex == -999 ? null : this.slots[slotIndex], button == 1 ? MouseButton.RIGHT : MouseButton.LEFT));
    }

    /**
     * Handles a shift click on the specified slot index, this will queue
     * all the slot updates that are required to force the client to revert
     * the changes.
     *
     * @param slotIndex The slot index that was clicked
     * @param button The button that was pressed (0: left; 1: right)
     */
    private void handleShiftClick(int slotIndex, int button) {
        handleShiftClick0(slotIndex);
        tryProcessBehavior(behavior -> behavior.handleShiftClick(this, this.slots[slotIndex], button == 1 ? MouseButton.RIGHT : MouseButton.LEFT));
    }

    private void handleShiftClick0(int slotIndex) {
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
        queueSilentSlotChangeSafely(slot);
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
