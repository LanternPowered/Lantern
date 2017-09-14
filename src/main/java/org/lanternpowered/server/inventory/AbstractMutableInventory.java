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
import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.lanternpowered.server.event.CauseStack;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.equipment.LanternEquipmentType;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.property.ArmorSlotType;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractMutableInventory extends AbstractInventory {

    static class NameHolder {

        static final Translation EMPTY = tr("inventory.empty.title");
        static final Translation DEFAULT = tr("inventory.name");
    }

    private final LanternEmptyInventory emptyInventory = new LanternEmptyInventory(this);

    /**
     * The plugin container that created this {@link AbstractMutableInventory}.
     */
    @Nullable private PluginContainer pluginContainer;

    @Nullable private final Inventory parent;
    @Nullable private Translation name;

    /**
     * All the {@link InventoryProperty}s of this inventory mapped by their type.
     */
    private final Multimap<Class<?>, InventoryProperty<?,?>> inventoryPropertiesByClass = HashMultimap.create();

    /**
     * All the {@link InventoryProperty}s of this inventory mapped by their key.
     */
    private final Map<InventoryPropertyKey, InventoryProperty<?,?>> inventoryPropertiesByKey = new HashMap<>();

    private final Set<ContainerViewListener> viewerListeners = new HashSet<>();
    final Set<InventoryCloseListener> closeListeners = new HashSet<>();

    public AbstractMutableInventory(@Nullable Inventory parent, @Nullable Translation name) {
        this.parent = parent;
        this.name = name;
    }

    void setName(@Nullable Translation name) {
        this.name = name;
    }

    void setPluginContainer(@Nullable PluginContainer pluginContainer) {
        this.pluginContainer = pluginContainer;
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
    protected LanternEmptyInventory empty() {
        return this.emptyInventory;
    }

    @Override
    public PluginContainer getPlugin() {
        return this.pluginContainer == null ? Lantern.getMinecraftPlugin() : this.pluginContainer;
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
    public AbstractInventory parent() {
        return this.parent == null ? this : (AbstractInventory) this.parent;
    }

    @Override
    public Translation getName() {
        return this.name == null ? NameHolder.DEFAULT : this.name;
    }

    @Override
    public boolean hasProperty(Class<? extends InventoryProperty<?, ?>> property) {
        if (this.inventoryPropertiesByClass.containsKey(checkNotNull(property, "property"))) {
            return true;
        }
        return super.hasProperty(property);
    }

    @Override
    public boolean hasProperty(InventoryProperty<?, ?> property) {
        checkNotNull(property, "property");
        final InventoryPropertyKey propertyKey = new InventoryPropertyKey(property.getClass(), property.getKey());
        final InventoryProperty property1 = this.inventoryPropertiesByKey.get(propertyKey);
        if (property1 != null && property1.equals(property)) {
            return true;
        }
        return super.hasProperty(property);
    }

    @Override
    public boolean hasProperty(Inventory child, InventoryProperty<?,?> property) {
        checkNotNull(property, "property");
        if (child instanceof AbstractMutableInventory) {
            final InventoryPropertyKey propertyKey = new InventoryPropertyKey(property.getClass(), property.getKey());
            final InventoryProperty property1 = ((AbstractMutableInventory) child).inventoryPropertiesByKey.get(propertyKey);
            if (property1 != null && property1.equals(property)) {
                return true;
            }
        }
        return super.hasProperty(child, property);
    }

    @Override
    <T extends InventoryProperty<?, ?>> ImmutableList.Builder<T> getPropertiesBuilder(Class<T> property) {
        final ImmutableList.Builder<T> builder = super.getPropertiesBuilder(property);
        builder.addAll((Collection<? extends T>) this.inventoryPropertiesByClass.get(property));
        return builder;
    }

    @Override
    <T extends InventoryProperty<?, ?>> ImmutableList.Builder<T> getPropertiesBuilder(Inventory child, Class<T> property) {
        final ImmutableList.Builder<T> builder = super.getPropertiesBuilder(child, property);
        if (child instanceof AbstractMutableInventory) {
            builder.addAll((Collection<? extends T>) ((AbstractMutableInventory) child).inventoryPropertiesByClass.get(property));
        }
        return builder;
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Class<T> property, @Nullable Object key) {
        checkNotNull(property, "property");
        final InventoryPropertyKey propertyKey = new InventoryPropertyKey(property, key);
        final InventoryProperty<?, ?> property1 = this.inventoryPropertiesByKey.get(propertyKey);
        if (property1 != null && property.isInstance(property1)) {
            return Optional.of(property.cast(property1));
        }
        return super.getProperty(property, key);
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Inventory child, Class<T> property, @Nullable Object key) {
        checkNotNull(child, "child");
        checkNotNull(property, "property");
        final InventoryPropertyKey propertyKey = new InventoryPropertyKey(property, key);
        if (child instanceof AbstractMutableInventory) {
            final InventoryProperty<?, ?> property1 = ((AbstractMutableInventory) child).inventoryPropertiesByKey.get(propertyKey);
            if (property1 != null && property.isInstance(property1)) {
                return Optional.of(property.cast(property1));
            }
        }
        return super.getProperty(child, property, key);
    }

    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Class<T> property, @Nullable Object key) {
        if (property == InventoryTitle.class) {
            return Optional.of((T) new InventoryTitle(Text.of(getName())));
        } else if (property == InventoryCapacity.class) {
            return Optional.of((T) new InventoryCapacity(capacity()));
        }
        return Optional.empty();
    }

    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Class<T> property) {
        final List<T> properties = new ArrayList<>();
        if (property == InventoryTitle.class) {
            properties.add((T) new InventoryTitle(Text.of(getName())));
        } else if (property == InventoryCapacity.class) {
            properties.add((T) new InventoryCapacity(capacity()));
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
        return query(inventory -> {
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
        return query(inventory -> {
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
        return query(inventory -> {
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
    public <T extends Inventory> T queryAny(ItemStack... types) {
        checkNotNull(types, "types");
        return query(inventory -> {
            // Slots are leaf nodes so only check if they contain
            // the item
            if (inventory instanceof Slot) {
                for (ItemStack type : types) {
                    if (inventory.containsAny(type)) {
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
        return query(inventory -> {
            for (InventoryProperty<?,?> prop : props) {
                if (((AbstractInventory) inventory).hasProperty(prop)) {
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
        return query(inventory -> {
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
        return query(inventory -> {
            String plainName = inventory.getName().get();
            for (String name : names) {
                if (plainName.equals(name)) {
                    return true;
                }
            }
            return false;
        }, false);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    public <T extends Inventory> T query(Object... args) {
        checkNotNull(args, "args");
        // This madness, trying to
        // cover all the cases
        return query(inventory -> {
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
                } else if (arg instanceof EquipmentType) {
                    for (EquipmentSlotType property : Iterables.concat(
                            inventory.getProperties(EquipmentSlotType.class),
                            inventory.getProperties(ArmorSlotType.class))) {
                        if (((LanternEquipmentType) arg).isChild(property.getValue())) {
                            return true;
                        }
                    }
                } else if (arg instanceof EquipmentSlotType) {
                    for (EquipmentSlotType property : Iterables.concat(
                            inventory.getProperties(EquipmentSlotType.class),
                            inventory.getProperties(ArmorSlotType.class))) {
                        if (((LanternEquipmentType) ((EquipmentSlotType) arg).getValue()).isChild(property.getValue())) {
                            return true;
                        }
                    }
                } else if (arg instanceof InventoryProperty<?,?>) {
                    if (((AbstractInventory) inventory).hasProperty((InventoryProperty<?, ?>) arg)) {
                        return true;
                    }
                } else if (arg instanceof Class<?>) {
                    final Class<?> clazz = (Class<?>) arg;
                    if (InventoryProperty.class.isAssignableFrom(clazz)) {
                        if (((AbstractInventory) inventory).hasProperty((Class<? extends InventoryProperty<?, ?>>) clazz)) {
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
    public void addViewListener(ContainerViewListener listener) {
        checkNotNull(listener, "listener");
        this.viewerListeners.add(listener);
    }

    @Override
    protected void addViewer(Viewer viewer, LanternContainer container) {
        this.viewerListeners.removeIf(listener ->
                listener.onViewerAdded(viewer, container) == ContainerViewListener.Result.REMOVE_LISTENER);
    }

    @Override
    protected void removeViewer(Viewer viewer, LanternContainer container) {
        this.viewerListeners.removeIf(listener ->
                listener.onViewerRemoved(viewer, container) == ContainerViewListener.Result.REMOVE_LISTENER);
    }

    @Override
    public void addCloseListener(InventoryCloseListener closeListener) {
        this.closeListeners.add(checkNotNull(closeListener, "closeListener"));
    }

    @Override
    void close() {
        final CauseStack causeStack = CauseStack.current();
        causeStack.pushCause(this);
        for (InventoryCloseListener listener : this.closeListeners) {
            listener.onClose(this);
        }
        causeStack.popCause();
    }
}
