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
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.behavior.ShiftClickBehavior;
import org.lanternpowered.server.inventory.constructor.InventoryConstructor;
import org.lanternpowered.server.inventory.constructor.InventoryConstructorFactory;
import org.lanternpowered.server.text.translation.TextTranslation;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.InventoryProperties;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.translation.Translation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public abstract class AbstractBuilder<R extends T, T extends AbstractInventory, B extends AbstractBuilder<R, T, B>> {

    @Nullable protected InventoryConstructor<R> constructor;
    protected final Map<Property<?>, Object> properties = new HashMap<>();
    @Nullable protected Map<Property<?>, Object> cachedProperties;

    // Catalog properties
    @Nullable protected PluginContainer pluginContainer;
    @Nullable protected Translation translation;

    @Nullable protected ShiftClickBehavior shiftClickBehavior;

    private boolean viewable;

    protected Map<Property<?>, Object> getProperties() {
        if (this.cachedProperties == null) {
            this.cachedProperties = ImmutableMap.copyOf(this.properties);
        }
        return this.cachedProperties;
    }

    /**
     * Sets the {@link Supplier} for the {@link AbstractInventory}, the
     * supplied slot may not be initialized yet.
     *
     * @param inventoryType The inventory type
     * @param <N> The inventory type
     * @return This builder, for chaining
     */
    public <N extends T> AbstractBuilder<N, T, ?> type(Class<N> inventoryType) {
        checkNotNull(inventoryType, "inventoryType");
        this.constructor = InventoryConstructorFactory.get().getConstructor((Class<R>) inventoryType);
        return (AbstractBuilder<N, T, ?>) this;
    }

    /**
     * Sets the {@link ShiftClickBehavior} of the {@link AbstractInventory}. This behavior
     * will only be used if this is the directly the top inventory.
     *
     * @param shiftClickBehavior The shift click behavior
     * @return This builder, for chaining
     */
    public B shiftClickBehavior(ShiftClickBehavior shiftClickBehavior) {
        checkNotNull(shiftClickBehavior, "shiftClickBehavior");
        this.shiftClickBehavior = shiftClickBehavior;
        return (B) this;
    }

    /**
     * Adds the provided {@link Property} and value.
     *
     * @param property The property
     * @return This builder, for chaining
     */
    public <V> B property(Property<V> property, V value) {
        checkNotNull(property, "property");
        // checkState(!(property instanceof InventoryCapacity), "The inventory capacity cannot be modified with a property.");
        // checkState(!(property instanceof InventoryDimension), "The inventory dimension cannot be modified with a property.");
        if (property == InventoryProperties.DIMENSION ||
                property == InventoryProperties.CAPACITY) {
            return (B) this;
        }
        // All inventories with this property are viewable
        if (property == InventoryProperties.GUI_ID) {
            this.viewable = true;
        }
        putProperty(property, value);
        if (property == InventoryProperties.TITLE) {
            this.translation = TextTranslation.of((Text) value);
        }
        return (B) this;
    }

    /**
     * Sets the title {@link Translation}.
     *
     * @param translation The title translation
     * @return This builder, for chaining
     */
    public B title(Translation translation) {
        checkNotNull(translation, "translation");
        this.translation = translation;
        putProperty(InventoryProperties.TITLE, TextTranslation.toText(translation));
        return (B) this;
    }

    private <V> void putProperty(Property<V> property, V value) {
        this.properties.put(property, value);
        this.cachedProperties = null;
    }

    /**
     * Sets the plugin that provides the {@link LanternInventoryArchetype}.
     *
     * @param plugin The plugin instance
     * @return This builder, for chaining
     */
    public B plugin(Object plugin) {
        this.pluginContainer = checkPlugin(plugin, "plugin");
        return (B) this;
    }

    /**
     * Constructs a {@link AbstractInventory}.
     *
     * @return The inventory
     */
    public R build() {
        return build0(false, this.pluginContainer == null ? Lantern.getImplementationPlugin() : this.pluginContainer, null);
    }

    /**
     * Constructs a {@link AbstractInventory} and sets the plugin
     * instance that constructed the inventory.
     *
     * @param plugin The plugin
     * @return The inventory
     */
    public R build(Object plugin) {
        return build(false, plugin, null);
    }

    R build(boolean carried, Object plugin, @Nullable LanternInventoryArchetype<R> archetype) {
        return build0(carried, checkPlugin(plugin, "plugin"), archetype);
    }

    R build0(boolean carried, @Nullable PluginContainer plugin, @Nullable LanternInventoryArchetype<R> archetype) {
        checkState(this.constructor != null, "Inventory constructor must be set.");
        if (plugin == null) {
            plugin = this.pluginContainer == null ? Lantern.getImplementationPlugin() : this.pluginContainer;
        }
        int flags = 0;
        if (carried) {
            flags |= InventoryConstructor.CARRIED;
        }
        if (this.viewable) {
            flags |= InventoryConstructor.VIEWABLE;
        }
        final R inventory = this.constructor.construct(flags);
        if (inventory instanceof AbstractMutableInventory) {
            final AbstractMutableInventory mutableInventory = (AbstractMutableInventory) inventory;
            mutableInventory.setPlugin(plugin);
            Map<Property<?>, Object> properties = getProperties();
            if (!properties.containsKey(InventoryProperties.UNIQUE_ID)) {
                final ImmutableMap.Builder<Property<?>, Object> builder = ImmutableMap.builder();
                builder.putAll(properties);
                builder.put(InventoryProperties.UNIQUE_ID, UUID.randomUUID());
                properties = builder.build();
            }
            mutableInventory.setProperties(properties);
        }
        if (this.translation != null) {
            inventory.setName(this.translation);
        }
        try {
            build(inventory);
        } catch (Exception e) {
            try {
                if (archetype == null) {
                    archetype = (LanternInventoryArchetype<R>) inventory.getArchetype();
                }
                if (archetype == InventoryArchetypes.UNKNOWN) {
                    throw e;
                }
                final CatalogKey key = archetype.getKey();
                final String builderType = archetype.getBuilder().getClass().getName();
                throw new RuntimeException("An error occurred while constructing " + key + " with builder type " + builderType, e);
            } catch (Exception e1) {
                throw e;
            }
        }
        if (inventory instanceof AbstractMutableInventory) {
            final AbstractMutableInventory mutableInventory = (AbstractMutableInventory) inventory;
            if (archetype != null) {
                mutableInventory.setArchetype(archetype);
            } else if (this instanceof AbstractArchetypeBuilder) {
                final String pluginId = (this.pluginContainer == null ? Lantern.getImplementationPlugin() : this.pluginContainer).getId();
                mutableInventory.setArchetype(((AbstractArchetypeBuilder) this)
                        .buildArchetype(CatalogKey.of(pluginId, UUID.randomUUID().toString())));
            }
            if (this.shiftClickBehavior != null) {
                mutableInventory.setShiftClickBehavior(this.shiftClickBehavior);
            }
        }
        return inventory;
    }

    /**
     * Initializes the build {@link AbstractInventory}.
     *
     * @param inventory The inventory
     */
    protected abstract void build(R inventory);
}
