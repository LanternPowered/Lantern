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
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.event.CauseStack;
import org.lanternpowered.server.inventory.behavior.ShiftClickBehavior;
import org.lanternpowered.server.inventory.behavior.SimpleShiftClickBehavior;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.text.translation.TextTranslation;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.property.AbstractInventoryProperty;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * A base class for every {@link Inventory} that
 * isn't a {@link EmptyInventory}.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMutableInventory extends AbstractInventory {

    @Nullable private PluginContainer plugin;
    @Nullable private LanternEmptyInventory emptyInventory;
    @Nullable private InventoryArchetype archetype;

    private ShiftClickBehavior shiftClickBehavior = SimpleShiftClickBehavior.INSTANCE;

    private final Set<InventoryViewerListener> viewerListeners = new HashSet<>();
    private final Set<InventoryCloseListener> closeListeners = new HashSet<>();

    @Nullable private Map<Class<?>, Map<Object, InventoryProperty<?,?>>> properties;

    /**
     * Sets the properties map of this inventory.
     *
     * @param properties The properties map
     */
    void setProperties(Map<Class<?>, Map<Object, InventoryProperty<?,?>>> properties) {
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
     * Sets the {@link InventoryArchetype} of this inventory.
     *
     * @param archetype The archetype
     */
    void setArchetype(InventoryArchetype archetype) {
        this.archetype = archetype;
    }

    /**
     * Sets the {@link Carrier} of this {@link Inventory}.
     *
     * @param carrier The carrier
     */
    void setCarrier0(Carrier carrier) {
        setCarrier(carrier);
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
     * Sets the {@link Carrier} of this {@link Inventory}.
     *
     * @param carrier The carrier
     */
    protected abstract void setCarrier(Carrier carrier);

    /**
     * Gets all the {@link AbstractSlot}s that could be found in the
     * children inventories. This method may return a empty {@link List}
     * if the subclass doesn't support children, for example slots and
     * empty inventories.
     *
     * @return The slots
     */
    protected abstract List<AbstractSlot> getSlotInventories();

    /**
     * Gets all the {@link AbstractSlot}s that could be found in the
     * children inventories. This method may return a empty {@link List}
     * if the subclass doesn't support children, for example slots and
     * empty inventories. The position of the slot also represents the
     * index.
     *
     * @return The slots
     */
    protected abstract List<AbstractSlot> getIndexedSlotInventories();

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
    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Class<T> propertyType, @Nullable Object key) {
        checkNotNull(propertyType, "propertyType");
        if (this.properties != null) {
            final Map<Object, InventoryProperty<?, ?>> properties = this.properties.get(propertyType);
            if (properties != null) {
                InventoryProperty<?, ?> property;
                if (key != null) {
                    property = properties.get(key);
                } else {
                    property = properties.get(AbstractInventoryProperty.getDefaultKey(propertyType));
                    if (property == null) {
                        property = properties.values().stream().findFirst().orElse(null);
                    }
                }
                if (property != null) {
                    return Optional.of(propertyType.cast(property));
                }
            }
        }
        return super.tryGetProperty(propertyType, key);
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Class<T> propertyType) {
        final List<T> propertyList = super.tryGetProperties(propertyType);
        if (this.properties != null) {
            final Map<Object, InventoryProperty<?, ?>> properties = this.properties.get(propertyType);
            if (properties != null) {
                propertyList.addAll((Collection<? extends T>) properties.values());
            }
        }
        return propertyList;
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
    void addViewer(Viewer viewer, LanternContainer container) {
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
    void removeViewer(Viewer viewer, LanternContainer container) {
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
    public <T extends Inventory> Iterable<T> orderedSlots() {
        return (Iterable<T>) getIndexedSlotInventories();
    }

    @Override
    public IInventory intersect(Inventory inventory) {
        checkNotNull(inventory, "inventory");
        if (inventory == this) {
            return this;
        }
        final AbstractInventory abstractInventory = (AbstractInventory) inventory;
        List<AbstractSlot> slots = abstractInventory.getSlotInventories();
        if (slots.isEmpty()) {
            return genericEmpty();
        }
        slots = new ArrayList<>(slots);
        slots.retainAll(getSlotInventories());
        if (slots.isEmpty()) { // No slots were intersected, just return a empty inventory
            return genericEmpty();
        } else {
            // Construct the result inventory
            final UnorderedChildrenInventoryQuery result = new UnorderedChildrenInventoryQuery();
            slots = Collections.unmodifiableList(slots);
            result.init(slots, slots);
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
        List<AbstractSlot> slotsB = abstractInventory.getSlotInventories();
        if (slotsB.isEmpty()) {
            return this;
        }
        slotsB = new ArrayList<>(slotsB);
        final List<AbstractSlot> slotsA = getSlotInventories();
        slotsB.removeAll(slotsA);
        slotsB.addAll(0, slotsA);
        if (slotsB.isEmpty()) { // No slots were intersected, just return a empty inventory
            return genericEmpty();
        } else {
            // Construct the result inventory
            final UnorderedChildrenInventoryQuery result = new UnorderedChildrenInventoryQuery();
            slotsB = Collections.unmodifiableList(slotsB);
            result.init(slotsB, slotsB);
            return result;
        }
    }

    @Override
    public InventoryArchetype getArchetype() {
        return this.archetype == null ? InventoryArchetypes.UNKNOWN : this.archetype;
    }

    @Override
    public PluginContainer getPlugin() {
        return this.plugin == null ? super.getPlugin() : this.plugin;
    }

    @Override
    public EmptyInventory empty() {
        // Lazily construct the empty inventory
        LanternEmptyInventory emptyInventory = this.emptyInventory;
        if (emptyInventory == null) {
            emptyInventory = this.emptyInventory = new LanternEmptyInventory();
            emptyInventory.setParent(this);
        }
        return emptyInventory;
    }
}
