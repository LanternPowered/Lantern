/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public abstract class InventoryBase implements IInventory {

    static class EmptyNameHolder {

        static final Translation EMPTY_NAME = Lantern.getRegistry().getTranslationManager().get("inventory.empty.title");
    }

    @Nullable private final Inventory parent;
    @Nullable private final Translation name;

    /**
     * All the {@link InventoryProperty}s of this inventory mapped by their type.
     */
    private final Multimap<Class<?>, InventoryProperty<?,?>> inventoryPropertiesByClass = HashMultimap.create();

    /**
     * All the {@link InventoryProperty}s of this inventory mapped by their key.
     */
    private final Map<PropertyKey, InventoryProperty<?,?>> inventoryPropertiesByKey = new HashMap<>();

    private final Set<IViewerListener> viewerListeners = new HashSet<>();

    private static final class PropertyKey {

        private final Class<? extends InventoryProperty> type;
        @Nullable private final Object key;

        private PropertyKey(Class<? extends InventoryProperty> type, @Nullable Object key) {
            this.type = type;
            this.key = key;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof PropertyKey)) {
                return false;
            }
            final PropertyKey other0 = (PropertyKey) other;
            return other0.type == this.type && Objects.equals(other0.key, this.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.type, this.key);
        }
    }

    protected final EmptyInventory emptyInventory = this instanceof EmptyInventory ?
            (EmptyInventory) this : new EmptyInventoryImpl(this);

    public InventoryBase(@Nullable Inventory parent, @Nullable Translation name) {
        this.parent = parent;
        this.name = name;
    }

    protected void finalizeContent() {
    }

    /**
     * Registers a {@link InventoryProperty} for this inventory.
     *
     * @param inventoryProperty The inventory property
     */
    protected void registerProperty(InventoryProperty<?, ?> inventoryProperty) {
        checkNotNull(inventoryProperty, "inventoryProperty");
        this.inventoryPropertiesByClass.put(inventoryProperty.getClass(), inventoryProperty);
        final PropertyKey propertyKey = new PropertyKey(inventoryProperty.getClass(), inventoryProperty.getKey());
        this.inventoryPropertiesByKey.put(propertyKey, inventoryProperty);
    }

    @Override
    public InventoryTransactionResult offer(ItemStack stack) {
        return this.offerFast(stack).asTransactionResult();
    }

    @Override
    public IInventory parent() {
        return this.parent == null ? this : (IInventory) this.parent;
    }

    @Override
    public Translation getName() {
        return this.name == null ? EmptyNameHolder.EMPTY_NAME : this.name;
    }

    @Override
    public boolean hasProperty(InventoryProperty<?, ?> property) {
        checkNotNull(property, "property");
        final PropertyKey propertyKey = new PropertyKey(property.getClass(), property.getKey());
        final InventoryProperty property1 = this.inventoryPropertiesByKey.get(propertyKey);
        if (property1 != null && property1.equals(property)) {
            return true;
        }
        final IInventory parent = this.parent();
        if (parent != this && parent instanceof InventoryBase) {
            //noinspection unchecked
            final Optional<InventoryProperty<?, ?>> optProperty = ((InventoryBase) parent).tryGetProperty(
                    this, (Class) property.getClass(), property.getKey());
            return optProperty.isPresent() && optProperty.get().equals(property);
        }
        return false;
    }

    @Override
    public boolean hasProperty(Inventory child, InventoryProperty<?,?> property) {
        checkNotNull(property, "property");
        if (!(child instanceof InventoryBase)) {
            return false;
        }
        final PropertyKey propertyKey = new PropertyKey(property.getClass(), property.getKey());
        final InventoryProperty property1 = ((InventoryBase) child).inventoryPropertiesByKey.get(propertyKey);
        if (property1 != null && property1.equals(property)) {
            return true;
        }
        //noinspection unchecked
        final Optional<InventoryProperty<?, ?>> optProperty = this.tryGetProperty(
                child, (Class) property.getClass(), property.getKey());
        return optProperty.isPresent() && optProperty.get().equals(property);
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Collection<T> getProperties(Class<T> property) {
        checkNotNull(property, "property");
        final IInventory parent = this.parent();
        final ImmutableList.Builder<T> properties = ImmutableList.builder();
        //noinspection unchecked
        properties.addAll((Collection<? extends T>) this.inventoryPropertiesByClass.get(property));
        if (parent != this && parent instanceof InventoryBase) {
            properties.addAll(((InventoryBase) parent).tryGetProperties(this, property));
        }
        return properties.build();
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Collection<T> getProperties(Inventory child, Class<T> property) {
        checkNotNull(property, "property");
        if (!(child instanceof InventoryBase)) {
            return ImmutableList.of();
        }
        final ImmutableList.Builder<T> properties = ImmutableList.builder();
        //noinspection unchecked
        properties.addAll((Collection<? extends T>) ((InventoryBase) child).inventoryPropertiesByClass.get(property));
        properties.addAll(this.tryGetProperties(child, property));
        return properties.build();
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Class<T> property, @Nullable Object key) {
        checkNotNull(property, "property");
        final PropertyKey propertyKey = new PropertyKey(property, key);
        final InventoryProperty<?, ?> property1 = this.inventoryPropertiesByKey.get(propertyKey);
        if (property1 != null && property.isInstance(property1)) {
            return Optional.of(property.cast(property1));
        }
        final IInventory parent = this.parent();
        if (parent != this && parent instanceof InventoryBase) {
            return ((InventoryBase) parent).tryGetProperty(this, property, key);
        }
        return Optional.empty();
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Inventory child, Class<T> property, @Nullable Object key) {
        checkNotNull(child, "child");
        checkNotNull(property, "property");
        if (!(child instanceof InventoryBase)) {
            return Optional.empty();
        }
        final InventoryBase inventoryBase = (InventoryBase) child;
        final PropertyKey propertyKey = new PropertyKey(property, key);
        final InventoryProperty<?, ?> property1 = inventoryBase.inventoryPropertiesByKey.get(propertyKey);
        if (property1 != null && property.isInstance(property1)) {
            return Optional.of(property.cast(property1));
        }
        return this.tryGetProperty(child, property, key);
    }

    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Inventory child, Class<T> property, @Nullable Object key) {
        return Optional.empty();
    }

    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Inventory child, Class<T> property) {
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(ItemType... types) {
        checkNotNull(types, "types");
        return this.query(inventory -> {
            // Slots are leaf nodes so only check if they contain
            // the item
            if (inventory instanceof Slot) {
                for (ItemType type : types) {
                    if (inventory.contains(type)) {
                        return true;
                    }
                }
            }
            return false;
        }, true);
    }

    @Override
    public <T extends Inventory> T query(Class<?>... types) {
        checkNotNull(types, "types");
        return this.query(inventory -> {
            for (Class<?> type : types) {
                if (type.isInstance(inventory)) {
                    return true;
                }
            }
            return false;
        }, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(ItemStack... types) {
        checkNotNull(types, "types");
        return this.query(inventory -> {
            // Slots are leaf nodes so only check if they contain
            // the item
            if (inventory instanceof Slot) {
                for (ItemStack type : types) {
                    if (inventory.contains(type)) {
                        return true;
                    }
                }
            }
            return false;
        }, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(InventoryProperty<?, ?>... props) {
        checkNotNull(props, "props");
        return this.query(inventory -> {
            for (InventoryProperty<?,?> prop : props) {
                if (((InventoryBase) inventory).hasProperty(prop)) {
                    return true;
                }
            }
            return false;
        }, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(Translation... names) {
        checkNotNull(names, "names");
        return this.query(inventory -> {
            for (Translation name : names) {
                if (inventory.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }, false);
    }

    @Override
    public <T extends Inventory> T query(String... names) {
        checkNotNull(names, "names");
        return this.query(inventory -> {
            String plainName = inventory.getName().get();
            for (String name : names) {
                if (plainName.equals(name)) {
                    return true;
                }
            }
            return false;
        }, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(Object... args) {
        checkNotNull(args, "args");
        return this.query(inventory -> {
            for (Object arg : args) {
                if (inventory.equals(arg)) {
                    return true;
                }
            }
            return false;
        }, false);
    }

    @Override
    public void add(IViewerListener listener) {
        this.viewerListeners.add(checkNotNull(listener, "listener"));
    }

    protected void addViewer(Viewer viewer, LanternContainer container) {
        this.viewerListeners.forEach(listener -> listener.onViewerAdded(viewer, container));
    }

    protected void removeViewer(Viewer viewer, LanternContainer container) {
        this.viewerListeners.forEach(listener -> listener.onViewerRemoved(viewer, container));
    }
}
