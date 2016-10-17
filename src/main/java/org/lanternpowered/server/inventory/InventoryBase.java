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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryCapacity;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
    private final Map<InventoryPropertyKey, InventoryProperty<?,?>> inventoryPropertiesByKey = new HashMap<>();

    private final Set<IViewerListener> viewerListeners = new HashSet<>();

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
        final InventoryPropertyKey propertyKey = new InventoryPropertyKey(inventoryProperty.getClass(), inventoryProperty.getKey());
        this.inventoryPropertiesByKey.put(propertyKey, inventoryProperty);
    }

    @Override
    public PluginContainer getPlugin() {
        return null; // TODO
    }

    @Override
    public InventoryArchetype getArchetype() {
        return null; // TODO
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
    public boolean hasProperty(Class<? extends InventoryProperty<?, ?>> property) {
        return this.inventoryPropertiesByClass.containsKey(checkNotNull(property, "property"));
    }

    @Override
    public boolean hasProperty(InventoryProperty<?, ?> property) {
        checkNotNull(property, "property");
        final InventoryPropertyKey propertyKey = new InventoryPropertyKey(property.getClass(), property.getKey());
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
        final InventoryPropertyKey propertyKey = new InventoryPropertyKey(property.getClass(), property.getKey());
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
        properties.addAll(this.tryGetProperties(property));
        return properties.build();
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Class<T> property, @Nullable Object key) {
        checkNotNull(property, "property");
        final InventoryPropertyKey propertyKey = new InventoryPropertyKey(property, key);
        final InventoryProperty<?, ?> property1 = this.inventoryPropertiesByKey.get(propertyKey);
        if (property1 != null && property.isInstance(property1)) {
            return Optional.of(property.cast(property1));
        }
        final IInventory parent = this.parent();
        if (parent != this && parent instanceof InventoryBase) {
            return ((InventoryBase) parent).tryGetProperty(this, property, key);
        }
        return this.tryGetProperty(property, key);
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Inventory child, Class<T> property, @Nullable Object key) {
        checkNotNull(child, "child");
        checkNotNull(property, "property");
        if (!(child instanceof InventoryBase)) {
            return Optional.empty();
        }
        final InventoryBase inventoryBase = (InventoryBase) child;
        final InventoryPropertyKey propertyKey = new InventoryPropertyKey(property, key);
        final InventoryProperty<?, ?> property1 = inventoryBase.inventoryPropertiesByKey.get(propertyKey);
        if (property1 != null && property.isInstance(property1)) {
            return Optional.of(property.cast(property1));
        }
        return this.tryGetProperty(child, property, key);
    }

    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Class<T> property, @Nullable Object key) {
        if (property == InventoryTitle.class) {
            //noinspection unchecked
            return Optional.of((T) new InventoryTitle(Text.of(this.getName())));
        } else if (property == InventoryCapacity.class) {
            //noinspection unchecked
            return Optional.of((T) new InventoryCapacity(this.capacity()));
        }
        return Optional.empty();
    }

    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Class<T> property) {
        final List<T> properties = new ArrayList<>();
        if (property == InventoryTitle.class) {
            //noinspection unchecked
            properties.add((T) new InventoryTitle(Text.of(this.getName())));
        } else if (property == InventoryCapacity.class) {
            //noinspection unchecked
            properties.add((T) new InventoryCapacity(this.capacity()));
        }
        return properties;
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
                if (((IInventory) inventory).hasProperty(prop)) {
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
        // This madness, trying to
        // cover all the cases
        return this.query(inventory -> {
            for (Object arg : args) {
                if (arg instanceof Inventory) {
                    if (inventory.equals(arg)) {
                        return true;
                    }
                } else if (arg instanceof InventoryArchetype) {
                    if (inventory.getArchetype().equals(arg)) {
                        return true;
                    }
                } else if (arg instanceof ItemStack) {
                    if (inventory.contains((ItemStack) arg)) {
                        return true;
                    }
                } else if (arg instanceof Translation) {
                    if (inventory.getName().equals(arg)) {
                        return true;
                    }
                } else if (arg instanceof ItemType) {
                    if (inventory.contains((ItemType) arg)) {
                        return true;
                    }
                } else if (arg instanceof InventoryProperty<?,?>) {
                    if (((IInventory) inventory).hasProperty((InventoryProperty<?, ?>) arg)) {
                        return true;
                    }
                } else if (arg instanceof Class<?>) {
                    final Class<?> clazz = (Class<?>) arg;
                    if (InventoryProperty.class.isAssignableFrom(clazz)) {
                        if (((IInventory) inventory).hasProperty((Class<? extends InventoryProperty<?, ?>>) clazz)) {
                            return true;
                        }
                    } else if (Inventory.class.isAssignableFrom(clazz)) {
                        if (clazz.isInstance(inventory)) {
                            return true;
                        }
                    }
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
        final Iterator<IViewerListener> it = this.viewerListeners.iterator();
        while (it.hasNext()) {
            final IViewerListener listener = it.next();
            if (listener.onViewerAdded(viewer, container) == IViewerListener.Result.REMOVE_LISTENER) {
                it.remove();
            }
        }
    }

    protected void removeViewer(Viewer viewer, LanternContainer container) {
        final Iterator<IViewerListener> it = this.viewerListeners.iterator();
        while (it.hasNext()) {
            final IViewerListener listener = it.next();
            if (listener.onViewerRemoved(viewer, container) == IViewerListener.Result.REMOVE_LISTENER) {
                it.remove();
            }
        }
    }
}
