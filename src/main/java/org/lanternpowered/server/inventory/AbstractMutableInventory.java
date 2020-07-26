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

import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.inventory.behavior.ShiftClickBehavior;
import org.lanternpowered.server.inventory.behavior.SimpleShiftClickBehavior;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A base class for every {@link Inventory} that
 * isn't a {@link EmptyInventory}.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMutableInventory extends AbstractInventory {

    @Nullable private PluginContainer plugin;
    @Nullable private LanternEmptyInventory emptyInventory;

    private ShiftClickBehavior shiftClickBehavior = SimpleShiftClickBehavior.INSTANCE;

    private final Set<InventoryViewerListener> viewerListeners = new HashSet<>();
    private final Set<InventoryCloseListener> closeListeners = new HashSet<>();

    @Nullable private Map<Property<?>, Object> properties;

    /**
     * Sets the properties map of this inventory.
     *
     * @param properties The properties map
     */
    void setProperties(Map<Property<?>, Object> properties) {
        this.properties = properties;
    }

    /**
     * Sets the {@link PluginContainer} of this inventory.
     *
     * @param plugin The plugin container
     */
    void setPlugin(PluginContainer plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the {@link ShiftClickBehavior} of this {@link Inventory}.
     *
     * @return The shift click behavior
     */
    public ShiftClickBehavior getShiftClickBehavior() {
        return this.shiftClickBehavior;
    }

    /**
     * Sets the {@link ShiftClickBehavior} of this {@link Inventory}.
     *
     * @param shiftClickBehavior The shift click behavior
     */
    void setShiftClickBehavior(ShiftClickBehavior shiftClickBehavior) {
        this.shiftClickBehavior = shiftClickBehavior;
    }

    /**
     * Initializes the target {@link ClientContainer} for this inventory.
     *
     * @param clientContainer The client container
     */
    protected void initClientContainer(ClientContainer clientContainer) {
        // Apply the name to the container
        clientContainer.setTitle(TextTranslation.toText(getName()));
    }

    @Override
    protected <V> Optional<V> tryGetProperty(Property<V> property) {
        checkNotNull(property, "property");
        if (this.properties != null) {
            final V value = (V) this.properties.get(property);
            if (value != null) {
                return Optional.of(value);
            }
        }
        return super.tryGetProperty(property);
    }

    @Override
    void close(CauseStack causeStack) {
        super.close(causeStack);
        try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
            frame.pushCause(this);
            for (InventoryCloseListener listener : this.closeListeners) {
                listener.onClose(this);
            }
        }
    }

    @Override
    public void addCloseListener(InventoryCloseListener listener) {
        checkNotNull(listener, "listener");
        this.closeListeners.add(listener);
    }

    @Override
    public void addViewListener(InventoryViewerListener listener) {
        checkNotNull(listener, "listener");
        this.viewerListeners.add(listener);
    }

    private static final class ViewerListenerCallback implements InventoryViewerListener.Callback {

        private boolean remove;

        @Override
        public void remove() {
            this.remove = true;
        }
    }

    @Override
    void addViewer(Player viewer, AbstractContainer container) {
        super.addViewer(viewer, container);
        if (this.viewerListeners.isEmpty()) {
            return;
        }
        final ViewerListenerCallback callback = new ViewerListenerCallback();
        this.viewerListeners.removeIf(listener -> {
            callback.remove = false;
            listener.onViewerAdded(viewer, container, callback);
            return callback.remove;
        });
    }

    @Override
    void removeViewer(Player viewer, AbstractContainer container) {
        super.removeViewer(viewer, container);
        if (this.viewerListeners.isEmpty()) {
            return;
        }
        final ViewerListenerCallback callback = new ViewerListenerCallback();
        this.viewerListeners.removeIf(listener -> {
            callback.remove = false;
            listener.onViewerRemoved(viewer, container, callback);
            return callback.remove;
        });
    }

    @Override
    public IInventory intersect(Inventory inventory) {
        checkNotNull(inventory, "inventory");
        if (inventory == this) {
            return this;
        }
        final AbstractInventory abstractInventory = (AbstractInventory) inventory;
        List<AbstractSlot> slots = abstractInventory.getSlots();
        if (slots.isEmpty()) {
            return genericEmpty();
        }
        slots = new ArrayList<>(slots);
        slots.retainAll(getSlots());
        if (slots.isEmpty()) { // No slots were intersected, just return a empty inventory
            return genericEmpty();
        } else {
            // Construct the result inventory
            final ChildrenInventoryQuery result = new ChildrenInventoryQuery();
            slots = Collections.unmodifiableList(slots);
            result.initWithSlots((List) slots, slots);
            return result;
        }
    }

    @Override
    public IInventory union(Inventory inventory) {
        checkNotNull(inventory, "inventory");
        if (inventory == this) {
            return this;
        }
        final AbstractInventory abstractInventory = (AbstractInventory) inventory;
        List<AbstractSlot> slotsB = abstractInventory.getSlots();
        if (slotsB.isEmpty()) {
            return this;
        }
        slotsB = new ArrayList<>(slotsB);
        final List<AbstractSlot> slotsA = getSlots();
        slotsB.removeAll(slotsA);
        slotsB.addAll(0, slotsA);
        if (slotsB.isEmpty()) { // No slots were intersected, just return a empty inventory
            return genericEmpty();
        } else {
            // Construct the result inventory
            final ChildrenInventoryQuery result = new ChildrenInventoryQuery();
            slotsB = Collections.unmodifiableList(slotsB);
            result.initWithSlots((List) slotsB, slotsB);
            return result;
        }
    }

    @Override
    public PluginContainer getPlugin() {
        return this.plugin == null ? super.getPlugin() : this.plugin;
    }

    @Override
    public EmptyInventory empty() {
        // Lazily construct the empty inventory
        @Nullable LanternEmptyInventory emptyInventory = this.emptyInventory;
        if (emptyInventory == null) {
            emptyInventory = this.emptyInventory = new LanternEmptyInventory();
            emptyInventory.setParent(this);
        }
        return emptyInventory;
    }
}
