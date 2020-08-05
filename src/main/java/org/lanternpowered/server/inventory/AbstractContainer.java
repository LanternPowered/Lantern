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
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.text.Component;
import org.lanternpowered.server.entity.player.LanternPlayer;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.type.slot.LanternSlot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.translation.Translation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a {@link Container} that can be opened by a {@link Player}.
 */
@SuppressWarnings({"SuspiciousMethodCalls", "unchecked"})
public abstract class AbstractContainer extends AbstractChildrenInventory implements Container {

    private final Map<AbstractSlot, AbstractContainerSlot> slotsToContainerSlot;

    /**
     * The slot of the cursor item.
     */
    private final AbstractSlot cursor;

    /**
     * The viewer of the container.
     */
    @Nullable private LanternPlayer viewer;

    /**
     * The client container that will be displayed to the viewer.
     * <p>Can be {@code null} if the container isn't being viewed.</p>
     */
    @Nullable private ClientContainer clientContainer;

    /**
     * Constructs a new {@link AbstractContainer} for the viewed inventories.
     *
     * @param inventories The viewed inventories
     */
    protected AbstractContainer(List<AbstractMutableInventory> inventories) {
        this(new LanternSlot(), inventories);
    }

    /**
     * Constructs a new {@link AbstractContainer} for the
     * cursor slot and viewed inventories.
     *
     * @param cursorSlot The cursor slot
     * @param inventories The viewed inventories
     */
    protected AbstractContainer(AbstractSlot cursorSlot, List<AbstractMutableInventory> inventories) {
        checkNotNull(cursorSlot, "cursorSlot");
        checkNotNull(inventories, "inventories");
        this.cursor = cursorSlot;
        final ImmutableList.Builder<AbstractContainerSlot> slots = ImmutableList.builder();
        final Map<AbstractSlot, AbstractContainerSlot> slotsToContainerSlot = new HashMap<>();
        for (AbstractMutableInventory inventory : inventories) {
            for (AbstractSlot slot : inventory.getSlots()) {
                slotsToContainerSlot.computeIfAbsent(slot, slot1 -> {
                    final AbstractContainerSlot containerSlot = ((AbstractInventorySlot) slot).constructContainerSlot();
                    containerSlot.slot = (AbstractInventorySlot) slot;
                    containerSlot.setParent(this);
                    slots.add(containerSlot);
                    return containerSlot;
                });
            }
        }
        this.slotsToContainerSlot = ImmutableMap.copyOf(slotsToContainerSlot);
        initWithSlots(inventories, slots.build());
    }

    /**
     * Constructs a {@link ClientContainer} for this container.
     *
     * @return The client container
     */
    protected abstract ClientContainer constructClientContainer();

    @Override
    public void setName(Translation name) {
        super.setName(name);
    }

    /**
     * Sets the name/title of the container.
     *
     * @param name The name
     */
    public void setName(Component name) {
        super.setName(TextTranslation.of(name));
    }

    /**
     * Gets the cursor {@link LanternSlot}.
     *
     * @return The cursor slot
     */
    public AbstractSlot getCursorSlot() {
        return this.cursor;
    }

    @Override
    public EmptyInventory empty() {
        return super.empty();
    }

    @Override
    public int getSlotIndex(Slot slot) {
        // Lookup the index using it's container variant if present
        if (!(slot instanceof AbstractContainerSlot)) {
            slot = this.slotsToContainerSlot.get(slot);
            if (slot == null) {
                return INVALID_SLOT_INDEX;
            }
        }
        return super.getSlotIndex(slot);
    }

    private Slot transformSlot(Slot slot) {
        if (!(slot instanceof AbstractContainerSlot)) {
            slot = this.slotsToContainerSlot.get(slot);
            checkState(slot != null, "A unknown slot appears?");
        }
        return slot;
    }

    private PeekedPollTransactionResult transformSlots(PeekedPollTransactionResult result) {
        return new PeekedPollTransactionResult(
                transformSlots(result.getTransactions()), result.getPolledItem());
    }

    private PeekedOfferTransactionResult transformSlots(PeekedOfferTransactionResult result) {
        return new PeekedOfferTransactionResult(transformSlots(result.getTransactions()), result.getRejectedItem());
    }

    public ImmutableList<SlotTransaction> transformSlots(List<SlotTransaction> transactions) {
        return transactions.stream()
                .map(transaction -> {
                    final Slot slot = transformSlot(transaction.getSlot());
                    return slot == transaction.getSlot() ? transaction : new SlotTransaction(
                            transformSlot(transaction.getSlot()), transaction.getOriginal(), transaction.getFinal());
                })
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    public PeekedPollTransactionResult peekPoll(Predicate<ItemStack> matcher) {
        return transformSlots(super.peekPoll(matcher));
    }

    @Override
    public PeekedPollTransactionResult peekPoll(int limit, Predicate<ItemStack> matcher) {
        return transformSlots(super.peekPoll(limit, matcher));
    }

    @Override
    public PeekedOfferTransactionResult peekOffer(ItemStack itemStack) {
        return transformSlots(super.peekOffer(itemStack));
    }

    @Override
    public boolean isViewedSlot(Slot slot) {
        return containsInventory(slot.viewedSlot());
    }

    @Override
    public List<Inventory> getViewed() {
        return (List) getChildren();
    }

    @Override
    public boolean setCursor(ItemStack item) {
        if (isOpen()) {
            this.cursor.set(item);
            return true;
        }
        return false;
    }

    @Override
    public Optional<ItemStack> getCursor() {
        return isOpen() ? Optional.of(this.cursor.peek()) : Optional.empty();
    }

    @Override
    public LanternPlayer getViewer() {
        checkState(this.viewer != null, "The viewer hasn't been initialized yet.");
        return this.viewer;
    }

    @Override
    public boolean isOpen() {
        return getClientContainer() != null && getViewer().getOpenInventory().orElse(null) == this;
    }

    @Override
    protected void queryInventories(QueryInventoryAdder adder) throws QueryInventoryAdder.Stop {
        getSlots().forEach(adder::add);
        super.queryInventories(inventory -> {
            // Don't process the slots twice
            if (inventory instanceof Slot) {
                return;
            }
            adder.add(inventory);
        });
    }

    @Override
    protected ViewableInventory toViewable() {
        return new ContainerProvidedWrappedInventory(this, this::copy);
    }

    /**
     * Creates a copy of this {@link AbstractContainer} without attaching
     * the viewer, so that it can be opened by a other viewer.
     *
     * @return The copied container
     */
    public abstract AbstractContainer copy();

    /**
     * Binds the {@link Player} to this container.
     *
     * <p>Only one player can be attached per container.</p>
     *
     * @param player The player
     */
    void bind(Player player) {
        checkNotNull(player, "player");
        checkState(this.viewer == null || this.viewer == player, "A container can only be attached to one player.");
        this.viewer = (LanternPlayer) player;
    }

    /**
     * Initializes and opens the client container.
     */
    void open() {
        checkState(this.viewer != null, "A viewer must be bound before opening.");
        this.clientContainer = constructClientContainer();
        this.clientContainer.bind(this.viewer);
        this.clientContainer.init();
        // Attach the viewer to all the linked children inventories, etc.
        addViewer(this.viewer, this);
    }

    /**
     * Closes the container.
     */
    void close() {
        if (this.clientContainer == null) {
            return;
        }
        removeViewer(getViewer(), this);
        this.clientContainer.release();
        this.clientContainer = null;
    }

    /**
     * Gets the {@link ClientContainer} that is being displayed. Will
     * be {@code null} if the inventory isn't open.
     *
     * @return The client container
     */
    @Nullable
    public ClientContainer getClientContainer() {
        return this.clientContainer;
    }
}
